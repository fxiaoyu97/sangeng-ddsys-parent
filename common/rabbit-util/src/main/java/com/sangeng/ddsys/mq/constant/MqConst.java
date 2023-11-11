package com.sangeng.ddsys.mq.constant;

/**
 * @author: calos
 * @version: 1.0.0
 * @createTime: 2023/11/5 20:03
 **/
public class MqConst {
    /**
     * 消息补偿
     */
    public static final String MQ_KEY_PREFIX = "ddsys.mq:list";
    public static final int RETRY_COUNT = 3;

    /**
     * 商品上下架
     */
    public static final String EXCHANGE_GOODS_DIRECT = "ddsys.goods.direct";
    public static final String ROUTING_GOODS_UPPER = "ddsys.goods.upper";
    public static final String ROUTING_GOODS_LOWER = "ddsys.goods.lower";
    // 队列
    public static final String QUEUE_GOODS_UPPER = "ddsys.goods.upper";
    public static final String QUEUE_GOODS_LOWER = "ddsys.goods.lower";

    /**
     * 团长上下线
     */
    public static final String EXCHANGE_LEADER_DIRECT = "ddsys.leader.direct";
    public static final String ROUTING_LEADER_UPPER = "ddsys.leader.upper";
    public static final String ROUTING_LEADER_LOWER = "ddsys.leader.lower";
    // 队列
    public static final String QUEUE_LEADER_UPPER = "ddsys.leader.upper";
    public static final String QUEUE_LEADER_LOWER = "ddsys.leader.lower";

    // 订单
    public static final String EXCHANGE_ORDER_DIRECT = "ddsys.order.direct";
    public static final String ROUTING_ROLLBACK_STOCK = "ddsys.rollback.stock";
    public static final String ROUTING_MINUS_STOCK = "ddsys.minus.stock";

    public static final String ROUTING_DELETE_CART = "ddsys.delete.cart";
    // 解锁普通商品库存
    public static final String QUEUE_ROLLBACK_STOCK = "ddsys.rollback.stock";
    public static final String QUEUE_SECKILL_ROLLBACK_STOCK = "ddsys.seckill.rollback.stock";
    public static final String QUEUE_MINUS_STOCK = "ddsys.minus.stock";
    public static final String QUEUE_DELETE_CART = "ddsys.delete.cart";

    // 支付
    public static final String EXCHANGE_PAY_DIRECT = "ddsys.pay.direct";
    public static final String ROUTING_PAY_SUCCESS = "ddsys.pay.success";
    public static final String QUEUE_ORDER_PAY = "ddsys.order.pay";
    public static final String QUEUE_LEADER_BILL = "ddsys.leader.bill";

    // 取消订单
    public static final String EXCHANGE_CANCEL_ORDER_DIRECT = "ddsys.cancel.order.direct";
    public static final String ROUTING_CANCEL_ORDER = "ddsys.cancel.order";
    // 延迟取消订单队列
    public static final String QUEUE_CANCEL_ORDER = "ddsys.cancel.order";

    /**
     * 定时任务
     */
    public static final String EXCHANGE_DIRECT_TASK = "ddsys.exchange.direct.task";
    public static final String ROUTING_TASK_23 = "ddsys.task.23";
    // 队列
    public static final String QUEUE_TASK_23 = "ddsys.queue.task.23";
}
