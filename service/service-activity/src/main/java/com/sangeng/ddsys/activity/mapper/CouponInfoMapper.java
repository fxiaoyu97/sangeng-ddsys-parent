package com.sangeng.ddsys.activity.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sangeng.ddsys.model.activity.CouponInfo;

/**
 * <p>
 * 优惠券信息 Mapper 接口
 * </p>
 *
 * @author calos
 * @since 2023-11-05
 */
public interface CouponInfoMapper extends BaseMapper<CouponInfo> {
    /**
     * sku优惠券
     * 
     * @param skuId
     * @param categoryId
     * @param userId
     * @return
     */
    List<CouponInfo> selectCouponInfoList(@Param("skuId") Long skuId, @Param("categoryId") Long categoryId,
        @Param("userId") Long userId);
}
