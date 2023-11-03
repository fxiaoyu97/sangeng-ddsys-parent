package com.sangeng.ddsys.acl.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sangeng.ddsys.acl.mapper.RoleMapper;
import com.sangeng.ddsys.acl.service.AdminRoleService;
import com.sangeng.ddsys.acl.service.RoleService;
import com.sangeng.ddsys.model.acl.AdminRole;
import com.sangeng.ddsys.model.acl.Role;
import com.sangeng.ddsys.vo.acl.RoleQueryVo;

/**
 * @author: calos
 * @version: 1.0.0
 * @createTime: 2023/10/27 18:47
 **/
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
    @Autowired
    private AdminRoleService adminRoleService;

    @Override
    public IPage<Role> selectRolePage(Page<Role> pageParam, RoleQueryVo roleQueryVo) {
        // 获取条件值
        String roleName = roleQueryVo.getRoleName();
        // 创建mp条件对象
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        // 判断条件值是否为空，不为空则封装查询条件
        if (!StringUtils.isEmpty(roleName)) {
            // roleName like ?
            wrapper.like(Role::getRoleName, roleName);
        }
        // 调用方法实现分页查询，返回分页对象
        return baseMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public Map<String, Object> getRoleByAdminId(Long adminId) {
        // 获取所有的用户角色
        List<Role> allRoleList = baseMapper.selectList(null);
        // 根据用户id获取用户分配的角色列表
        LambdaQueryWrapper<AdminRole> wrapper = new LambdaQueryWrapper<>();
        // 设置查询条件
        wrapper.eq(AdminRole::getAdminId, adminId);
        List<AdminRole> adminRoleList = adminRoleService.list(wrapper);
        // 通过查询结果转化成只包含角色ID的集合
        List<Long> roleIdList = adminRoleList.stream().map(AdminRole::getRoleId).collect(Collectors.toList());
        // 创建新的list集合，用户存储用户配置的角色
        ArrayList<Role> assignRoleList = new ArrayList<>();
        // 遍历所有角色列表，得到每个角色
        for (Role role : allRoleList) {
            // 判断所有角色里面是否包含已经分配角色id，包含的存放到新list集合中
            if (roleIdList.contains(role.getId())) {
                assignRoleList.add(role);
            }
        }
        // 封装map
        HashMap<String, Object> result = new HashMap<>();
        result.put("allRoleList", allRoleList);
        result.put("assignRoles", assignRoleList);
        return result;
    }

    @Override
    public void saveAdminRole(Long adminId, Long[] roleIds) {
        // 1、删除用户已经分配的角色，根据用户id删除admin_role里面的数据
        LambdaQueryWrapper<AdminRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdminRole::getAdminId, adminId);
        adminRoleService.remove(wrapper);
        // 2、重新分配
        ArrayList<AdminRole> list = new ArrayList<>();
        for (Long roleId : roleIds) {
            AdminRole adminRole = new AdminRole();
            adminRole.setAdminId(adminId);
            adminRole.setRoleId(roleId);
            list.add(adminRole);
        }
        adminRoleService.saveBatch(list);
    }
}
