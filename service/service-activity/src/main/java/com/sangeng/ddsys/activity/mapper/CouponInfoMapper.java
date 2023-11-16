package com.sangeng.ddsys.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sangeng.ddsys.model.activity.CouponInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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

    List<CouponInfo> selectCartCouponInfoList(Long userId);
}
