package com.sangeng.ddsys.activity;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author: calos
 * @version: 1.0.0
 * @createTime: 2023/11/15 9:35
 **/
@FeignClient("service-activity")
public interface ActivityFeignClient {
    // 根据skuId列表获取促销信息
    @PostMapping("/api/activity/inner/findActivity")
    Map<Long, List<String>> findActivity(@RequestBody List<Long> skuIdList);

    /**
     * 根据skuId获取促销与优惠券信息
     *
     * @param skuId
     * @param userId
     * @return
     */
    @GetMapping("/api/activity/inner/findActivityAndCoupon/{skuId}/{userId}")
    Map<String, Object> findActivityAndCoupon(@PathVariable Long skuId, @PathVariable("userId") Long userId);
}
