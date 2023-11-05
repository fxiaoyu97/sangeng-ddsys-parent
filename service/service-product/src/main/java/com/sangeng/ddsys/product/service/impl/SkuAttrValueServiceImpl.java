package com.sangeng.ddsys.product.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sangeng.ddsys.model.product.SkuAttrValue;
import com.sangeng.ddsys.product.mapper.SkuAttrValueMapper;
import com.sangeng.ddsys.product.service.SkuAttrValueService;

/**
 * <p>
 * spu属性值 服务实现类
 * </p>
 *
 * @author calos
 * @since 2023-11-03
 */
@Service
public class SkuAttrValueServiceImpl extends ServiceImpl<SkuAttrValueMapper, SkuAttrValue>
    implements SkuAttrValueService {
    @Override
    public List<SkuAttrValue> findBySkuId(Long id) {
        LambdaQueryWrapper<SkuAttrValue> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SkuAttrValue::getSkuId, id);
        return baseMapper.selectList(queryWrapper);
    }
}
