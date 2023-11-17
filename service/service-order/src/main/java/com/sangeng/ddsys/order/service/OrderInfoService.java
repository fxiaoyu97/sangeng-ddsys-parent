package com.sangeng.ddsys.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sangeng.ddsys.model.order.OrderInfo;
import com.sangeng.ddsys.vo.order.OrderConfirmVo;
import com.sangeng.ddsys.vo.order.OrderSubmitVo;

/**
 * <p>
 * 订单 服务类
 * </p>
 *
 * @author calos
 * @since 2023-11-17
 */
public interface OrderInfoService extends IService<OrderInfo> {
    /**
     * 确认订单
     */
    OrderConfirmVo confirmOrder();

    //生成订单
    Long submitOrder(OrderSubmitVo orderParamVo);

    //订单详情
    OrderInfo getOrderInfoById(Long orderId);
}
