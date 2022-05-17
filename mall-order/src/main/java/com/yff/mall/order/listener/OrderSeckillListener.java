package com.yff.mall.order.listener;

import com.rabbitmq.client.Channel;
import com.yff.common.constant.RabbitMQConstant;
import com.yff.common.to.mq.QuickSeckillOrderTo;
import com.yff.mall.order.service.OrderService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author yanfeifan
 * @Package com.yff.mall.order.listener
 * @Description
 * @date 2022/2/12 15:55
 */
@Component
@RabbitListener(queues = RabbitMQConstant.ORDER_SECKILL_QUEUE)
public class OrderSeckillListener {

    @Autowired
    private OrderService orderService;

    @RabbitHandler
    public void handleOrderSeckill(QuickSeckillOrderTo to, Channel channel, Message message) throws IOException {
        System.out.println("订单秒杀");
        try {
            orderService.orderSeckill(to);
            //手动确认
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            e.printStackTrace();
            //拒绝接收消息，并重新入队
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }
}
