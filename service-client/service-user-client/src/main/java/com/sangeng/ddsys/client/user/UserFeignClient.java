package com.sangeng.ddsys.client.user;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.sangeng.ddsys.vo.user.LeaderAddressVo;

/**
 * @author: calos
 * @version: 1.0.0
 * @createTime: 2023/11/15 8:33
 **/
@FeignClient("service-user")
public interface UserFeignClient {

    @GetMapping("/api/user/leader/inner/getUserAddressByUserId/{userId}")
    LeaderAddressVo getUserAddressByUserId(@PathVariable(value = "userId") Long userId);

}
