package com.sangeng.ddsys.cart.receiver;

import java.io.IOException;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;
import com.sangeng.ddsys.cart.service.CartInfoService;
import com.sangeng.ddsys.mq.constant.MqConst;

/**
 * @author: calos
 * @version: 1.0.0
 * @createTime: 2023/11/17 21:38
 **/
@Component
public class CartReceiver {
    @Autowired
    public CartInfoService cartInfoService;

    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = MqConst.QUEUE_DELETE_CART, durable = "true"),
        exchange = @Exchange(value = MqConst.EXCHANGE_ORDER_DIRECT), key = {MqConst.ROUTING_DELETE_CART}))
    public void deleteCart(Long userId, Message message, Channel channel) throws IOException {
        if (userId != null) {
            cartInfoService.deleteCartChecked(userId);
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
