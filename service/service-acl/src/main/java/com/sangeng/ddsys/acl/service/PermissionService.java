package com.sangeng.ddsys.acl.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sangeng.ddsys.model.acl.Permission;

/**
 * @author calos
 * @version 1.0.0
 * @createTime 2023/11/2 11:26
 **/
public interface PermissionService extends IService<Permission> {
    /**
     * 查看某个角色的权限列表
     * 
     * @param roleId 角色id
     * @return 查询结果
     */
    List<Permission> queryByRoleId(Long roleId);

    /**
     * 递归删除菜单
     *
     * @param id 菜单的id
     */
    void removeChildById(Long id);
}
