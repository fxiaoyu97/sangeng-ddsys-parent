package com.sangeng.ddsys.product.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sangeng.ddsys.model.product.Category;
import com.sangeng.ddsys.product.mapper.CategoryMapper;
import com.sangeng.ddsys.product.service.CategoryService;
import com.sangeng.ddsys.vo.product.CategoryQueryVo;

/**
 * <p>
 * 商品三级分类 服务实现类
 * </p>
 *
 * @author calos
 * @since 2023-11-03
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Override
    public IPage<Category> selectPage(Page<Category> pageParam, CategoryQueryVo categoryQueryVo) {
        String name = categoryQueryVo.getName();
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(name)) {
            queryWrapper.like(Category::getName, name);
        }
        return baseMapper.selectPage(pageParam, queryWrapper);
    }

    @Override
    public List<Category> findAllList() {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getSort);
        return this.list(queryWrapper);
    }
}
