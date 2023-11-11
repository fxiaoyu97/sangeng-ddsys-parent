package com.sangeng.ddsys.sys.controller;

import com.sangeng.ddsys.common.result.Result;
import com.sangeng.ddsys.model.sys.Ware;
import com.sangeng.ddsys.sys.service.WareService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 仓库表 前端控制器
 * </p>
 *
 * @author calos
 * @since 2023-11-03
 */
@Api(tags = "仓库管理接口")
@RestController
@RequestMapping("/admin/sys/ware")
public class WareController {
    @Autowired
    private WareService wareService;

    @ApiOperation("查询所有仓库")
    @GetMapping("findAllList")
    public Result findAllList() {
        List<Ware> list = wareService.list();
        return Result.ok(list);
    }
}
