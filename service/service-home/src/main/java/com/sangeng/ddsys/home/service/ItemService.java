package com.sangeng.ddsys.home.service;

import java.util.Map;

/**
 * @author: calos
 * @version: 1.0.0
 * @createTime: 2023/11/15 18:54
 **/
public interface ItemService {
    Map<String, Object> item(Long id, Long userId);
}
