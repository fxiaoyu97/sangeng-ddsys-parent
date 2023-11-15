package com.sangeng.ddsys.activity.api;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.*;

import com.sangeng.ddsys.activity.service.ActivityInfoService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author: calos
 * @version: 1.0.0
 * @createTime: 2023/11/15 9:36
 **/
@Api(tags = "促销与优惠券接口")
@RestController
@RequestMapping("/api/activity")
public class ActivityInfoApiController {
    @Resource
    private ActivityInfoService activityInfoService;

    @ApiOperation(value = "根据skuId列表获取促销信息")
    @PostMapping("inner/findActivity")
    public Map<Long, List<String>> findActivity(@RequestBody List<Long> skuIdList) {
        return activityInfoService.findActivity(skuIdList);
    }

    @ApiOperation(value = "根据skuId获取促销与优惠券信息")
    @GetMapping("inner/findActivityAndCoupon/{skuId}/{userId}")
    public Map<String, Object> findActivityAndCoupon(@PathVariable Long skuId, @PathVariable("userId") Long userId) {
        return activityInfoService.findActivityAndCoupon(skuId, userId);
    }
}
