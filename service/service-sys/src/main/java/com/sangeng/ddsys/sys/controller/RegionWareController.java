package com.sangeng.ddsys.sys.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sangeng.ddsys.common.result.Result;
import com.sangeng.ddsys.model.sys.RegionWare;
import com.sangeng.ddsys.sys.service.RegionWareService;
import com.sangeng.ddsys.vo.sys.RegionWareQueryVo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * <p>
 * 城市仓库关联表 前端控制器
 * </p>
 *
 * @author calos
 * @since 2023-11-03
 */
@Api(tags = "开通区域接口")
@RestController
@RequestMapping("/admin/sys/regionWare")
@CrossOrigin
public class RegionWareController {

    @Autowired
    private RegionWareService regionWareService;

    // 开通区域列表
    @ApiOperation("开通区域列表")
    @GetMapping("{page}/{limit}")
    public Result get(@PathVariable Long page, @PathVariable Long limit, RegionWareQueryVo regionWareQueryVo) {
        Page<RegionWare> regionWarePage = new Page<>();
        IPage<RegionWare> pageModel = regionWareService.selectPageRegionWare(regionWarePage, regionWareQueryVo);
        return Result.ok(pageModel);
    }

    /**
     * 添加开通区域
     * 
     * @param regionWare 区域仓库信息
     * @return 添加结果
     */
    @ApiOperation("添加开通区域")
    @PostMapping("save")
    public Result addRegionWare(@RequestBody RegionWare regionWare) {
        regionWareService.saveRegionWare(regionWare);
        return Result.ok(null);
    }

    @ApiOperation("删除开通区域")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        regionWareService.removeById(id);
        return Result.ok(null);
    }

    @ApiOperation("取消开通区域")
    @PostMapping("/updateStatus/{id}/{status}")
    public Result updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        regionWareService.updateStatus(id, status);
        return Result.ok(null);
    }
}
