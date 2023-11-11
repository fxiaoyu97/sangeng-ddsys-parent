package com.sangeng.ddsys.mq.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: calos
 * @version: 1.0.0
 * @createTime: 2023/11/5 19:47
 **/
@Service
public class RabbitService {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送消息
     * 
     * @param exchange 交换机
     * @param routingKey 路由键
     * @param message 消息
     * @return 结果
     */
    public boolean sendMessage(String exchange, String routingKey, Object message) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
        return true;
    }
}
