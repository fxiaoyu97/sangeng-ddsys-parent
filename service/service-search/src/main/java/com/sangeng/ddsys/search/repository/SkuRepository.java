package com.sangeng.ddsys.search.repository;

import com.sangeng.ddsys.model.search.SkuEs;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author: calos
 * @create: 2023-11-05 12:58
 */
public interface SkuRepository extends ElasticsearchRepository<SkuEs, Long> {
}
