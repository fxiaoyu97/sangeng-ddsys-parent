package com.sangeng.ddsys.acl.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.sangeng.ddsys.acl.service.PermissionService;
import com.sangeng.ddsys.common.result.Result;
import com.sangeng.ddsys.model.acl.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author: calos
 * @version: 1.0.0
 * @createTime: 2023/10/31 10:32
 **/
@Api(tags = "菜单管理")
@RestController
@RequestMapping("/admin/acl/permission")
@CrossOrigin
public class PermissionController {
    @Autowired
    private PermissionService permissionService;

    // 查询所有的菜单
    @ApiOperation("查询所有菜单")
    @GetMapping
    public Result list() {
        List<Permission> list = permissionService.list();
        return Result.ok(list);
    }

    /**
     * 查看某个角色的权限列表
     * 
     * @return 某个角色的权限列表
     */
    @ApiOperation("查看某个角色的权限列表")
    @GetMapping("toAssign/${roleId}")
    public Result list(@PathVariable Long roleId) {
        List<Permission> list = permissionService.queryByRoleId(roleId);
        return Result.ok(list);
    }

    // 添加菜单
    @ApiOperation("添加菜单")
    @PostMapping("save")
    public Result save(@RequestBody Permission permission) {
        permissionService.save(permission);
        return Result.ok(null);
    }

    // 修改菜单

    @ApiOperation("修改菜单")
    @PutMapping("update")
    public Result update(@RequestBody Permission permission) {
        permissionService.updateById(permission);
        return Result.ok(null);
    }

    // 递归删除菜单
    @ApiOperation("删除菜单")
    @DeleteMapping("remove/{id}")
    public Result delete(@PathVariable Long id) {
        permissionService.removeChildById(id);
        return Result.ok(null);
    }
}
