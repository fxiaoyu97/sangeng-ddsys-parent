package com.sangeng.ddsys.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sangeng.ddsys.model.product.AttrGroup;
import com.sangeng.ddsys.vo.product.AttrGroupQueryVo;

/**
 * <p>
 * 属性分组 服务类
 * </p>
 *
 * @author calos
 * @since 2023-11-03
 */
public interface AttrGroupService extends IService<AttrGroup> {
    /**
     * 平台属性分组列表
     *
     * @param pageParam
     * @param attrGroupQueryVo
     * @return
     */
    IPage<AttrGroup> selectPage(Page<AttrGroup> pageParam, AttrGroupQueryVo attrGroupQueryVo);

    Object findAllList();
}
