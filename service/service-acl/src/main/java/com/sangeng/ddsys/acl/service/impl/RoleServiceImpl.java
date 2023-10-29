package com.sangeng.ddsys.acl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sangeng.ddsys.acl.mapper.RoleMapper;
import com.sangeng.ddsys.acl.service.RoleService;
import com.sangeng.ddsys.model.acl.Role;
import com.sangeng.ddsys.vo.acl.RoleQueryVo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @author: calos
 * @version: 1.0.0
 * @createTime: 2023/10/27 18:47
 **/
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
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

        return null;
    }
}
