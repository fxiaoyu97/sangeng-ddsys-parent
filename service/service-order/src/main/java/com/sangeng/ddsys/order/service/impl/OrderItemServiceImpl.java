package com.sangeng.ddsys.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sangeng.ddsys.model.order.OrderItem;
import com.sangeng.ddsys.order.mapper.OrderItemMapper;
import com.sangeng.ddsys.order.service.OrderItemService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单项信息 服务实现类
 * </p>
 *
 * @author calos
 * @since 2023-11-17
 */
@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements OrderItemService {
        
}
