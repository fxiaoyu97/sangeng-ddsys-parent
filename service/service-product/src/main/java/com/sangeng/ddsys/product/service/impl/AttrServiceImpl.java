package com.sangeng.ddsys.product.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sangeng.ddsys.model.product.Attr;
import com.sangeng.ddsys.product.mapper.AttrMapper;
import com.sangeng.ddsys.product.service.AttrService;

/**
 * <p>
 * 商品属性 服务实现类
 * </p>
 *
 * @author calos
 * @since 2023-11-03
 */
@Service
public class AttrServiceImpl extends ServiceImpl<AttrMapper, Attr> implements AttrService {
    @Override
    public List<Attr> findByAttrGroupId(Long attrGroupId) {
        LambdaQueryWrapper<Attr> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Attr::getAttrGroupId, attrGroupId);
        return baseMapper.selectList(wrapper);
    }
}
