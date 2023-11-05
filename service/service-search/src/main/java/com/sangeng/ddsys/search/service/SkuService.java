package com.sangeng.ddsys.search.service;

public interface SkuService {

    /**
     * 上架商品列表
     *
     * @param skuId
     */
    void upperSku(Long skuId);

    /**
     * 下架商品列表
     *
     * @param skuId
     */
    void lowerSku(Long skuId);
}
