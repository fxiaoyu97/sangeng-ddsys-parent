package com.sangeng.ddsys.product.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sangeng.ddsys.model.product.SkuImage;
import com.sangeng.ddsys.product.mapper.SkuImageMapper;
import com.sangeng.ddsys.product.service.SkuImageService;

/**
 * <p>
 * 商品图片 服务实现类
 * </p>
 *
 * @author calos
 * @since 2023-11-03
 */
@Service
public class SkuImageServiceImpl extends ServiceImpl<SkuImageMapper, SkuImage> implements SkuImageService {
    @Override
    public List<SkuImage> findBySkuId(Long id) {
        LambdaQueryWrapper<SkuImage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SkuImage::getSkuId, id);
        return baseMapper.selectList(queryWrapper);
    }
}
