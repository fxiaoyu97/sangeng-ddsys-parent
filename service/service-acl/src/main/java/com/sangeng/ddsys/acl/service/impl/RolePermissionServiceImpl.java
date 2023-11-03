package com.sangeng.ddsys.acl.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sangeng.ddsys.acl.mapper.RolePermissionMapper;
import com.sangeng.ddsys.acl.service.RolePermissionService;
import com.sangeng.ddsys.model.acl.RolePermission;

/**
 * @author: calos
 * @version: 1.0.0
 * @createTime: 2023/11/3 10:44
 **/
@Service
public class RolePermissionServiceImpl extends ServiceImpl<RolePermissionMapper, RolePermission>
    implements RolePermissionService {}
