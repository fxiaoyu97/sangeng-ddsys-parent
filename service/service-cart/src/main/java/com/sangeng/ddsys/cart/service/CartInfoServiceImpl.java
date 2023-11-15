package com.sangeng.ddsys.cart.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.sangeng.ddsys.common.constant.RedisConst;

/**
 * @author: calos
 * @version: 1.0.0
 * @createTime: 2023/11/15 21:40
 **/
@Service
public class CartInfoServiceImpl implements CartInfoService {
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void addToCart(Long skuId, Long userId, Integer skuNum) {
        // 购物车数据存储在Redis里面，从redis里面根据key获取数据，这个key包含userId

        // 根据第一步查询的结果，得到skuId+skuNum关系
        // 判断是否是第一次添加商品到购物车，进行判断，判断结果里面，是否有skuId

        // 如果结果里面包含skuId，不是第一次添加，根据skuId，获取对应数量，更新数量

        // 如果结果中没有skuId，就是第一次添加，直接进行添加

        // 更新redis缓存

        // 设置有效时间

    }

    private String getCartKey(Long userId) {
        // 定义key user:userId:cart
        return RedisConst.USER_KEY_PREFIX + userId + RedisConst.USER_CART_KEY_SUFFIX;
    }
}
