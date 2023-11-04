package com.sangeng.ddsys.product.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sangeng.ddsys.model.product.Attr;

/**
 * <p>
 * 商品属性 服务类
 * </p>
 *
 * @author calos
 * @since 2023-11-03
 */
public interface AttrService extends IService<Attr> {

    /**
     * 根据属性分组id 获取属性列表
     * 
     * @param attrGroupId 分组id
     * @return 属性结果
     */
    List<Attr> findByAttrGroupId(Long attrGroupId);
}
