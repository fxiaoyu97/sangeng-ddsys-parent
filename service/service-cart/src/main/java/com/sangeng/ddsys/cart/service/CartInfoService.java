package com.sangeng.ddsys.cart.service;

/**
 * @author: calos
 * @version: 1.0.0
 * @createTime: 2023/11/15 21:40
 **/
public interface CartInfoService {
    void addToCart(Long skuId, Long userId, Integer skuNum);
}
