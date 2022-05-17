package com.yff.common.constant;

/**
 * @author yanfeifan
 * @Package com.yff.common.constant
 * @Description
 * @date 2022/2/6 14:18
 */

public class RabbitMQConstant {
    //ware
    public static final String STOCK_DELAY_QUEUE = "stock.delay.queue";
    public static final String STOCK_RELEASE_QUEUE = "stock.release.queue";
    public static final String STOCK_EVENT_EXCHANGE = "stock-event-exchange";
    public static final String DELAY_ROUTING_KEY = "stock.create";
    public static final String RELEASE_ROUTING_KEY = "stock.release";

    //order
    public static final String ORDER_DELAY_QUEUE = "order.delay.queue";
    public static final String ORDER_CLOSE_QUEUE = "order.close.queue";
    public static final String ORDER_EVENT_EXCHANGE = "order-event-exchange";
    public static final String ORDER_DELAY_ROUTING_KEY = "order.create";
    public static final String ORDER_CLOSE_ROUTING_KEY = "order.close";
    public static final String ORDER_CLOSE_RELEASE_OTHER_ROUTING_KEY = "order.release.other.#";


    public static final String ORDER_SECKILL_EVENT_EXCHANGE = "order-seckill-event-exchange";
    public static final String ORDER_SECKILL_ROUTING_KEY = "order.seckill.order";
    public static final String ORDER_SECKILL_QUEUE = "order.seckill.queue";
}
