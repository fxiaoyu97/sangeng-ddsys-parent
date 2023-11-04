package com.sangeng.ddsys.product.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sangeng.ddsys.model.product.AttrGroup;
import com.sangeng.ddsys.product.mapper.AttrGroupMapper;
import com.sangeng.ddsys.product.service.AttrGroupService;
import com.sangeng.ddsys.vo.product.AttrGroupQueryVo;

/**
 * <p>
 * 属性分组 服务实现类
 * </p>
 *
 * @author calos
 * @since 2023-11-03
 */
@Service
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupMapper, AttrGroup> implements AttrGroupService {
    @Override
    public IPage<AttrGroup> selectPage(Page<AttrGroup> pageParam, AttrGroupQueryVo attrGroupQueryVo) {
        String name = attrGroupQueryVo.getName();
        LambdaQueryWrapper<AttrGroup> wrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(name)) {
            wrapper.like(AttrGroup::getName, name);
        }
        return baseMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public Object findAllList() {
        QueryWrapper<AttrGroup> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("id");
        return baseMapper.selectList(wrapper);
    }
}
