package com.yff.mall.order.listener;

import com.rabbitmq.client.Channel;
import com.yff.common.constant.RabbitMQConstant;
import com.yff.mall.order.entity.OrderEntity;
import com.yff.mall.order.service.OrderService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author yanfeifan
 * @Package com.yff.mall.ware.listener
 * @Description
 * @date 2022/2/6 18:47
 */
@RabbitListener(queues = RabbitMQConstant.ORDER_CLOSE_QUEUE)
@Component
public class OrderCloseListener {
    @Autowired
    private OrderService orderService;

    @RabbitHandler
    public void handleOrderClose(OrderEntity orderEntity, Channel channel, Message message) throws IOException {
        System.out.println("订单关单");
        try {
            orderService.closeOrder(orderEntity);
            //手动确认
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            e.printStackTrace();
            //拒绝接收消息，并重新入队
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }
}
