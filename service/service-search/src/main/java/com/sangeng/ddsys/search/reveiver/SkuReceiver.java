package com.sangeng.ddsys.search.reveiver;

import java.io.IOException;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;
import com.sangeng.ddsys.mq.constant.MqConst;
import com.sangeng.ddsys.search.service.SkuService;

/**
 * @author: calos
 * @version: 1.0.0
 * @createTime: 2023/11/5 20:13
 **/
@Component
public class SkuReceiver {
    @Autowired
    private SkuService skuService;

    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = MqConst.QUEUE_GOODS_UPPER, durable = "true"),
        exchange = @Exchange(value = MqConst.EXCHANGE_GOODS_DIRECT), key = {MqConst.ROUTING_GOODS_UPPER}))
    public void upperSku(Long skuId, Message message, Channel channel) throws IOException {
        if (skuId != null) {
            skuService.upperSku(skuId);
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 商品下架
     * 
     * @param skuId
     */
    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = MqConst.QUEUE_GOODS_LOWER, durable = "true"),
        exchange = @Exchange(value = MqConst.EXCHANGE_GOODS_DIRECT), key = {MqConst.ROUTING_GOODS_LOWER}))
    public void lowerSku(Long skuId, Message message, Channel channel) throws IOException {
        if (null != skuId) {
            skuService.lowerSku(skuId);
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
