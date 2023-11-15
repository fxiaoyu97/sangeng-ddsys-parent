package com.sangeng.ddsys.user.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sangeng.ddsys.enums.UserType;
import com.sangeng.ddsys.model.user.Leader;
import com.sangeng.ddsys.model.user.User;
import com.sangeng.ddsys.model.user.UserDelivery;
import com.sangeng.ddsys.user.mapper.LeaderMapper;
import com.sangeng.ddsys.user.mapper.UserDeliveryMapper;
import com.sangeng.ddsys.user.mapper.UserMapper;
import com.sangeng.ddsys.user.service.UserService;
import com.sangeng.ddsys.vo.user.LeaderAddressVo;
import com.sangeng.ddsys.vo.user.UserLoginVo;

/**
 * @author: calos
 * @create: 2023-11-12 15:55
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private UserDeliveryMapper userDeliveryMapper;

    @Autowired
    private LeaderMapper leaderMapper;

    @Override
    public User getUserByOpenid(String openid) {
        return baseMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getOpenId, openid));
    }

    @Override
    public LeaderAddressVo getLeaderAddressVoByUserId(Long userId) {
        // 根据用户userId查询用户默认的团长id
        UserDelivery userDelivery = userDeliveryMapper.selectOne(new LambdaQueryWrapper<UserDelivery>()
            .eq(UserDelivery::getUserId, userId).eq(UserDelivery::getIsDefault, 1));
        if (userDelivery == null) {
            return null;
        }
        // 使用团长id查询leader表查询团长其他信息
        Leader leader = leaderMapper.selectById(userDelivery.getLeaderId());
        LeaderAddressVo leaderAddressVo = new LeaderAddressVo();
        BeanUtils.copyProperties(leader, leaderAddressVo);
        leaderAddressVo.setUserId(userId);
        leaderAddressVo.setLeaderId(leader.getId());
        leaderAddressVo.setLeaderName(leader.getName());
        leaderAddressVo.setLeaderPhone(leader.getPhone());
        leaderAddressVo.setWareId(userDelivery.getWareId());
        leaderAddressVo.setStorePath(leader.getStorePath());
        return leaderAddressVo;
    }

    @Override
    public UserLoginVo getUserLoginVo(Long id) {
        User user = baseMapper.selectById(id);
        UserLoginVo userLoginVo = new UserLoginVo();
        userLoginVo.setNickName(user.getNickName());
        userLoginVo.setUserId(id);
        userLoginVo.setPhotoUrl(user.getPhotoUrl());
        userLoginVo.setOpenId(user.getOpenId());
        userLoginVo.setIsNew(user.getIsNew());

        // 如果是团长获取当前前团长id与对应的仓库id
        if (user.getUserType() == UserType.LEADER) {
            // TODO 如果是团长获取当前前团长id与对应的仓库id
        } else {
            UserDelivery userDelivery = userDeliveryMapper.selectOne(new LambdaQueryWrapper<UserDelivery>()
                .eq(UserDelivery::getUserId, id).eq(UserDelivery::getIsDefault, 1));
            if (userDelivery != null) {
                userLoginVo.setLeaderId(userDelivery.getLeaderId());
                userLoginVo.setWareId(userDelivery.getWareId());
            } else {
                userLoginVo.setLeaderId(1L);
                userLoginVo.setWareId(1L);
            }
        }

        return null;
    }
}
