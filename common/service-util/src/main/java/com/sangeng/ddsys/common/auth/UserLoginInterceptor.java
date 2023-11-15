package com.sangeng.ddsys.common.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import com.sangeng.ddsys.common.constant.RedisConst;
import com.sangeng.ddsys.common.utils.JwtHelper;
import com.sangeng.ddsys.vo.user.UserLoginVo;

/**
 * @author: calos
 * @version: 1.0.0
 * @createTime: 2023/11/13 22:40
 **/
public class UserLoginInterceptor implements HandlerInterceptor {

    private RedisTemplate redisTemplate;

    public UserLoginInterceptor(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {
        this.getUserLoginVo(request);
        return true;
    }

    private void getUserLoginVo(HttpServletRequest request) {
        // 从请求头中获取token
        String token = request.getHeader("token");
        // 判断token不为空
        if (!StringUtils.isEmpty(token)) {
            // 从token获取userId
            Long userId = JwtHelper.getUserId(token);
            // 根据userId到Redis中获取用户信息
            UserLoginVo userLoginVo =
                (UserLoginVo)redisTemplate.opsForValue().get(RedisConst.USER_LOGIN_KEY_PREFIX + userId);
            // 获取数据放到ThreadLocal中
            if (userLoginVo != null) {
                AuthContextHolder.setUserId(userLoginVo.getUserId());
                AuthContextHolder.setWareId(userLoginVo.getWareId());
                AuthContextHolder.setUserLoginVo(userLoginVo);
            }
        }
    }
}
