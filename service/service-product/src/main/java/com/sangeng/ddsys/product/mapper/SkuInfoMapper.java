package com.sangeng.ddsys.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sangeng.ddsys.model.product.SkuInfo;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * sku信息 Mapper 接口
 * </p>
 *
 * @author calos
 * @since 2023-11-03
 */
public interface SkuInfoMapper extends BaseMapper<SkuInfo> {

    SkuInfo checkStock(@Param("skuId") Long skuId, @Param("skuNum") Integer skuNum);

    Integer lockStock(@Param("skuId") Long skuId, @Param("skuNum") Integer skuNum);

    Integer unlockStock(@Param("skuId") Long skuId, @Param("skuNum") Integer skuNum);
}
