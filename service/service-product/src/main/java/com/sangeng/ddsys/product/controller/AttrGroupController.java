package com.sangeng.ddsys.product.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sangeng.ddsys.common.result.Result;
import com.sangeng.ddsys.model.product.AttrGroup;
import com.sangeng.ddsys.product.service.AttrGroupService;
import com.sangeng.ddsys.vo.product.AttrGroupQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 属性分组 前端控制器
 * </p>
 *
 * @author calos
 * @since 2023-11-03
 */
@Api(value = "AttrGroup管理", tags = "平台属性分组管理")
@RestController
@RequestMapping("/admin/product/attrGroup")
public class AttrGroupController {

    @Autowired
    private AttrGroupService attrGroupService;

    @ApiOperation(value = "获取分页列表")
    @GetMapping("{page}/{limit}")
    public Result index(@ApiParam(name = "page", value = "当前页码", required = true) @PathVariable Long page,
        @ApiParam(name = "limit", value = "每页记录数", required = true) @PathVariable Long limit,
        @ApiParam(name = "attrGroupQueryVo", value = "查询对象") AttrGroupQueryVo attrGroupQueryVo) {
        Page<AttrGroup> pageParam = new Page<>(page, limit);
        IPage<AttrGroup> pageModel = attrGroupService.selectPage(pageParam, attrGroupQueryVo);
        return Result.ok(pageModel);
    }

    @ApiOperation(value = "获取")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        AttrGroup attrGroup = attrGroupService.getById(id);
        return Result.ok(attrGroup);
    }

    @ApiOperation(value = "新增")
    @PostMapping("save")
    public Result save(@RequestBody AttrGroup attrGroup) {
        attrGroupService.save(attrGroup);
        return Result.ok();
    }

    @ApiOperation(value = "修改")
    @PutMapping("update")
    public Result updateById(@RequestBody AttrGroup attrGroup) {
        attrGroupService.updateById(attrGroup);
        return Result.ok();
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        attrGroupService.removeById(id);
        return Result.ok();
    }

    @ApiOperation(value = "根据id列表删除")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> idList) {
        attrGroupService.removeByIds(idList);
        return Result.ok();
    }

    @ApiOperation(value = "获取全部属性分组")
    @GetMapping("findAllList")
    public Result findAllList() {
        return Result.ok(attrGroupService.findAllList());
    }
}
