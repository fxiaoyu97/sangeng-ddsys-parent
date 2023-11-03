package com.sangeng.ddsys.acl.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sangeng.ddsys.acl.mapper.PermissionMapper;
import com.sangeng.ddsys.acl.service.PermissionService;
import com.sangeng.ddsys.acl.service.RolePermissionService;
import com.sangeng.ddsys.model.acl.Permission;
import com.sangeng.ddsys.model.acl.RolePermission;

/**
 * @author: calos
 * @version: 1.0.0
 * @createTime: 2023/10/31 10:34
 **/
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {
    @Autowired
    private RolePermissionService rolePermissionService;

    @Override
    public List<Permission> queryByRoleId(Long roleId) {
        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermission::getRoleId, roleId);
        List<RolePermission> rolePermissionList = rolePermissionService.list(wrapper);
        ArrayList<Long> permissionIdList = new ArrayList<>();
        rolePermissionList.forEach(e -> permissionIdList.add(e.getPermissionId()));
        return this.listByIds(permissionIdList);
    }

    @Override
    public void removeChildById(Long id) {
        ArrayList<Long> idList = new ArrayList<>();
        this.getAllPermissionIds(id, idList);
        baseMapper.deleteBatchIds(idList);
    }

    /**
     * 递归查找当前菜单下的所有子菜单
     * 
     * @param id 当前菜单id
     * @param idList 存放所有的菜单id
     */
    private void getAllPermissionIds(Long id, List<Long> idList) {
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getPid, id);
        List<Permission> childIdList = baseMapper.selectList(wrapper);
        childIdList.forEach(item -> {
            idList.add(item.getId());
            this.getAllPermissionIds(item.getId(), idList);
        });
    }
}
