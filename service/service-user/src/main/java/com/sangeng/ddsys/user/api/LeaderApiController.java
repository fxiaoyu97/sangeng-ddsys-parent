package com.sangeng.ddsys.user.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sangeng.ddsys.user.service.UserService;
import com.sangeng.ddsys.vo.user.LeaderAddressVo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author: calos
 * @version: 1.0.0
 * @createTime: 2023/11/15 8:30
 **/
@Api(tags = "团长接口")
@RestController
@RequestMapping("/api/user/leader")
public class LeaderApiController {

    @Autowired
    private UserService userService;

    @ApiOperation("提货点地址信息")
    @GetMapping("/inner/getUserAddressByUserId/{userId}")
    public LeaderAddressVo getUserAddressByUserId(@PathVariable(value = "userId") Long userId) {
        return userService.getLeaderAddressVoByUserId(userId);
    }
}