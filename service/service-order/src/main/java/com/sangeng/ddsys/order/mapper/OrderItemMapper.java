package com.sangeng.ddsys.order.mapper;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sangeng.ddsys.model.order.OrderItem;

/**
 * <p>
 * 订单项信息 Mapper 接口
 * </p>
 *
 * @author calos
 * @since 2023-11-17
 */
@Repository
public interface OrderItemMapper extends BaseMapper<OrderItem> {

}
