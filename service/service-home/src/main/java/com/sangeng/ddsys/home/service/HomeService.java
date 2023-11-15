package com.sangeng.ddsys.home.service;

import java.util.Map;

/**
 * @author: calos
 * @version: 1.0.0
 * @createTime: 2023/11/15 8:21
 **/
public interface HomeService {
    // 首页数据
    Map<String, Object> home(Long userId);
}
