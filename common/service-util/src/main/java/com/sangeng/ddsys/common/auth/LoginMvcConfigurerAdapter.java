package com.sangeng.ddsys.common.auth;

import javax.annotation.Resource;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * @author: calos
 * @version: 1.0.0
 * @createTime: 2023/11/15 8:10
 **/

@Configuration
public class LoginMvcConfigurerAdapter extends WebMvcConfigurationSupport {
    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加自定义拦截器，设置路径
        registry.addInterceptor(new UserLoginInterceptor(redisTemplate)).addPathPatterns("/api/**")
            .excludePathPatterns("/api/user/weixin/wxLogin/*");
        super.addInterceptors(registry);
    }
}
