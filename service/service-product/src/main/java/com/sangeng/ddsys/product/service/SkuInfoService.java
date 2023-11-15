package com.sangeng.ddsys.product.service;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sangeng.ddsys.model.product.SkuInfo;
import com.sangeng.ddsys.vo.product.SkuInfoQueryVo;
import com.sangeng.ddsys.vo.product.SkuInfoVo;

/**
 * <p>
 * sku信息 服务类
 * </p>
 *
 * @author calos
 * @since 2023-11-03
 */
public interface SkuInfoService extends IService<SkuInfo> {

    IPage<SkuInfo> selectPage(Page<SkuInfo> pageParam, SkuInfoQueryVo skuInfoQueryVo);

    void saveSkuInfo(SkuInfoVo skuInfoVo);

    SkuInfoVo getSkuInfoVo(Long id);

    void updateSkuInfo(SkuInfoVo skuInfoVo);

    void check(Long skuId, Integer status);

    void publish(Long skuId, Integer status);

    void isNewPerson(Long skuId, Integer status);

    List<SkuInfo> findSkuInfoList(List<Long> skuIdList);

    List<SkuInfo> findSkuInfoByKeyword(String keyword);

    List<SkuInfo> findNewPersonList();
}
