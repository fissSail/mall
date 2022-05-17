package com.yff.mall.order.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author yanfeifan
 * @Package com.yff.rabbitmq.springbootrabbitmq.config
 * @Description
 * @date 2021/9/22 17:22
 */
@Slf4j
@Component
public class ConfirmCallback implements RabbitTemplate.ConfirmCallback,RabbitTemplate.ReturnsCallback {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    //注入进rabbitTemplate
    @PostConstruct
    public void init(){
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnsCallback(this::returnedMessage);
    }

    /**
     * 交换机确认回调方法
     * 1，发消息交换机接收到回调
     * 2，发消息交换机接收失败到回调
     *
     * @param correlationData 保存回调消息的信息
     * @param b 交换机收到消息成功 b=true 失败 false
     * @param s 失败的原因/成功null
     */

    @Override
    public void confirm(CorrelationData correlationData, boolean b, String s) {
        String id = correlationData != null ? correlationData.getId() : "";
        if(b){
            log.info("消息确认"+id);
        }else{
            log.info("消息失败"+id+"，原因"+s);
        }
    }

    /**
     * 当消息传递过程中不可到达目的地时将消息返回给生成者
     * 只有不可到达目的地时，可进行回退
     * @param returnedMessage
     */
    @Override
    public void returnedMessage(ReturnedMessage returnedMessage) {

        log.error("消息{}，被交换机{}退回，原因{}，routingKey{}",
                returnedMessage.getMessage(),returnedMessage.getExchange(),
                returnedMessage.getReplyText(),returnedMessage.getRoutingKey());
    }
}
