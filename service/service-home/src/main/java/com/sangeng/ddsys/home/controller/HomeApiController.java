package com.sangeng.ddsys.home.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sangeng.ddsys.common.auth.AuthContextHolder;
import com.sangeng.ddsys.common.result.Result;
import com.sangeng.ddsys.home.service.HomeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author: calos
 * @version: 1.0.0
 * @createTime: 2023/11/15 8:20
 **/
@Api(tags = "首页接口")
@RestController
@RequestMapping("api/home")
public class HomeApiController {
    @Autowired
    private HomeService homeService;

    @ApiOperation(value = "获取首页数据")
    @GetMapping("index")
    public Result index(HttpServletRequest request) {
        // 获取用户Id
        Long userId = AuthContextHolder.getUserId();
        return Result.ok(homeService.home(userId));
    }
}
