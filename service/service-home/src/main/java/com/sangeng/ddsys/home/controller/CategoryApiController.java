package com.sangeng.ddsys.home.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sangeng.ddsys.client.product.ProductFeignClient;
import com.sangeng.ddsys.common.result.Result;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author: calos
 * @version: 1.0.0
 * @createTime: 2023/11/15 10:58
 **/
@Api(tags = "商品分类")
@RestController
@RequestMapping("api/home")
public class CategoryApiController {

    @Resource
    private ProductFeignClient productFeignClient;

    @ApiOperation(value = "获取分类信息")
    @GetMapping("category")
    public Result index() {
        return Result.ok(productFeignClient.findAllCategoryList());
    }
}
