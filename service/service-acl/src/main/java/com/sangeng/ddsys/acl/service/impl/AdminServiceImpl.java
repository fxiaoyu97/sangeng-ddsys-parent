package com.sangeng.ddsys.acl.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sangeng.ddsys.acl.mapper.AdminMapper;
import com.sangeng.ddsys.acl.service.AdminService;
import com.sangeng.ddsys.model.acl.Admin;
import org.springframework.stereotype.Service;

/**
 * @author: calos
 * @create: 2023-10-29 11:45
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {
}
