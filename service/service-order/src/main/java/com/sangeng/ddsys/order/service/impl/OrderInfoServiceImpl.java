package com.sangeng.ddsys.order.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sangeng.ddsys.activity.ActivityFeignClient;
import com.sangeng.ddsys.client.cart.CartFeignClient;
import com.sangeng.ddsys.client.product.ProductFeignClient;
import com.sangeng.ddsys.client.user.UserFeignClient;
import com.sangeng.ddsys.common.auth.AuthContextHolder;
import com.sangeng.ddsys.common.constant.RedisConst;
import com.sangeng.ddsys.common.exception.DdsysException;
import com.sangeng.ddsys.common.result.ResultCodeEnum;
import com.sangeng.ddsys.common.utils.DateUtil;
import com.sangeng.ddsys.enums.*;
import com.sangeng.ddsys.model.activity.ActivityRule;
import com.sangeng.ddsys.model.activity.CouponInfo;
import com.sangeng.ddsys.model.order.CartInfo;
import com.sangeng.ddsys.model.order.OrderInfo;
import com.sangeng.ddsys.model.order.OrderItem;
import com.sangeng.ddsys.mq.constant.MqConst;
import com.sangeng.ddsys.mq.service.RabbitService;
import com.sangeng.ddsys.order.mapper.OrderInfoMapper;
import com.sangeng.ddsys.order.service.OrderInfoService;
import com.sangeng.ddsys.order.service.OrderItemService;
import com.sangeng.ddsys.vo.order.CartInfoVo;
import com.sangeng.ddsys.vo.order.OrderConfirmVo;
import com.sangeng.ddsys.vo.order.OrderSubmitVo;
import com.sangeng.ddsys.vo.product.SkuStockLockVo;
import com.sangeng.ddsys.vo.user.LeaderAddressVo;

/**
 * <p>
 * 订单 服务实现类
 * </p>
 *
 * @author calos
 * @since 2023-11-17
 */
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {
    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private CartFeignClient cartFeignClient;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ActivityFeignClient activityFeignClient;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private RabbitService rabbitService;

    @Override
    public OrderConfirmVo confirmOrder() {
        // 获取到用户Id
        Long userId = AuthContextHolder.getUserId();

        // 获取用户地址
        LeaderAddressVo leaderAddressVo = userFeignClient.getUserAddressByUserId(userId);

        // 先得到用户想要购买的商品！
        List<CartInfo> cartInfoList = cartFeignClient.getCartCheckedList(userId);

        // 防重：生成一个唯一标识，保存到redis中一份
        String orderNo = System.currentTimeMillis() + "";// IdWorker.getTimeId();
        redisTemplate.opsForValue().set(RedisConst.ORDER_REPEAT + orderNo, orderNo, 24, TimeUnit.HOURS);
        // 获取购物车满足条件的促销与优惠券信息
        OrderConfirmVo orderTradeVo = activityFeignClient.findCartActivityAndCoupon(cartInfoList, userId);
        orderTradeVo.setLeaderAddressVo(leaderAddressVo);
        orderTradeVo.setOrderNo(orderNo);
        return orderTradeVo;
    }

    @Override
    public Long submitOrder(OrderSubmitVo orderParamVo) {
        // 第一步 设置给哪个用户生成订单，设置orderParamVo的userId
        Long userId = AuthContextHolder.getUserId();
        // 第二步 订单不能重复提交，重复提交验证
        // 通过Redis+lua脚本进行判断，Lua脚本保证原子性操作
        // 1 获取传递过来的订单 orderNo
        String orderNo = orderParamVo.getOrderNo();
        if (StringUtils.isEmpty(orderNo)) {
            throw new DdsysException(ResultCodeEnum.ILLEGAL_REQUEST);
        }
        // 2 拿着orderNo到redis中查询
        // 3 如果redis中有相同orderNo，表示正常提交订单，然后把redis中的orderNo删除
        String script =
            "if(redis.call('get', KEYS[1]) == ARGV[1]) then return redis.call('del', KEYS[1]) else return 0 end";
        Boolean flag = (Boolean)redisTemplate.execute(new DefaultRedisScript(script, Boolean.class),
            Collections.singletonList(RedisConst.ORDER_REPEAT + orderNo), orderNo);
        // 4 如果redis没有相同orderNo，表示重复提交，不能往后进行
        if (Boolean.FALSE.equals(flag)) {
            throw new DdsysException(ResultCodeEnum.REPEAT_SUBMIT);
        }

        // 第三步 验证库存，并且锁定库存
        // 比如仓库有10个西红柿，我想买2个西红杯
        // ** 验证库存，查询仓库里面是是否有充足西红柿
        // ** 库存充足，库存锁定2（目前没有真正减库存）
        // 1、远程调用service-cart模块，获取当前用户购物车商品（选中的购物项）
        List<CartInfo> cartInfoList = cartFeignClient.getCartCheckedList(userId);
        // 2、购物车有很多商品，商品不同类型，重点处理普通类型商品
        List<CartInfo> commonSkuList =
            cartInfoList.stream().filter(cartInfo -> Objects.equals(cartInfo.getSkuType(), SkuType.COMMON.getCode()))
                .collect(Collectors.toList());
        // 3、把获取购物车里面普通类型商品list集合，转换List<SkuStockLockVo>
        if (!CollectionUtils.isEmpty(commonSkuList)) {
            List<SkuStockLockVo> commonStockLockVoList = commonSkuList.stream().map(item -> {
                SkuStockLockVo skuStockLockVo = new SkuStockLockVo();
                skuStockLockVo.setSkuId(item.getSkuId());
                skuStockLockVo.setSkuNum(item.getSkuNum());
                return skuStockLockVo;
            }).collect(Collectors.toList());
            // 4、远程调用service-product模块锁定商品，验证库存并锁定，保证具备原子性
            Boolean isLockSuccess = productFeignClient.checkAndLock(commonStockLockVoList, orderNo);
            if (!isLockSuccess) {
                throw new DdsysException(ResultCodeEnum.ORDER_STOCK_FALL);
            }
        }
        /*
        //2.2秒杀商品
        List<CartInfo> seckillSkuList =
            cartInfoList.stream().filter(cartInfo -> Objects.equals(cartInfo.getSkuType(),
                    SkuType.SECKILL.getCode()))
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(seckillSkuList)) {
            List<SkuStockLockVo> seckillStockLockVoList = seckillSkuList.stream().map(item -> {
                SkuStockLockVo skuStockLockVo = new SkuStockLockVo();
                skuStockLockVo.setSkuId(item.getSkuId());
                skuStockLockVo.setSkuNum(item.getSkuNum());
                return skuStockLockVo;
            }).collect(Collectors.toList());
            //是否锁定
            Boolean isLockSeckill =
                seckillFeignClient.checkAndMinusStock(seckillStockLockVoList, orderNo);
            if (!isLockSeckill) {
                throw new DdsysException(ResultCodeEnum.ORDER_STOCK_FALL);
            }
        }*/
        // 第四步 下单过程，order_info和order_item
        Long orderId = this.saveOrder(orderParamVo, cartInfoList);

        // 下单完成之后，删除购物车记录
        // 异步删除购物车中对应的记录。不应该影响下单的整体流程
        rabbitService.sendMessage(MqConst.EXCHANGE_ORDER_DIRECT, MqConst.ROUTING_DELETE_CART, orderParamVo.getUserId());
        return orderId;
    }

    @Transactional(rollbackFor = {Exception.class})
    public Long saveOrder(OrderSubmitVo orderParamVo, List<CartInfo> cartInfoList) {
        if (CollectionUtils.isEmpty(cartInfoList)) {
            throw new DdsysException(ResultCodeEnum.DATA_ERROR);
        }
        // 查询用户提货点和团长信息
        Long userId = AuthContextHolder.getUserId();
        LeaderAddressVo leaderAddressVo = userFeignClient.getUserAddressByUserId(userId);
        if (leaderAddressVo == null) {
            throw new DdsysException(ResultCodeEnum.DATA_ERROR);
        }
        // 计算金额
        // 营销活动金额
        Map<String, BigDecimal> activitySplitAmountMap = this.computeActivitySplitAmount(cartInfoList);
        // 优惠券金额
        Map<String, BigDecimal> couponInfoSplitAmountMap =
            this.computeCouponInfoSplitAmount(cartInfoList, orderParamVo.getCouponId());
        // 封装订单项数据
        List<OrderItem> orderItemList = new ArrayList<>();
        for (CartInfo cartInfo : cartInfoList) {
            OrderItem orderItem = new OrderItem();
            orderItem.setId(null);
            orderItem.setCategoryId(cartInfo.getCategoryId());
            if (Objects.equals(cartInfo.getSkuType(), SkuType.COMMON.getCode())) {
                orderItem.setSkuType(SkuType.COMMON);
            } else {
                orderItem.setSkuType(SkuType.SECKILL);
            }
            orderItem.setSkuId(cartInfo.getSkuId());
            orderItem.setSkuName(cartInfo.getSkuName());
            orderItem.setSkuPrice(cartInfo.getCartPrice());
            orderItem.setImgUrl(cartInfo.getImgUrl());
            orderItem.setSkuNum(cartInfo.getSkuNum());
            orderItem.setLeaderId(orderParamVo.getLeaderId());
            // 促销活动分摊金额
            BigDecimal splitActivityAmount = activitySplitAmountMap.get("activity:" + orderItem.getSkuId());
            if (null == splitActivityAmount) {
                splitActivityAmount = new BigDecimal(0);
            }
            orderItem.setSplitActivityAmount(splitActivityAmount);

            // 优惠券分摊金额
            BigDecimal splitCouponAmount = couponInfoSplitAmountMap.get("coupon:" + orderItem.getSkuId());
            if (null == splitCouponAmount) {
                splitCouponAmount = new BigDecimal(0);
            }
            orderItem.setSplitCouponAmount(splitCouponAmount);
            // 优惠前总金额
            BigDecimal skuTotalAmount = orderItem.getSkuPrice().multiply(new BigDecimal(orderItem.getSkuNum()));
            // 优惠后的总金额
            BigDecimal splitTotalAmount = skuTotalAmount.subtract(splitActivityAmount).subtract(splitCouponAmount);
            orderItem.setSplitTotalAmount(splitTotalAmount);
            orderItemList.add(orderItem);
        }
        // 封装订单OrderInfo数据
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setUserId(userId);
        orderInfo.setOrderNo(orderParamVo.getOrderNo());
        orderInfo.setOrderStatus(OrderStatus.UNPAID);
        orderInfo.setProcessStatus(ProcessStatus.UNPAID);
        orderInfo.setCouponId(orderParamVo.getCouponId());
        orderInfo.setLeaderId(orderParamVo.getLeaderId());
        orderInfo.setLeaderName(leaderAddressVo.getLeaderName());
        orderInfo.setLeaderPhone(leaderAddressVo.getLeaderPhone());
        orderInfo.setTakeName(leaderAddressVo.getTakeName());
        orderInfo.setReceiverName(orderParamVo.getReceiverName());
        orderInfo.setReceiverPhone(orderParamVo.getReceiverPhone());
        orderInfo.setReceiverProvince(leaderAddressVo.getProvince());
        orderInfo.setReceiverCity(leaderAddressVo.getCity());
        orderInfo.setReceiverDistrict(leaderAddressVo.getDistrict());
        orderInfo.setReceiverAddress(leaderAddressVo.getDetailAddress());
        orderInfo.setWareId(cartInfoList.get(0).getWareId());

        // 计算订单金额
        BigDecimal originalTotalAmount = this.computeTotalAmount(cartInfoList);
        BigDecimal activityAmount = activitySplitAmountMap.get("activity:total");
        if (null == activityAmount) {
            activityAmount = new BigDecimal(0);
        }
        BigDecimal couponAmount = couponInfoSplitAmountMap.get("coupon:total");
        if (null == couponAmount) {
            couponAmount = new BigDecimal(0);
        }
        BigDecimal totalAmount = originalTotalAmount.subtract(activityAmount).subtract(couponAmount);
        // 计算订单金额
        orderInfo.setOriginalTotalAmount(originalTotalAmount);
        orderInfo.setActivityAmount(activityAmount);
        orderInfo.setCouponAmount(couponAmount);
        orderInfo.setTotalAmount(totalAmount);

        // 计算团长佣金
        BigDecimal profitRate = new BigDecimal(0);
        BigDecimal commissionAmount = orderInfo.getTotalAmount().multiply(profitRate);
        orderInfo.setCommissionAmount(commissionAmount);

        baseMapper.insert(orderInfo);

        // 保存订单项
        for (OrderItem orderItem : orderItemList) {
            orderItem.setOrderId(orderInfo.getId());
        }
        orderItemService.saveBatch(orderItemList);

        // 更新优惠券使用状态
        if (null != orderInfo.getCouponId()) {
            activityFeignClient.updateCouponInfoUseStatus(orderInfo.getCouponId(), userId, orderInfo.getId());
        }
        // 下单成功，记录用户购物商品数量，redis
        // hash类型，key(userId) - field(skuId) - value(skuNum)
        String orderSkuKey = RedisConst.ORDER_SKU_MAP + orderParamVo.getUserId();
        BoundHashOperations<String, String, Integer> hashOperations = redisTemplate.boundHashOps(orderSkuKey);
        cartInfoList.forEach(cartInfo -> {
            if (Boolean.TRUE.equals(hashOperations.hasKey(cartInfo.getSkuId().toString()))) {
                Integer orderSkuNum = hashOperations.get(cartInfo.getSkuId().toString()) + cartInfo.getSkuNum();
                hashOperations.put(cartInfo.getSkuId().toString(), orderSkuNum);
            }
        });
        redisTemplate.expire(orderSkuKey, DateUtil.getCurrentExpireTimes(), TimeUnit.SECONDS);

        // 发送消息
        return orderInfo.getId();
    }

    @Override
    public OrderInfo getOrderInfoById(Long orderId) {
        return null;
    }

    private BigDecimal computeTotalAmount(List<CartInfo> cartInfoList) {
        BigDecimal total = new BigDecimal(0);
        for (CartInfo cartInfo : cartInfoList) {
            BigDecimal itemTotal = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
            total = total.add(itemTotal);
        }
        return total;
    }

    /**
     * 计算购物项分摊的优惠减少金额 打折：按折扣分担 现金：按比例分摊
     *
     * @param cartInfoParamList
     * @return
     */
    private Map<String, BigDecimal> computeActivitySplitAmount(List<CartInfo> cartInfoParamList) {
        Map<String, BigDecimal> activitySplitAmountMap = new HashMap<>();

        // 促销活动相关信息
        List<CartInfoVo> cartInfoVoList = activityFeignClient.findCartActivityList(cartInfoParamList);

        // 活动总金额
        BigDecimal activityReduceAmount = new BigDecimal(0);
        if (!CollectionUtils.isEmpty(cartInfoVoList)) {
            for (CartInfoVo cartInfoVo : cartInfoVoList) {
                ActivityRule activityRule = cartInfoVo.getActivityRule();
                List<CartInfo> cartInfoList = cartInfoVo.getCartInfoList();
                if (null != activityRule) {
                    // 优惠金额， 按比例分摊
                    BigDecimal reduceAmount = activityRule.getReduceAmount();
                    activityReduceAmount = activityReduceAmount.add(reduceAmount);
                    if (cartInfoList.size() == 1) {
                        activitySplitAmountMap.put("activity:" + cartInfoList.get(0).getSkuId(), reduceAmount);
                    } else {
                        // 总金额
                        BigDecimal originalTotalAmount = new BigDecimal(0);
                        for (CartInfo cartInfo : cartInfoList) {
                            BigDecimal skuTotalAmount =
                                cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
                            originalTotalAmount = originalTotalAmount.add(skuTotalAmount);
                        }
                        // 记录除最后一项是所有分摊金额， 最后一项=总的 - skuPartReduceAmount
                        BigDecimal skuPartReduceAmount = new BigDecimal(0);
                        if (activityRule.getActivityType() == ActivityType.FULL_REDUCTION) {
                            for (int i = 0, len = cartInfoList.size(); i < len; i++) {
                                CartInfo cartInfo = cartInfoList.get(i);
                                if (i < len - 1) {
                                    BigDecimal skuTotalAmount =
                                        cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
                                    // sku分摊金额
                                    BigDecimal skuReduceAmount = skuTotalAmount
                                        .divide(originalTotalAmount, 2, RoundingMode.HALF_UP).multiply(reduceAmount);
                                    activitySplitAmountMap.put("activity:" + cartInfo.getSkuId(), skuReduceAmount);

                                    skuPartReduceAmount = skuPartReduceAmount.add(skuReduceAmount);
                                } else {
                                    BigDecimal skuReduceAmount = reduceAmount.subtract(skuPartReduceAmount);
                                    activitySplitAmountMap.put("activity:" + cartInfo.getSkuId(), skuReduceAmount);
                                }
                            }
                        } else {
                            for (int i = 0, len = cartInfoList.size(); i < len; i++) {
                                CartInfo cartInfo = cartInfoList.get(i);
                                if (i < len - 1) {
                                    BigDecimal skuTotalAmount =
                                        cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));

                                    // sku分摊金额
                                    BigDecimal skuDiscountTotalAmount = skuTotalAmount
                                        .multiply(activityRule.getBenefitDiscount().divide(new BigDecimal("10")));
                                    BigDecimal skuReduceAmount = skuTotalAmount.subtract(skuDiscountTotalAmount);
                                    activitySplitAmountMap.put("activity:" + cartInfo.getSkuId(), skuReduceAmount);

                                    skuPartReduceAmount = skuPartReduceAmount.add(skuReduceAmount);
                                } else {
                                    BigDecimal skuReduceAmount = reduceAmount.subtract(skuPartReduceAmount);
                                    activitySplitAmountMap.put("activity:" + cartInfo.getSkuId(), skuReduceAmount);
                                }
                            }
                        }
                    }
                }
            }
        }
        activitySplitAmountMap.put("activity:total", activityReduceAmount);
        return activitySplitAmountMap;
    }

    private Map<String, BigDecimal> computeCouponInfoSplitAmount(List<CartInfo> cartInfoList, Long couponId) {
        Map<String, BigDecimal> couponInfoSplitAmountMap = new HashMap<>();

        if (null == couponId)
            return couponInfoSplitAmountMap;
        CouponInfo couponInfo = activityFeignClient.findRangeSkuIdList(cartInfoList, couponId);

        if (null != couponInfo) {
            // sku对应的订单明细
            Map<Long, CartInfo> skuIdToCartInfoMap = new HashMap<>();
            for (CartInfo cartInfo : cartInfoList) {
                skuIdToCartInfoMap.put(cartInfo.getSkuId(), cartInfo);
            }
            // 优惠券对应的skuId列表
            List<Long> skuIdList = couponInfo.getSkuIdList();
            if (CollectionUtils.isEmpty(skuIdList)) {
                return couponInfoSplitAmountMap;
            }
            // 优惠券优化总金额
            BigDecimal reduceAmount = couponInfo.getAmount();
            if (skuIdList.size() == 1) {
                // sku的优化金额
                couponInfoSplitAmountMap.put("coupon:" + skuIdToCartInfoMap.get(skuIdList.get(0)).getSkuId(),
                    reduceAmount);
            } else {
                // 总金额
                BigDecimal originalTotalAmount = new BigDecimal(0);
                for (Long skuId : skuIdList) {
                    CartInfo cartInfo = skuIdToCartInfoMap.get(skuId);
                    BigDecimal skuTotalAmount = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
                    originalTotalAmount = originalTotalAmount.add(skuTotalAmount);
                }
                // 记录除最后一项是所有分摊金额， 最后一项=总的 - skuPartReduceAmount
                BigDecimal skuPartReduceAmount = new BigDecimal(0);
                if (couponInfo.getCouponType() == CouponType.CASH
                    || couponInfo.getCouponType() == CouponType.FULL_REDUCTION) {
                    for (int i = 0, len = skuIdList.size(); i < len; i++) {
                        CartInfo cartInfo = skuIdToCartInfoMap.get(skuIdList.get(i));
                        if (i < len - 1) {
                            BigDecimal skuTotalAmount =
                                cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
                            // sku分摊金额
                            BigDecimal skuReduceAmount = skuTotalAmount
                                .divide(originalTotalAmount, 2, RoundingMode.HALF_UP).multiply(reduceAmount);
                            couponInfoSplitAmountMap.put("coupon:" + cartInfo.getSkuId(), skuReduceAmount);

                            skuPartReduceAmount = skuPartReduceAmount.add(skuReduceAmount);
                        } else {
                            BigDecimal skuReduceAmount = reduceAmount.subtract(skuPartReduceAmount);
                            couponInfoSplitAmountMap.put("coupon:" + cartInfo.getSkuId(), skuReduceAmount);
                        }
                    }
                }
            }
            couponInfoSplitAmountMap.put("coupon:total", couponInfo.getAmount());
        }
        return couponInfoSplitAmountMap;
    }
}
