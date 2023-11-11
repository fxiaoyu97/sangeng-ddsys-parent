package com.sangeng.ddsys.activity.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sangeng.ddsys.model.activity.ActivityInfo;
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

    Object findActivityRuleList(Long id);

    void saveActivityRule(ActivityRuleVo activityRuleVo);

    Object findSkuInfoByKeyword(String keyword);
}
