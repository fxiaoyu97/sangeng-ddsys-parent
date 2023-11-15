package com.sangeng.ddsys.activity.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sangeng.ddsys.model.activity.ActivityInfo;
import com.sangeng.ddsys.model.activity.ActivityRule;

/**
 * <p>
 * 活动表 Mapper 接口
 * </p>
 *
 * @author calos
 * @since 2023-11-05
 */
public interface ActivityInfoMapper extends BaseMapper<ActivityInfo> {

    List<Long> selectExistSkuIdList(@Param("skuIdList") List<Long> skuIdList);

    List<ActivityRule> selectActivityRuleList(@Param("skuId") Long skuId);

}
