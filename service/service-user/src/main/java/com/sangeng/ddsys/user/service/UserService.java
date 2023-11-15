package com.sangeng.ddsys.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sangeng.ddsys.model.user.User;
import com.sangeng.ddsys.vo.user.LeaderAddressVo;
import com.sangeng.ddsys.vo.user.UserLoginVo;

public interface UserService extends IService<User> {
    User getUserByOpenid(String openid);

    LeaderAddressVo getLeaderAddressVoByUserId(Long id);

    UserLoginVo getUserLoginVo(Long id);
}
