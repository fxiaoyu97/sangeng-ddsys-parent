package com.sangeng.ddsys.acl.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sangeng.ddsys.acl.mapper.AdminRoleMapper;
import com.sangeng.ddsys.acl.service.AdminRoleService;
import com.sangeng.ddsys.model.acl.AdminRole;
import org.springframework.stereotype.Service;

/**
 * @author: calos
 * @create: 2023-10-29 16:36
 */
@Service
public class AdminRoleServiceImpl extends ServiceImpl<AdminRoleMapper, AdminRole> implements AdminRoleService {
}
