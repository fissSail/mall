package com.yff.mall.ware.listener;

import com.rabbitmq.client.Channel;
import com.yff.common.constant.RabbitMQConstant;
import com.yff.common.to.feign.OrderTo;
import com.yff.common.to.mq.StockLockedTo;
import com.yff.mall.ware.service.WareSkuService;
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
@RabbitListener(queues = RabbitMQConstant.STOCK_RELEASE_QUEUE)
@Component
public class StockReleaseListener {

    @Autowired
    private WareSkuService wareSkuService;

    @RabbitHandler
    public void handleStockLockedRelease(StockLockedTo to, Channel channel, Message message) throws IOException {
        System.out.println("解锁库存");
        try {
            wareSkuService.stockLockedRelease(to);
            //手动确认
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            e.printStackTrace();
            //拒绝接收消息，并重新入队
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }

    @RabbitHandler
    public void handleOrderCloseRelease(OrderTo to, Channel channel, Message message) throws IOException {
        System.out.println("订单关单，解锁库存");
        try {
            wareSkuService.stockLockedRelease(to);
            //手动确认
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            e.printStackTrace();
            //拒绝接收消息，并重新入队
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }
}
