package com.sangeng.ddsys.activity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sangeng.ddsys.activity.mapper.ActivityInfoMapper;
import com.sangeng.ddsys.activity.mapper.ActivityRuleMapper;
import com.sangeng.ddsys.activity.mapper.ActivitySkuMapper;
import com.sangeng.ddsys.activity.service.ActivityInfoService;
import com.sangeng.ddsys.activity.service.CouponInfoService;
import com.sangeng.ddsys.client.product.ProductFeignClient;
import com.sangeng.ddsys.enums.ActivityType;
import com.sangeng.ddsys.model.activity.ActivityInfo;
import com.sangeng.ddsys.model.activity.ActivityRule;
import com.sangeng.ddsys.model.activity.ActivitySku;
import com.sangeng.ddsys.model.activity.CouponInfo;
import com.sangeng.ddsys.model.order.CartInfo;
import com.sangeng.ddsys.model.product.SkuInfo;
import com.sangeng.ddsys.vo.activity.ActivityRuleVo;
import com.sangeng.ddsys.vo.order.CartInfoVo;
import com.sangeng.ddsys.vo.order.OrderConfirmVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 活动表 服务实现类
 * </p>
 *
 * @author calos
 * @since 2023-11-05
 */
@Service
public class ActivityInfoServiceImpl extends ServiceImpl<ActivityInfoMapper, ActivityInfo>
    implements ActivityInfoService {

    @Autowired
    private ActivityRuleMapper activityRuleMapper;
    @Autowired
    private ActivitySkuMapper activitySkuMapper;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private CouponInfoService couponInfoService;

    @Override
    public IPage<ActivityInfo> selectPage(Page<ActivityInfo> pageParam) {
        QueryWrapper<ActivityInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");

        IPage<ActivityInfo> page = baseMapper.selectPage(pageParam, queryWrapper);
        page.getRecords().forEach(item -> item.setActivityTypeString(item.getActivityType().getComment()));
        return page;
    }

    @Override
    public Map<String, Object> findActivityRuleList(Long activityId) {
        Map<String, Object> result = new HashMap<>();

        // 根据活动id查询，查询规则列表 activity_rule 表
        List<ActivityRule> activityRuleList =
            activityRuleMapper.selectList(new QueryWrapper<ActivityRule>().eq("activity_id", activityId));
        result.put("activityRuleList", activityRuleList);
        // 根据活动id查询，查询使用规则商品skuId列表，activity_sku列表
        List<ActivitySku> activitySkuList =
            activitySkuMapper.selectList(new QueryWrapper<ActivitySku>().eq("activity_id", activityId));
        // 获取所有的skuId
        List<Long> skuIdList = activitySkuList.stream().map(ActivitySku::getSkuId).collect(Collectors.toList());
        // 远程调用 service-product 模块接口，根据skuid列表得到商品信息
        List<SkuInfo> skuInfoList = productFeignClient.findSkuInfoList(skuIdList);
        result.put("skuInfoList", skuInfoList);
        return result;
    }

    @Override
    public void saveActivityRule(ActivityRuleVo activityRuleVo) {
        // 根据活动id先删除之前的数据
        Long activityId = activityRuleVo.getActivityId();
        activityRuleMapper.delete(new LambdaQueryWrapper<ActivityRule>().eq(ActivityRule::getActivityId, activityId));
        activitySkuMapper.delete(new QueryWrapper<ActivitySku>().eq("activity_id", activityId));

        // 获取规则列表数据
        List<ActivityRule> activityRuleList = activityRuleVo.getActivityRuleList();
        // 获取规则范围数据
        List<ActivitySku> activitySkuList = activityRuleVo.getActivitySkuList();

        // 规则列表
        ActivityInfo activityInfo = baseMapper.selectById(activityRuleVo.getActivityId());
        for (ActivityRule activityRule : activityRuleList) {
            activityRule.setActivityId(activityRuleVo.getActivityId());// 活动id
            activityRule.setActivityType(activityInfo.getActivityType());// 活动类型
            activityRuleMapper.insert(activityRule);
        }
        // 规则范围数据
        for (ActivitySku activitySku : activitySkuList) {
            activitySku.setActivityId(activityRuleVo.getActivityId());
            activitySkuMapper.insert(activitySku);
        }
    }

    @Override
    public List<SkuInfo> findSkuInfoByKeyword(String keyword) {
        // 1、根据关键字查询sku匹配内容列表
        // service-product模块创建接口， 根据关键字查询sku匹配内容列表
        // service-activity远程调用得到sku内容列表
        List<SkuInfo> skuInfoList = productFeignClient.findSkuInfoByKeyword(keyword);
        List<SkuInfo> notExistSkuInfoList = new ArrayList<>();
        if (CollectionUtils.isEmpty(skuInfoList)) {
            return notExistSkuInfoList;
        }

        List<Long> skuIdList = skuInfoList.stream().map(SkuInfo::getId).collect(Collectors.toList());
        // 已经存在的skuId，一个sku只能参加一个促销活动，所以存在的得排除
        List<Long> existSkuIdList = baseMapper.selectExistSkuIdList(skuIdList);
        for (SkuInfo skuInfo : skuInfoList) {
            if (!existSkuIdList.contains(skuInfo.getId())) {
                notExistSkuInfoList.add(skuInfo);
            }
        }
        return notExistSkuInfoList;
    }

    // 查询商品获取规则数据
    @Override
    public List<ActivityRule> findActivityRule(Long skuId) {
        List<ActivityRule> activityRuleList = baseMapper.selectActivityRuleList(skuId);
        if (!CollectionUtils.isEmpty(activityRuleList)) {
            for (ActivityRule activityRule : activityRuleList) {
                activityRule.setRuleDesc(this.getRuleDesc(activityRule));
            }
        }
        return activityRuleList;
    }

    // 根据skuId列表获取促销信息
    @Override
    public Map<Long, List<String>> findActivity(List<Long> skuIdList) {
        Map<Long, List<String>> result = new HashMap<>();
        // skuIdList遍历，得到每个skuId
        skuIdList.forEach(skuId -> {
            // 根据skuId进行查询，查询sku对应活动里面规则列表
            List<ActivityRule> activityRuleList = baseMapper.selectActivityRuleList(skuId);
            // 数据封装，规则名称
            if (!CollectionUtils.isEmpty(activityRuleList)) {
                List<String> ruleList = new ArrayList<>();
                // 把规则名称处理
                for (ActivityRule activityRule : activityRuleList) {
                    ruleList.add(this.getRuleDesc(activityRule));
                }
                result.put(skuId, ruleList);
            }
        });
        return result;
    }

    // 构造规则名称的方法
    private String getRuleDesc(ActivityRule activityRule) {
        ActivityType activityType = activityRule.getActivityType();
        StringBuilder ruleDesc = new StringBuilder();
        if (activityType == ActivityType.FULL_REDUCTION) {
            ruleDesc.append("满").append(activityRule.getConditionAmount()).append("元减")
                .append(activityRule.getBenefitAmount()).append("元");
        } else {
            ruleDesc.append("满").append(activityRule.getConditionNum()).append("元打")
                .append(activityRule.getBenefitDiscount()).append("折");
        }
        return ruleDesc.toString();
    }

    @Override
    public Map<String, Object> findActivityAndCoupon(Long skuId, Long userId) {
        // 一个sku只能有一个促销活动，一个活动有多个活动规则（如满赠，满100送10，满500送50）
        List<ActivityRule> activityRuleList = this.findActivityRule(skuId);

        // 获取优惠券信息
        List<CouponInfo> couponInfoList = couponInfoService.findCouponInfoList(skuId, userId);

        Map<String, Object> map = new HashMap<>();
        map.put("activityRuleList", activityRuleList);
        map.put("couponInfoList", couponInfoList);
        return map;
    }

    @Override
    public OrderConfirmVo findCartActivityAndCoupon(List<CartInfo> cartInfoList, Long userId) {
        // 获取购物车，每个购物项参与活动，根据活动规则分组
        // 一个规则的对应多个商品
        List<CartInfoVo> carInfoVoList = this.findCartActivityList(cartInfoList);
        // 计算参与活动之后金额
        BigDecimal activityReduceAmount =
            carInfoVoList.stream().filter(carInfoVo -> null != carInfoVo.getActivityRule())
                .map(carInfoVo -> carInfoVo.getActivityRule().getReduceAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        // 获取购物车可以使用优惠券列表
        List<CouponInfo> couponInfoList = couponInfoService.findCartCouponInfo(cartInfoList, userId);

        //优惠券可优惠的总金额，一次购物只能使用一张优惠券
        BigDecimal couponReduceAmount = new BigDecimal(0);
        if (!CollectionUtils.isEmpty(couponInfoList)) {
            couponReduceAmount = couponInfoList.stream().filter(couponInfo -> couponInfo.getIsOptimal().intValue() == 1)
                .map(CouponInfo::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        // 计算没有参与活动，没有使用优惠券的购物车总金额
        BigDecimal originalTotalAmount = cartInfoList.stream().filter(cartInfo -> cartInfo.getIsChecked() == 1)
            .map(cartInfo -> cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 参与活动，使用优惠券总金额
        BigDecimal totalAmount = originalTotalAmount.subtract(activityReduceAmount).subtract(couponReduceAmount);
        // 封装需要数据到OrderConfirmVo，返回
        OrderConfirmVo orderTradeVo = new OrderConfirmVo();
        orderTradeVo.setCarInfoVoList(carInfoVoList);
        orderTradeVo.setActivityReduceAmount(activityReduceAmount);
        orderTradeVo.setCouponInfoList(couponInfoList);
        orderTradeVo.setCouponReduceAmount(couponReduceAmount);
        orderTradeVo.setOriginalTotalAmount(originalTotalAmount);
        orderTradeVo.setTotalAmount(totalAmount);
        return orderTradeVo;
    }

    @Override
    public List<CartInfoVo> findCartActivityList(List<CartInfo> cartInfoList) {
        List<CartInfoVo> carInfoVoList = new ArrayList<>();
        // 获取所有的skuId
        List<Long> skuIdList = cartInfoList.stream().map(CartInfo::getSkuId).collect(Collectors.toList());
        // 根据skuId获取对应的活动信息
        List<ActivitySku> activitySkuList = baseMapper.selectCartActivityList(skuIdList);
        //  根据活动分组，每个活动中包含哪些skuId信息，一个sku只能参加一个活动
        // key是分组字段，value对应skuId
        Map<Long, Set<Long>> activityIdToSkuIdListMap = activitySkuList.stream().collect(
            Collectors.groupingBy(ActivitySku::getActivityId,
                Collectors.mapping(ActivitySku::getSkuId, Collectors.toSet())));
        //第二步：获取活动对应的促销规则
        //获取购物车对应的活动id
        // key是活动id，value是活动里面规则列表数据
        Map<Long, List<ActivityRule>> activityIdToActivityRuleListMap = new HashMap<>();
        // 所有活动id
        Set<Long> activityIdSet = activitySkuList.stream().map(ActivitySku::getActivityId).collect(Collectors.toSet());
        if (!CollectionUtils.isEmpty(activityIdSet)) {
            LambdaQueryWrapper<ActivityRule> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.orderByDesc(ActivityRule::getConditionAmount, ActivityRule::getConditionNum);
            queryWrapper.in(ActivityRule::getActivityId, activityIdSet);
            List<ActivityRule> activityRuleList = activityRuleMapper.selectList(queryWrapper);
            // 封装到activityIdToActivityRuleListMap里面
            // 根据活动Id进行分组
            activityIdToActivityRuleListMap =
                activityRuleList.stream().collect(Collectors.groupingBy(ActivityRule::getActivityId));
        }
        //第三步：根据活动汇总购物项，相同活动的购物项为一组显示在页面，并且计算最优优惠金额
        //记录有活动的购物项skuId
        HashSet<Long> activitySkuIdSet = new HashSet<>();
        if (!CollectionUtils.isEmpty(activityIdToSkuIdListMap)) {
            // 遍历activityIdToSkuIdListMap集合
            Iterator<Map.Entry<Long, Set<Long>>> iterator = activityIdToSkuIdListMap.entrySet().iterator();
            while (iterator.hasNext()) {
                // 活动的Id
                Map.Entry<Long, Set<Long>> next = iterator.next();
                Long activityId = next.getKey();
                // 每个活动对应的skuId列表
                Set<Long> currentActivitySkuIdSet = next.getValue();
                // 获取当前活动对应的购物项列表
                List<CartInfo> currentActivityCartInfoList =
                    cartInfoList.stream().filter(cartInfo -> currentActivitySkuIdSet.contains(cartInfo.getSkuId()))
                        .collect(Collectors.toList());

                // 计算购物项总金额和总数量
                BigDecimal activityTotalAmount = this.computeTotalAmount(currentActivityCartInfoList);
                int activityTotalNum = this.computeCartNum(currentActivityCartInfoList);
                // 计算活动对应规则
                List<ActivityRule> currentActivityRuleList = activityIdToActivityRuleListMap.get(activityId);
                ActivityType activityType = currentActivityRuleList.get(0).getActivityType();
                // 判断活动类型，满减还是打折，计算出最优的优惠规则
                ActivityRule activityRule;
                if (activityType == ActivityType.FULL_REDUCTION) {
                    // 满减
                    activityRule = this.computeFullReduction(activityTotalAmount, currentActivityRuleList);
                } else {
                    // 计算满量打折最优规则
                    activityRule =
                        this.computeFullDiscount(activityTotalNum, activityTotalAmount, currentActivityRuleList);
                }
                // CartInfoVo封装
                CartInfoVo cartInfoVo = new CartInfoVo();
                cartInfoVo.setActivityRule(activityRule);
                cartInfoVo.setCartInfoList(currentActivityCartInfoList);
                carInfoVoList.add(cartInfoVo);
                // 记录哪些购物项参与活动
                activitySkuIdSet.addAll(currentActivitySkuIdSet);
            }
        }

        // 没有活动购物项skuId
        // 获取哪些skuId没有参加活动
        skuIdList.removeAll(activitySkuIdSet);
        if (!CollectionUtils.isEmpty(skuIdList)) {
            // 获取skuId对应购物项
            Map<Long, CartInfo> skuIdToCartInfoMap =
                cartInfoList.stream().collect(Collectors.toMap(CartInfo::getSkuId, Cartinfo -> Cartinfo));
            for (Long skuId : skuIdList) {
                CartInfoVo cartInfoVo = new CartInfoVo();
                cartInfoVo.setActivityRule(null);
                List<CartInfo> currentCartInfoList = new ArrayList<>();
                currentCartInfoList.add(skuIdToCartInfoMap.get(skuId));
                cartInfoVo.setCartInfoList(currentCartInfoList);
                carInfoVoList.add(cartInfoVo);
            }
        }

        return carInfoVoList;
    }

    /**
     * 计算满量打折最优规则
     *
     * @param totalNum
     * @param activityRuleList //该活动规则skuActivityRuleList数据，已经按照优惠折扣从大到小排序了
     */
    private ActivityRule computeFullDiscount(Integer totalNum, BigDecimal totalAmount,
        List<ActivityRule> activityRuleList) {
        ActivityRule optimalActivityRule = null;
        //该活动规则skuActivityRuleList数据，已经按照优惠金额从大到小排序了
        for (ActivityRule activityRule : activityRuleList) {
            //如果订单项购买个数大于等于满减件数，则优化打折
            if (totalNum.intValue() >= activityRule.getConditionNum()) {
                BigDecimal skuDiscountTotalAmount =
                    totalAmount.multiply(activityRule.getBenefitDiscount().divide(new BigDecimal("10")));
                BigDecimal reduceAmount = totalAmount.subtract(skuDiscountTotalAmount);
                activityRule.setReduceAmount(reduceAmount);
                optimalActivityRule = activityRule;
                break;
            }
        }
        if (null == optimalActivityRule) {
            //如果没有满足条件的取最小满足条件的一项
            optimalActivityRule = activityRuleList.get(activityRuleList.size() - 1);
            optimalActivityRule.setReduceAmount(new BigDecimal("0"));
            optimalActivityRule.setSelectType(1);

            StringBuffer ruleDesc =
                new StringBuffer().append("满").append(optimalActivityRule.getConditionNum()).append("元打")
                    .append(optimalActivityRule.getBenefitDiscount()).append("折，还差")
                    .append(totalNum - optimalActivityRule.getConditionNum()).append("件");
            optimalActivityRule.setRuleDesc(ruleDesc.toString());
        } else {
            StringBuffer ruleDesc =
                new StringBuffer().append("满").append(optimalActivityRule.getConditionNum()).append("元打")
                    .append(optimalActivityRule.getBenefitDiscount()).append("折，已减")
                    .append(optimalActivityRule.getReduceAmount()).append("元");
            optimalActivityRule.setRuleDesc(ruleDesc.toString());
            optimalActivityRule.setSelectType(2);
        }
        return optimalActivityRule;
    }

    /**
     * 计算满减最优规则
     *
     * @param totalAmount
     * @param activityRuleList //该活动规则skuActivityRuleList数据，已经按照优惠金额从大到小排序了
     */
    private ActivityRule computeFullReduction(BigDecimal totalAmount, List<ActivityRule> activityRuleList) {
        ActivityRule optimalActivityRule = null;
        //该活动规则skuActivityRuleList数据，已经按照优惠金额从大到小排序了
        for (ActivityRule activityRule : activityRuleList) {
            //如果订单项金额大于等于满减金额，则优惠金额
            if (totalAmount.compareTo(activityRule.getConditionAmount()) > -1) {
                //优惠后减少金额
                activityRule.setReduceAmount(activityRule.getBenefitAmount());
                optimalActivityRule = activityRule;
                break;
            }
        }
        if (null == optimalActivityRule) {
            //如果没有满足条件的取最小满足条件的一项
            optimalActivityRule = activityRuleList.get(activityRuleList.size() - 1);
            optimalActivityRule.setReduceAmount(new BigDecimal("0"));
            optimalActivityRule.setSelectType(1);

            StringBuffer ruleDesc =
                new StringBuffer().append("满").append(optimalActivityRule.getConditionAmount()).append("元减")
                    .append(optimalActivityRule.getBenefitAmount()).append("元，还差")
                    .append(totalAmount.subtract(optimalActivityRule.getConditionAmount())).append("元");
            optimalActivityRule.setRuleDesc(ruleDesc.toString());
        } else {
            StringBuffer ruleDesc =
                new StringBuffer().append("满").append(optimalActivityRule.getConditionAmount()).append("元减")
                    .append(optimalActivityRule.getBenefitAmount()).append("元，已减")
                    .append(optimalActivityRule.getReduceAmount()).append("元");
            optimalActivityRule.setRuleDesc(ruleDesc.toString());
            optimalActivityRule.setSelectType(2);
        }
        return optimalActivityRule;
    }

    private BigDecimal computeTotalAmount(List<CartInfo> cartInfoList) {
        BigDecimal total = new BigDecimal("0");
        for (CartInfo cartInfo : cartInfoList) {
            //是否选中
            if (cartInfo.getIsChecked().intValue() == 1) {
                BigDecimal itemTotal = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
                total = total.add(itemTotal);
            }
        }
        return total;
    }

    private int computeCartNum(List<CartInfo> cartInfoList) {
        int total = 0;
        for (CartInfo cartInfo : cartInfoList) {
            //是否选中
            if (cartInfo.getIsChecked().intValue() == 1) {
                total += cartInfo.getSkuNum();
            }
        }
        return total;
    }
}
