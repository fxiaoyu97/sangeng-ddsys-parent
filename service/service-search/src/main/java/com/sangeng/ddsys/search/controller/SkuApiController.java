package com.sangeng.ddsys.search.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sangeng.ddsys.common.result.Result;
import com.sangeng.ddsys.model.search.SkuEs;
import com.sangeng.ddsys.search.service.SkuService;
import com.sangeng.ddsys.vo.search.SkuEsQueryVo;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * @author: calos
 * @create: 2023-11-05 12:52
 */
@RestController
@RequestMapping("api/search/sku")
public class SkuApiController {

    @Autowired
    private SkuService skuService;

    @ApiOperation(value = "上架商品")
    @GetMapping("inner/upperSku/{skuId}")
    public Result upperGoods(@PathVariable("skuId") Long skuId) {
        skuService.upperSku(skuId);
        return Result.ok();
    }

    @ApiOperation(value = "下架商品")
    @GetMapping("inner/lowerSku/{skuId}")
    public Result lowerGoods(@PathVariable("skuId") Long skuId) {
        skuService.lowerSku(skuId);
        return Result.ok();
    }

    @ApiOperation(value = "获取爆品商品")
    @GetMapping("inner/findHotSkuList")
    public List<SkuEs> findHotSkuList() {
        return skuService.findHotSkuList();
    }

    @ApiOperation(value = "搜索商品")
    @GetMapping("{page}/{limit}")
    public Result list(@ApiParam(name = "page", value = "当前页码", required = true) @PathVariable Integer page,
        @ApiParam(name = "limit", value = "每页记录数", required = true) @PathVariable Integer limit,
        @ApiParam(name = "searchParamVo", value = "查询对象", required = false) SkuEsQueryVo searchParamVo) {

        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<SkuEs> pageModel = skuService.search(pageable, searchParamVo);
        return Result.ok(pageModel);
    }

    @ApiOperation(value = "更新商品incrHotScore")
    @GetMapping("inner/incrHotScore/{skuId}")
    public Boolean incrHotScore(@PathVariable("skuId") Long skuId) {
        // 调用服务层
        skuService.incrHotScore(skuId);
        return true;
    }
}
