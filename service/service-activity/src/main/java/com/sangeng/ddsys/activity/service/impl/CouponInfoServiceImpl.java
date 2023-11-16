package com.sangeng.ddsys.activity.service.impl;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sangeng.ddsys.activity.mapper.CouponInfoMapper;
import com.sangeng.ddsys.activity.mapper.CouponRangeMapper;
import com.sangeng.ddsys.activity.service.CouponInfoService;
import com.sangeng.ddsys.client.product.ProductFeignClient;
import com.sangeng.ddsys.enums.CouponRangeType;
import com.sangeng.ddsys.model.activity.CouponInfo;
import com.sangeng.ddsys.model.activity.CouponRange;
import com.sangeng.ddsys.model.base.BaseEntity;
import com.sangeng.ddsys.model.order.CartInfo;
import com.sangeng.ddsys.model.product.Category;
import com.sangeng.ddsys.model.product.SkuInfo;
import com.sangeng.ddsys.vo.activity.CouponRuleVo;

/**
 * <p>
 * 优惠券信息 服务实现类
 * </p>
 *
 * @author calos
 * @since 2023-11-05
 */
@Service
public class CouponInfoServiceImpl extends ServiceImpl<CouponInfoMapper, CouponInfo> implements CouponInfoService {
    @Autowired
    private CouponRangeMapper couponRangeMapper;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Override
    public IPage<CouponInfo> selectPage(Page<CouponInfo> pageParam) {
        // 构造排序条件
        QueryWrapper<CouponInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        IPage<CouponInfo> page = baseMapper.selectPage(pageParam, queryWrapper);
        page.getRecords().forEach(item -> {
            item.setCouponTypeString(item.getCouponType().getComment());
            if (null != item.getRangeType()) {
                item.setRangeTypeString(item.getRangeType().getComment());
            }
        });
        // 返回数据集合
        return page;
    }

    @Override
    public CouponInfo getCouponInfo(String id) {
        CouponInfo couponInfo = baseMapper.selectById(id);
        couponInfo.setCouponTypeString(couponInfo.getCouponType().getComment());
        if (couponInfo.getRangeType() != null) {
            couponInfo.setRangeTypeString(couponInfo.getRangeType().getComment());
        }
        return couponInfo;
    }

    @Override
    public Map<String, Object> findCouponRuleList(Long id) {
        // 1、根据优惠券id查询优惠券基本信息 coupon_info表
        Map<String, Object> result = new HashMap<>();
        CouponInfo couponInfo = baseMapper.selectById(id);
        // 2、根据优惠券id查询coupon_range 查询里面对应的range_id
        // 如果规则类型sku range_id就是skuId
        // 如果规则类型是分类Category range_id就是分类id
        List<CouponRange> couponRangeList =
            couponRangeMapper.selectList(new LambdaQueryWrapper<CouponRange>().eq(CouponRange::getCouponId, id));
        List<Long> rangeIdList = couponRangeList.stream().map(CouponRange::getRangeId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(rangeIdList)) {
            return result;
        }

        // 3、分别判断封装不同数据
        if (couponInfo.getRangeType() == CouponRangeType.SKU) {
            // 如果规则类型是sku，得到skuId，远程调用根据多个skuId获取对应sku信息
            List<SkuInfo> skuInfoList = productFeignClient.findSkuInfoList(rangeIdList);
            result.put("skuInfoList", skuInfoList);
        } else if (couponInfo.getRangeType() == CouponRangeType.CATEGORY) {
            // 如果规则类型是sku，得到分类id，远程调用根据分类id值，获得对应分类信息}
            List<Category> categoryList = productFeignClient.findCategoryList(rangeIdList);
            result.put("categoryList", categoryList);
        }
        return result;
    }

    @Override
    public void saveCouponRule(CouponRuleVo couponRuleVo) {
        /*
        优惠券couponInfo 与 couponRange 要一起操作：先删除couponRange ，更新couponInfo ，再新增couponRange ！
        */
        QueryWrapper<CouponRange> couponRangeQueryWrapper = new QueryWrapper<>();
        couponRangeQueryWrapper.eq("coupon_id", couponRuleVo.getCouponId());
        couponRangeMapper.delete(couponRangeQueryWrapper);

        // 更新数据
        CouponInfo couponInfo = this.getById(couponRuleVo.getCouponId());
        // couponInfo.setCouponType();
        couponInfo.setRangeType(couponRuleVo.getRangeType());
        couponInfo.setConditionAmount(couponRuleVo.getConditionAmount());
        couponInfo.setAmount(couponRuleVo.getAmount());
        couponInfo.setConditionAmount(couponRuleVo.getConditionAmount());
        couponInfo.setRangeDesc(couponRuleVo.getRangeDesc());

        baseMapper.updateById(couponInfo);

        // 插入优惠券的规则 couponRangeList
        List<CouponRange> couponRangeList = couponRuleVo.getCouponRangeList();
        for (CouponRange couponRange : couponRangeList) {
            couponRange.setCouponId(couponRuleVo.getCouponId());
            // 插入数据
            couponRangeMapper.insert(couponRange);
        }
    }

    @Override
    public List<CouponInfo> findCouponByKeyword(String keyword) {
        // 模糊查询
        QueryWrapper<CouponInfo> couponInfoQueryWrapper = new QueryWrapper<>();
        couponInfoQueryWrapper.like("coupon_name", keyword);
        return baseMapper.selectList(couponInfoQueryWrapper);
    }

    @Override
    public List<CouponInfo> findCouponInfoList(Long skuId, Long userId) {
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
        if (null == skuInfo)
            return new ArrayList<>();
        return baseMapper.selectCouponInfoList(skuInfo.getId(), skuInfo.getCategoryId(), userId);
    }

    @Override
    public List<CouponInfo> findCartCouponInfo(List<CartInfo> cartInfoList, Long userId) {
        // 获取全部用户优惠券，coupon_use coupon_info
        List<CouponInfo> userAllCouponInfoList = baseMapper.selectCartCouponInfoList(userId);
        if (CollectionUtils.isEmpty(userAllCouponInfoList)) {
            return null;
        }
        // 从第一步返回list集合中，获取优惠券id列表
        List<Long> couponIdList = userAllCouponInfoList.stream().map(BaseEntity::getId).collect(Collectors.toList());
        // 查询优惠券对应的范围 coupon_range
        List<CouponRange> couponRangesList = couponRangeMapper
            .selectList(new LambdaQueryWrapper<CouponRange>().in(CouponRange::getCouponId, couponIdList));
        // 获取优惠券id对应的满足使用范围的购物项skuId列表
        // 优惠券id进行分组，得到map集合，Map<Long,List<Long>>
        Map<Long, List<Long>> couponIdToSkuIdMap = this.findCouponIdToSkuIdMap(cartInfoList, couponRangesList);
        // 优惠后减少金额
        BigDecimal reduceAmount = new BigDecimal(0);
        // 记录最优优惠券
        CouponInfo optimalCouponInfo = null;
        // 遍历全部优惠券集合，判断优惠券类型 - 全场通用、sku、分类
        for (CouponInfo couponInfo : userAllCouponInfoList) {
            // 全场通用
            if (CouponRangeType.ALL == couponInfo.getRangeType()) {
                // 判断是否满足优惠使用门槛
                // 计算购物车商品的总价
                BigDecimal totalAmount = computeTotalAmount(cartInfoList);
                if (totalAmount.subtract(couponInfo.getConditionAmount()).doubleValue() >= 0) {
                    couponInfo.setIsSelect(1);
                }
            } else {
                // 根据优惠券Id获取对应的skuId列表
                List<Long> skuIdList = couponIdToSkuIdMap.get(couponInfo.getId());
                // 获取满足适用范围的购物项
                List<CartInfo> currentCartInfoList = cartInfoList.stream()
                    .filter(cartInfo -> skuIdList.contains(cartInfo.getSkuId())).collect(Collectors.toList());
                BigDecimal totalAmount = computeTotalAmount(currentCartInfoList);
                if (totalAmount.subtract(couponInfo.getConditionAmount()).doubleValue() >= 0) {
                    couponInfo.setIsSelect(1);
                }
            }
            // 判断当前优惠券是否是最优优惠券
            if (couponInfo.getIsSelect().intValue() == 1
                && couponInfo.getAmount().subtract(reduceAmount).doubleValue() > 0) {
                reduceAmount = couponInfo.getAmount();
                optimalCouponInfo = couponInfo;
            }

        }
        if (null != optimalCouponInfo) {
            optimalCouponInfo.setIsOptimal(1);
        }
        // 返回结果
        return null;
    }

    private BigDecimal computeTotalAmount(List<CartInfo> cartInfoList) {
        BigDecimal total = new BigDecimal("0");
        for (CartInfo cartInfo : cartInfoList) {
            // 是否选中
            if (cartInfo.getIsChecked().intValue() == 1) {
                BigDecimal itemTotal = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
                total = total.add(itemTotal);
            }
        }
        return total;
    }

    private Map<Long, List<Long>> findCouponIdToSkuIdMap(List<CartInfo> cartInfoList,
        List<CouponRange> couponRangesList) {
        Map<Long, List<Long>> couponIdToSkuIdMap = new HashMap<>();
        // couponRangeList数据处理，根据优惠券id分组
        Map<Long, List<CouponRange>> couponIdToCouponRangeListMap =
            couponRangesList.stream().collect(Collectors.groupingBy(CouponRange::getCouponId));

        for (Map.Entry<Long, List<CouponRange>> entry : couponIdToCouponRangeListMap.entrySet()) {
            Long couponId = entry.getKey();
            Set<Long> skuIdSet = this.getCouponSkuIdList(cartInfoList, entry);
            couponIdToSkuIdMap.put(couponId, new ArrayList<>(skuIdSet));
        }
        return couponIdToSkuIdMap;
    }

    private Set<Long> getCouponSkuIdList(List<CartInfo> cartInfoList, Map.Entry<Long, List<CouponRange>> entry) {
        List<CouponRange> couponRangeList = entry.getValue();

        Set<Long> skuIdSet = new HashSet<>();
        for (CartInfo cartInfo : cartInfoList) {
            for (CouponRange couponRange : couponRangeList) {
                if (CouponRangeType.SKU == couponRange.getRangeType()
                    && couponRange.getRangeId().longValue() == cartInfo.getSkuId().intValue()) {
                    skuIdSet.add(cartInfo.getSkuId());
                } else if (CouponRangeType.CATEGORY == couponRange.getRangeType()
                    && couponRange.getRangeId().longValue() == cartInfo.getCategoryId().intValue()) {
                    skuIdSet.add(cartInfo.getSkuId());
                } else {

                }
            }
        }
        return skuIdSet;
    }
}
