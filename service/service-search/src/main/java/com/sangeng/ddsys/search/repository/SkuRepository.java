package com.sangeng.ddsys.search.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.sangeng.ddsys.model.search.SkuEs;

/**
 * @author: calos
 * @create: 2023-11-05 12:58
 */
public interface SkuRepository extends ElasticsearchRepository<SkuEs, Long> {
    // 获取爆品商品
    Page<SkuEs> findByOrderByHotScoreDesc(Pageable page);

    Page<SkuEs> findByCategoryIdAndWareId(Long categoryId, Long wareId, Pageable pageable);

    Page<SkuEs> findByKeywordAndWareId(String keyword, Long wareId, Pageable pageable);
}
