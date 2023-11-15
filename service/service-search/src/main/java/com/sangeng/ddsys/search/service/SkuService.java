package com.sangeng.ddsys.search.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sangeng.ddsys.model.search.SkuEs;
import com.sangeng.ddsys.vo.search.SkuEsQueryVo;

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

    List<SkuEs> findHotSkuList();

    Page<SkuEs> search(Pageable pageable, SkuEsQueryVo skuEsQueryVo);

    void incrHotScore(Long skuId);
}
