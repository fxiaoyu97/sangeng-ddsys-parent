package com.sangeng.ddsys.client.search;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.sangeng.ddsys.model.search.SkuEs;

/**
 * @author: calos
 * @version: 1.0.0
 * @createTime: 2023/11/15 8:52
 **/
@FeignClient("service-search")
public interface SearchFeignClient {
    @GetMapping("/api/search/sku/inner/findHotSkuList")
    List<SkuEs> findHotSkuList();

    /**
     * 更新商品incrHotScore
     * 
     * @param skuId
     * @return
     */
    @GetMapping("/api/search/sku/inner/incrHotScore/{skuId}")
    Boolean incrHotScore(@PathVariable("skuId") Long skuId);
}
