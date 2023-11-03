package com.sangeng.ddsys.sys.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.sangeng.ddsys.common.result.Result;
import com.sangeng.ddsys.model.sys.Region;
import com.sangeng.ddsys.sys.service.RegionService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * <p>
 * 地区表 前端控制器
 * </p>
 *
 * @author calos
 * @since 2023-11-03
 */
@Api(tags = "区域管理接口")
@RestController
@RequestMapping("/admin/sys/region")
@CrossOrigin
public class RegionController {
    @Autowired
    private RegionService regionService;

    @ApiOperation("根据区域关键词查询区域列表信息")
    @GetMapping("/findRegionByKeyword/{keyword}")
    public Result findRegionByKeyword(@PathVariable String keyword) {
        List<Region> regionList = regionService.findRegionByKeyword(keyword);
        return Result.ok(regionList);
    }
}
