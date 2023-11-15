package com.sangeng.ddsys.activity.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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
import com.sangeng.ddsys.model.product.SkuInfo;
import com.sangeng.ddsys.vo.activity.ActivityRuleVo;

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
}
