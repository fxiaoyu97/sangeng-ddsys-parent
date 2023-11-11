package com.sangeng.ddsys.acl.controller;

import com.sangeng.ddsys.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: calos
 * @version: 1.0.0
 * @createTime: 2023/10/27 17:05
 **/
@Api(tags = "登录接口")
@RestController
@RequestMapping("/admin/acl/index")
public class IndexController {
    /**
     * 1、请求登陆的login
     *
     * @return token
     */
    @ApiOperation("登录")
    @PostMapping("login")
    public Result loggin() {
        final Map<String, Object> map = new HashMap<>();
        map.put("token", "admin-token");
        return Result.ok(map);
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @ApiOperation("获取信息")
    @GetMapping("info")
    public Result info() {
        final Map<String, Object> map = new HashMap<>();
        map.put("name", "atguigu");
        map.put("avatar", "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
        return Result.ok(map);
    }

    /**
     * 退出
     *
     * @return result
     */
    @ApiOperation("登出")
    @PostMapping("logout")
    public Result logout() {
        return Result.ok(null);
    }
}
