package com.sangeng.ddsys.acl.service;

import java.util.Map;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sangeng.ddsys.model.acl.Role;
import com.sangeng.ddsys.vo.acl.RoleQueryVo;

/**
 * @author calos
 * @version 1.0.0
 * @createTime 2023/10/27 18:46
 **/
public interface RoleService extends IService<Role> {
    /**
     * 角色列表，条件分页查询
     *
     * @param pageParam 分页条件
     * @param roleQueryVo 查询条件
     * @return 查询结果
     */
    IPage<Role> selectRolePage(Page<Role> pageParam, RoleQueryVo roleQueryVo);

    Map<String, Object> getRoleByAdminId(Long adminId);

    /**
     * 为用户分配角色
     * 
     * @param adminId 用户id
     * @param roleId 角色Id
     */
    void saveAdminRole(Long adminId, Long[] roleId);
}
