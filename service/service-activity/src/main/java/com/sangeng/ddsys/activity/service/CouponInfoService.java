package com.sangeng.ddsys.activity.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sangeng.ddsys.model.activity.CouponInfo;
import com.sangeng.ddsys.vo.activity.CouponRuleVo;

/**
 * <p>
 * 优惠券信息 服务类
 * </p>
 *
 * @author calos
 * @since 2023-11-05
 */
public interface CouponInfoService extends IService<CouponInfo> {
    // 优惠卷分页查询
    IPage<CouponInfo> selectPage(Page<CouponInfo> pageParam);

    CouponInfo getCouponInfo(String id);

    Map<String, Object> findCouponRuleList(Long id);

    // 新增优惠券规则
    void saveCouponRule(CouponRuleVo couponRuleVo);

    // 根据关键字获取sku列表，活动使用
    List<CouponInfo> findCouponByKeyword(String keyword);

    List<CouponInfo> findCouponInfoList(Long skuId, Long userId);

}
