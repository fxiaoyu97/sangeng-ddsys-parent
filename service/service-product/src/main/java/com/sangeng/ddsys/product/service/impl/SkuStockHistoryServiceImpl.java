package com.sangeng.ddsys.product.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sangeng.ddsys.model.product.SkuStockHistory;
import com.sangeng.ddsys.product.mapper.SkuStockHistoryMapper;
import com.sangeng.ddsys.product.service.SkuStockHistoryService;

/**
 * <p>
 * sku的库存历史记录 服务实现类
 * </p>
 *
 * @author calos
 * @since 2023-11-03
 */
@Service
public class SkuStockHistoryServiceImpl extends ServiceImpl<SkuStockHistoryMapper, SkuStockHistory>
    implements SkuStockHistoryService {

}
