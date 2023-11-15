package com.sangeng.ddsys.activity.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sangeng.ddsys.model.activity.ActivityInfo;
import com.sangeng.ddsys.model.activity.ActivityRule;
import com.sangeng.ddsys.model.product.SkuInfo;
import com.sangeng.ddsys.vo.activity.ActivityRuleVo;

/**
 * <p>
 * 活动表 服务类
 * </p>
 *
 * @author calos
 * @since 2023-11-05
 */
public interface ActivityInfoService extends IService<ActivityInfo> {

    IPage<ActivityInfo> selectPage(Page<ActivityInfo> pageParam);

    Map<String, Object> findActivityRuleList(Long id);

    void saveActivityRule(ActivityRuleVo activityRuleVo);

    List<SkuInfo> findSkuInfoByKeyword(String keyword);

    Map<Long, List<String>> findActivity(List<Long> skuIdList);

    /**
     * 根据skuId获取促销规则信息
     * 
     * @param skuId
     * @return
     */
    List<ActivityRule> findActivityRule(Long skuId);

    Map<String, Object> findActivityAndCoupon(Long skuId, Long userId);
}
