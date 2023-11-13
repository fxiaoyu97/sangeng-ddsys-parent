package com.sangeng.ddsys.user.utils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author: calos
 * @create: 2023-11-12 12:32
 */

@Component
public class ConstantPropertiesUtil implements InitializingBean {

    public static String WX_OPEN_APP_ID;
    public static String WX_OPEN_APP_SECRET;
    @Value("${wx.open.app_id}")
    private String appId;
    @Value("${wx.open.app_secret}")
    private String appSecret;

    @Override
    public void afterPropertiesSet() throws Exception {
        WX_OPEN_APP_ID = appId;
        WX_OPEN_APP_SECRET = appSecret;
    }
}
