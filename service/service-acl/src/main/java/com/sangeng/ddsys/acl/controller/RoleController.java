package com.sangeng.ddsys.acl.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sangeng.ddsys.acl.service.RoleService;
import com.sangeng.ddsys.common.result.Result;
import com.sangeng.ddsys.model.acl.Role;
import com.sangeng.ddsys.vo.acl.RoleQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
     * @param current     当前页
     * @param limit       每页数量
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
        IPage<Role> pageModel = roleService.selectRolePage(pageParam, roleQueryVo);
        return Result.ok(pageModel);
    }

    /**
     * 根据id查询角色
     *
     * @param id 角色id
     * @return 查询结果
     */
    @ApiOperation("根据id查询角色")
    @GetMapping("/role/{id}")
    public Result get(@PathVariable Long id) {
        Role role = roleService.getById(id);
        return Result.ok(role);
    }

    @ApiOperation("添加角色")
    @PostMapping("save")
    public Result save(@RequestBody Role role) {
        boolean isSuccess = roleService.save(role);
        if (isSuccess) {
            return Result.ok(null);
        } else {
            return Result.fail(null);
        }
    }

    @ApiOperation("修改角色")
    @PutMapping("update")
    public Result update(@RequestBody Role role) {
        boolean isSuccess = roleService.updateById(role);
        if (isSuccess) {
            return Result.ok(null);
        } else {
            return Result.fail(null);
        }
    }

    @ApiOperation("删除角色")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        boolean isSuccess = roleService.removeById(id);
        if (isSuccess) {
            return Result.ok(null);
        } else {
            return Result.fail(null);
        }
    }

    /**
     * 批量删除角色
     * json数组[1,2,3]对应 java的list集合
     *
     * @param idList
     * @return
     */
    @ApiOperation("批量删除角色")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> idList) {
        boolean isSuccess = roleService.removeByIds(idList);
        if (isSuccess) {
            return Result.ok(null);
        } else {
            return Result.fail(null);
        }
    }
}
