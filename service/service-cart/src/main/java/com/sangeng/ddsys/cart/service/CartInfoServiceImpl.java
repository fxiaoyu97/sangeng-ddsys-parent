package com.sangeng.ddsys.cart.service;

import com.sangeng.ddsys.client.product.ProductFeignClient;
import com.sangeng.ddsys.common.constant.RedisConst;
import com.sangeng.ddsys.common.exception.DdsysException;
import com.sangeng.ddsys.common.result.ResultCodeEnum;
import com.sangeng.ddsys.enums.SkuType;
import com.sangeng.ddsys.model.order.CartInfo;
import com.sangeng.ddsys.model.product.SkuInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author: calos
 * @version: 1.0.0
 * @createTime: 2023/11/15 21:40
 **/
@Service
public class CartInfoServiceImpl implements CartInfoService {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Override
    public void addToCart(Long skuId, Long userId, Integer skuNum) {
        // 购物车数据存储在Redis里面，从redis里面根据key获取数据，这个key包含userId
        String cartKey = this.getCartKey(userId);
        BoundHashOperations<String, String, CartInfo> hashOperations = redisTemplate.boundHashOps(cartKey);
        CartInfo cartInfo;
        // 根据第一步查询的结果，得到skuId+skuNum关系
        // 判断是否是第一次添加商品到购物车，进行判断，判断结果里面，是否有skuId
        if (Boolean.TRUE.equals(hashOperations.hasKey(skuId.toString()))) {
            // 如果结果里面包含skuId，不是第一次添加，根据skuId，获取对应数量，更新数量
            cartInfo = hashOperations.get(skuId.toString());
            Integer currentSkuNum = cartInfo.getSkuNum() + skuNum;
            if (currentSkuNum < 1) {
                return;
            }
            // 更新cartInfo对象
            cartInfo.setSkuNum(currentSkuNum);
            // 预留字段
            cartInfo.setCurrentBuyNum(currentSkuNum);
            Integer perLimit = cartInfo.getPerLimit();
            // 判断商品数量不能大于限购数量
            if (currentSkuNum > perLimit) {
                throw new DdsysException(ResultCodeEnum.SKU_LIMIT_ERROR);
            }
            // 购物车中的商品是否选中
            cartInfo.setIsChecked(1);
            cartInfo.setUpdateTime(new Date());
        } else {
            // 如果结果中没有skuId，就是第一次添加，直接进行添加
            // 第一次添加只能添加一个
            skuNum = 1;

            // 当购物车中没用该商品的时候，则直接添加到购物车！insert
            cartInfo = new CartInfo();
            // 购物车数据是从商品详情得到 {skuInfo}
            SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
            if (null == skuInfo) {
                throw new DdsysException(ResultCodeEnum.DATA_ERROR);
            }
            cartInfo.setSkuId(skuId);
            cartInfo.setCategoryId(skuInfo.getCategoryId());
            cartInfo.setSkuType(skuInfo.getSkuType());
            cartInfo.setIsNewPerson(skuInfo.getIsNewPerson());
            cartInfo.setUserId(userId);
            cartInfo.setCartPrice(skuInfo.getPrice());
            cartInfo.setSkuNum(skuNum);
            cartInfo.setCurrentBuyNum(skuNum);
            cartInfo.setSkuType(SkuType.COMMON.getCode());
            cartInfo.setPerLimit(skuInfo.getPerLimit());
            cartInfo.setImgUrl(skuInfo.getImgUrl());
            cartInfo.setSkuName(skuInfo.getSkuName());
            cartInfo.setWareId(skuInfo.getWareId());
            cartInfo.setIsChecked(1);
            cartInfo.setStatus(1);
            cartInfo.setCreateTime(new Date());
            cartInfo.setUpdateTime(new Date());
        }

        // 更新缓存
        hashOperations.put(skuId.toString(), cartInfo);
        // 设置过期时间
        this.setCartKeyExpire(cartKey);

    }

    private void setCartKeyExpire(String cartKey) {
        redisTemplate.expire(cartKey, RedisConst.USER_CART_EXPIRE, TimeUnit.SECONDS);
    }

    private String getCartKey(Long userId) {
        // 定义key user:userId:cart
        return RedisConst.USER_KEY_PREFIX + userId + RedisConst.USER_CART_KEY_SUFFIX;
    }

    @Override
    public void deleteCart(Long skuId, Long userId) {
        BoundHashOperations boundHashOps = this.redisTemplate.boundHashOps(this.getCartKey(userId));
        // 判断购物车中是否有该商品！
        if (Boolean.TRUE.equals(boundHashOps.hasKey(skuId.toString()))) {
            boundHashOps.delete(skuId.toString());
        }
    }

    @Override
    public void deleteAllCart(Long userId) {
        String cartKey = getCartKey(userId);
        // 获取缓存对象
        BoundHashOperations<String, String, CartInfo> hashOperations = redisTemplate.boundHashOps(cartKey);
        Objects.requireNonNull(hashOperations.values()).forEach(cartInfo -> {
            hashOperations.delete(cartInfo.getSkuId().toString());
        });
    }

    @Override
    public void batchDeleteCart(List<Long> skuIdList, Long userId) {
        String cartKey = getCartKey(userId);
        // 获取缓存对象
        BoundHashOperations<String, String, CartInfo> hashOperations = redisTemplate.boundHashOps(cartKey);
        skuIdList.forEach(skuId -> {
            hashOperations.delete(skuId.toString());
        });
    }

    /**
     * 根据用户获取购物车
     *
     * @param userId
     * @return
     */
    @Override
    public List<CartInfo> getCartList(Long userId) {
        // 什么一个返回的集合对象
        List<CartInfo> cartInfoList = new ArrayList<>();
        if (StringUtils.isEmpty(userId))
            return cartInfoList;

        // 定义key user:userId:cart
        String cartKey = this.getCartKey(userId);
        BoundHashOperations<String, String, CartInfo> hashOperations = redisTemplate.boundHashOps(cartKey);
        // 获取数据
        cartInfoList = hashOperations.values();
        if (!CollectionUtils.isEmpty(cartInfoList)) {
            // 购物车列表显示有顺序：按照商品的更新时间 降序
            cartInfoList.sort(new Comparator<CartInfo>() {
                @Override
                public int compare(CartInfo o1, CartInfo o2) {
                    // str1 = ab str2 = ac;
                    return o2.getCreateTime().compareTo(o1.getCreateTime());
                }
            });
        }
        return cartInfoList;
    }

    @Override
    public void checkCart(Long userId, Integer isChecked, Long skuId) {
        // 获取redis的key
        String cartKey = this.getCartKey(userId);
        // cartKey获取field-value
        BoundHashOperations<String, String, CartInfo> boundHashOperations = redisTemplate.boundHashOps(cartKey);
        // 根据field(skuId)获取value(CartInfo)
        CartInfo cartInfo = boundHashOperations.get(skuId.toString());
        if (cartInfo != null) {
            cartInfo.setIsChecked(isChecked);
            // 更新
            boundHashOperations.put(skuId.toString(), cartInfo);
            // 设置key过期时间
            this.setCartKeyExpire(cartKey);
        }
    }

    @Override
    public void checkAllCart(Long userId, Integer isChecked) {
        String cartKey = this.getCartKey(userId);
        BoundHashOperations<String, String, CartInfo> boundHashOps = this.redisTemplate.boundHashOps(cartKey);
        Objects.requireNonNull(boundHashOps.values()).forEach(cartInfo -> {
            cartInfo.setIsChecked(isChecked);
            boundHashOps.put(cartInfo.getSkuId().toString(), cartInfo);
        });
        this.setCartKeyExpire(cartKey);
    }

    @Override
    public void batchCheckCart(List<Long> skuIdList, Long userId, Integer isChecked) {
        String cartKey = getCartKey(userId);
        // 获取缓存对象
        BoundHashOperations<String, String, CartInfo> hashOperations = redisTemplate.boundHashOps(cartKey);
        skuIdList.forEach(skuId -> {
            CartInfo cartInfo = hashOperations.get(skuId.toString());
            cartInfo.setIsChecked(isChecked);
            hashOperations.put(cartInfo.getSkuId().toString(), cartInfo);
        });
    }

    @Override
    public List<CartInfo> getCartCheckedList(Long userId) {
        BoundHashOperations<String, String, CartInfo> boundHashOps =
            this.redisTemplate.boundHashOps(this.getCartKey(userId));
        return Objects.requireNonNull(boundHashOps.values()).stream()
            .filter((cartInfo) -> cartInfo.getIsChecked().intValue() == 1).collect(Collectors.toList());
    }
}
