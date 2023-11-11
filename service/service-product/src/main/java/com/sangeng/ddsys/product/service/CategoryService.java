package com.sangeng.ddsys.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sangeng.ddsys.model.product.Category;
import com.sangeng.ddsys.vo.product.CategoryQueryVo;

/**
 * <p>
 * 商品三级分类 服务类
 * </p>
 *
 * @author calos
 * @since 2023-11-03
 */
public interface CategoryService extends IService<Category> {
    /**
     * 商品分类分页列表
     *
     * @param pageParam       分页参数
     * @param categoryQueryVo 查询参数
     * @return 查询结果
     */
    IPage<Category> selectPage(Page<Category> pageParam, CategoryQueryVo categoryQueryVo);

    /**
     * 查询所有商品分类
     *
     * @return 查询结果
     */
    Object findAllList();
}
