package com.sangeng.ddsys.order.controller;

import com.sangeng.ddsys.common.result.Result;
import com.sangeng.ddsys.order.service.OrderInfoService;
import com.sangeng.ddsys.vo.order.OrderSubmitVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 订单 前端控制器
 * </p>
 *
 * @author calos
 * @since 2023-11-17
 */
@Api(value = "Order管理", tags = "Order管理")
@RestController
@RequestMapping(value = "/api/order")
public class OrderInfoController {

    @Autowired
    private OrderInfoService orderService;

    @ApiOperation("确认订单")
    @GetMapping("auth/confirmOrder")
    public Result confirm() {
        return Result.ok(orderService.confirmOrder());
    }

    @ApiOperation("生成订单")
    @PostMapping("auth/submitOrder")
    public Result submitOrder(@RequestBody OrderSubmitVo orderParamVo) {
        return Result.ok(orderService.submitOrder(orderParamVo));
    }

    @ApiOperation("获取订单详情")
    @GetMapping("auth/getOrderInfoById/{orderId}")
    public Result getOrderInfoById(@PathVariable("orderId") Long orderId) {
        return Result.ok(orderService.getOrderInfoById(orderId));
    }
}
