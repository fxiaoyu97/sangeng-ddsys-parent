package com.sangeng.ddsys.acl.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sangeng.ddsys.acl.service.AdminService;
import com.sangeng.ddsys.acl.service.RoleService;
import com.sangeng.ddsys.common.result.Result;
import com.sangeng.ddsys.common.utils.MD5;
import com.sangeng.ddsys.model.acl.Admin;
import com.sangeng.ddsys.vo.acl.AdminQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author: calos
 * @create: 2023-10-29 11:43
 */
@Api(tags = "用户接口")
@RestController
@RequestMapping("/admin/acl/user")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @Autowired
    private RoleService roleService;

    // 为用户进行角色分配
    @ApiOperation("为用户进行角色分配")
    @PostMapping("doAssign")
    public Result doAssign(@RequestParam Long adminId, @RequestParam Long[] roleId) {
        roleService.saveAdminRole(adminId, roleId);
        return Result.ok(null);
    }

    // 获取所有角色和根据用户id获取角色
    @ApiOperation("获取用户角色")
    @GetMapping("/toAssign/{adminId}")
    public Result toAssign(@PathVariable Long adminId) {
        Map<String, Object> result = roleService.getRoleByAdminId(adminId);
        return Result.ok(result);
    }

    // 查询用户，条件分页查询
    @ApiOperation("分页条件查询用户")
    @GetMapping("/{page}/{limit}")
    public Result get(@PathVariable Long page, @PathVariable Long limit, AdminQueryVo adminQueryVo) {
        Page<Admin> adminPage = new Page<>(page, limit);
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(adminQueryVo.getName())) {
            wrapper.like(Admin::getName, adminQueryVo.getName());
        }
        if (!StringUtils.isEmpty(adminQueryVo.getUsername())) {
            wrapper.like(Admin::getUsername, adminQueryVo.getUsername());
        }
        Page<Admin> result = adminService.page(adminPage, wrapper);
        return Result.ok(result);

    }

    // 根据id 查询用户
    @ApiOperation("根据id查询用户")
    @GetMapping("get/{id}")
    public Result getById(@PathVariable Long id) {
        return Result.ok(adminService.getById(id));
    }

    // 添加用户
    @ApiOperation("添加用户")
    @PostMapping("save")
    public Result save(@RequestBody Admin admin) {
        // 获取用户输入的密码
        String password = admin.getPassword();
        // 对密码进行加密
        String encryptPwd = MD5.encrypt(password);
        // 设置到对象中
        admin.setPassword(encryptPwd);
        // 添加到数据库中
        boolean isSuccess = adminService.save(admin);
        if (isSuccess) {
            return Result.ok(null);
        } else {
            return Result.fail(null);
        }
    }

    // 修改用户
    @ApiOperation("修改用户")
    @PutMapping("update")
    public Result update(@RequestBody Admin admin) {
        boolean isSuccess = adminService.updateById(admin);
        if (isSuccess) {
            return Result.ok(null);
        } else {
            return Result.fail(null);
        }
    }

    // 根据ID删除用户

    @ApiOperation("根据Id删除用户")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        boolean isSuccess = adminService.removeById(id);
        if (isSuccess) {
            return Result.ok(null);
        } else {
            return Result.fail(null);
        }
    }

    // 批量删除用户
    @ApiOperation("批量删除用户")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> list) {
        boolean isSuccess = adminService.removeByIds(list);
        if (isSuccess) {
            return Result.ok(null);
        } else {
            return Result.fail(null);
        }
    }
}
