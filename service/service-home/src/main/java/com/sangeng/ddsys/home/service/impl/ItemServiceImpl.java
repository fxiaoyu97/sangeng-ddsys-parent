package com.sangeng.ddsys.home.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sangeng.ddsys.activity.ActivityFeignClient;
import com.sangeng.ddsys.client.product.ProductFeignClient;
import com.sangeng.ddsys.client.search.SearchFeignClient;
import com.sangeng.ddsys.home.service.ItemService;
import com.sangeng.ddsys.vo.product.SkuInfoVo;

/**
 * @author: calos
 * @version: 1.0.0
 * @createTime: 2023/11/15 18:55
 **/
@Service
public class ItemServiceImpl implements ItemService {
    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private SearchFeignClient searchFeignClient;

    @Autowired
    private ActivityFeignClient activityFeignClient;

    @Override
    public Map<String, Object> item(Long id, Long userId) {
        Map<String, Object> result = new HashMap<>();
        // skuId查询
        CompletableFuture<SkuInfoVo> skuInfoVoCompletableFuture = CompletableFuture.supplyAsync(() -> {
            // 远程调用获取sku对应数据
            SkuInfoVo skuInfoVo = productFeignClient.getSkuInfoVo(id);
            result.put("skuInfoVo", skuInfoVo);
            return skuInfoVo;
        }, threadPoolExecutor);
        // sku对应优惠券信息
        CompletableFuture<Void> activityCompletableFuture = CompletableFuture.runAsync(() -> {
            // 远程调用获取优惠券
            Map<String, Object> activityMap = activityFeignClient.findActivityAndCoupon(id, userId);
            result.putAll(activityMap);
        }, threadPoolExecutor);
        // 更新商品热度
        CompletableFuture<Void> hotCompletableFuture = CompletableFuture.runAsync(() -> {
            // 远程调用更新热度
            searchFeignClient.incrHotScore(id);
        }, threadPoolExecutor);
        // 任务组合
        CompletableFuture.allOf(skuInfoVoCompletableFuture, activityCompletableFuture, hotCompletableFuture).join();
        return result;
    }
}
