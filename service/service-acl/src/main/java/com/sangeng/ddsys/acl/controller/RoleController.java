package com.sangeng.ddsys.acl.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sangeng.ddsys.acl.service.RoleService;
import com.sangeng.ddsys.common.result.Result;
import com.sangeng.ddsys.model.acl.Role;
import com.sangeng.ddsys.vo.acl.RoleQueryVo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author: calos
 * @version: 1.0.0
 * @createTime: 2023/10/27 18:44
 **/
@Api(tags = "角色接口")
@RestController
@RequestMapping("/admin/acl/role")
@CrossOrigin
public class RoleController {

    @Autowired
    public RoleService roleService;

    /**
     * 角色条件分页查询
     * 
     * @param current 当前页
     * @param limit 每页数量
     * @param roleQueryVo 查询条件
     * @return 查询结果
     */
    @ApiOperation("角色条件分页查询")
    @GetMapping("{current}/{limit}")
    public Result pageList(@PathVariable final Long current, @PathVariable final Long limit,
        final RoleQueryVo roleQueryVo) {
        // 1、创建page对象，传递当前页记录数
        // current 当前页
        // limit 每页显示记录数
        Page<Role> pageParam = new Page<>(current, limit);

        // 2、调用service方法实现条件分页查询，返回分页对象
        final IPage<Role> pageModel = roleService.selectRolePage(pageParam, roleQueryVo);
        return Result.ok(pageModel);
    }
}
