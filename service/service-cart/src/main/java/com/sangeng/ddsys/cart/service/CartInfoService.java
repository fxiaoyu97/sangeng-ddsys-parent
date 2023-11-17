package com.sangeng.ddsys.cart.service;

import java.util.List;

import com.sangeng.ddsys.model.order.CartInfo;

/**
 * @author: calos
 * @version: 1.0.0
 * @createTime: 2023/11/15 21:40
 **/
public interface CartInfoService {
    void addToCart(Long skuId, Long userId, Integer skuNum);

    void deleteCart(Long skuId, Long userId);

    void deleteAllCart(Long userId);

    void batchDeleteCart(List<Long> skuIdList, Long userId);

    List<CartInfo> getCartList(Long userId);

    void checkCart(Long userId, Integer isChecked, Long skuId);

    void checkAllCart(Long userId, Integer isChecked);

    void batchCheckCart(List<Long> skuIdList, Long userId, Integer isChecked);

    List<CartInfo> getCartCheckedList(Long userId);

    void deleteCartChecked(Long userId);
}
