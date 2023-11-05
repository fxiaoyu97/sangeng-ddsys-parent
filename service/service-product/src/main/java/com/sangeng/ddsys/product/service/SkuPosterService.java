package com.sangeng.ddsys.product.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sangeng.ddsys.model.product.SkuPoster;

/**
 * <p>
 * 商品海报表 服务类
 * </p>
 *
 * @author calos
 * @since 2023-11-03
 */
public interface SkuPosterService extends IService<SkuPoster> {

    List<SkuPoster> findBySkuId(Long id);
}
