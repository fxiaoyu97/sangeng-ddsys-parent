package com.sangeng.ddsys.cart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sangeng.ddsys.cart.service.CartInfoService;
import com.sangeng.ddsys.common.auth.AuthContextHolder;
import com.sangeng.ddsys.common.result.Result;

import io.swagger.annotations.Api;

/**
 * @author: calos
 * @version: 1.0.0
 * @createTime: 2023/11/15 21:37
 **/
@Api(tags = "购物车接口")
@RestController
@RequestMapping("/api/cartc")
public class CartApiController {
    @Autowired
    private CartInfoService cartInfoService;

    /**
     * 添加购物车
     *
     * @param skuId
     * @param skuNum
     * @return
     */
    @GetMapping("addToCart/{skuId}/{skuNum}")
    public Result addToCart(@PathVariable("skuId") Long skuId, @PathVariable("skuNum") Integer skuNum) {
        // 如何获取userId
        Long userId = AuthContextHolder.getUserId();
        cartInfoService.addToCart(skuId, userId, skuNum);
        return Result.ok();
    }
}
