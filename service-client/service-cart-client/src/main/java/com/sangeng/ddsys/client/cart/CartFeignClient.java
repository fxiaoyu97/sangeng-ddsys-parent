package com.sangeng.ddsys.client.cart;

import com.sangeng.ddsys.model.order.CartInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author: calos
 * @create: 2023-11-17 11:10
 */
@FeignClient(value = "service-cart")
public interface CartFeignClient {
    /**
     * 根据用户Id 查询购物车列表
     *
     * @param userId
     */
    @GetMapping("/api/cart/inner/getCartCheckedList/{userId}")
    List<CartInfo> getCartCheckedList(@PathVariable("userId") Long userId);
}
