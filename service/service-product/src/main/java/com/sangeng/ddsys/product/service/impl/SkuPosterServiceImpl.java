package com.sangeng.ddsys.product.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sangeng.ddsys.model.product.SkuPoster;
import com.sangeng.ddsys.product.mapper.SkuPosterMapper;
import com.sangeng.ddsys.product.service.SkuPosterService;

/**
 * <p>
 * 商品海报表 服务实现类
 * </p>
 *
 * @author calos
 * @since 2023-11-03
 */
@Service
public class SkuPosterServiceImpl extends ServiceImpl<SkuPosterMapper, SkuPoster> implements SkuPosterService {
    @Override
    public List<SkuPoster> findBySkuId(Long id) {
        LambdaQueryWrapper<SkuPoster> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SkuPoster::getSkuId, id);
        return baseMapper.selectList(queryWrapper);
    }
}
