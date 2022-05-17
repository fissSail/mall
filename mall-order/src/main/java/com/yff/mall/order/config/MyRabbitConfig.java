package com.yff.mall.order.config;

import com.yff.common.constant.RabbitMQConstant;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yanfeifan
 * @Package com.yff.mall.order.config
 * @Description
 * @date 2022/1/30 17:32
 */
@Configuration
public class MyRabbitConfig {


    @Bean
    public MessageConverter getMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue orderDelayQueue() {
        Map<String, Object> map = new HashMap<>();
        map.put("x-dead-letter-exchange", RabbitMQConstant.ORDER_EVENT_EXCHANGE);
        map.put("x-dead-letter-routing-key", RabbitMQConstant.ORDER_CLOSE_ROUTING_KEY);
        map.put("x-message-ttl", 60000);
        return new Queue(RabbitMQConstant.ORDER_DELAY_QUEUE, true, false, false, map);
    }

    @Bean
    public Queue orderReleaseQueue() {
        return new Queue(RabbitMQConstant.ORDER_CLOSE_QUEUE, true, false, false);
    }

    @Bean
    public Exchange orderEventExchange() {
        return new TopicExchange(RabbitMQConstant.ORDER_EVENT_EXCHANGE, true, false);
    }

    @Bean
    public Binding orderDelayBinging() {
        return new Binding(RabbitMQConstant.ORDER_DELAY_QUEUE, Binding.DestinationType.QUEUE, RabbitMQConstant.ORDER_EVENT_EXCHANGE, RabbitMQConstant.ORDER_DELAY_ROUTING_KEY, null);
    }

    @Bean
    public Binding orderReleaseBinging() {
        return new Binding(RabbitMQConstant.ORDER_CLOSE_QUEUE, Binding.DestinationType.QUEUE, RabbitMQConstant.ORDER_EVENT_EXCHANGE, RabbitMQConstant.ORDER_CLOSE_ROUTING_KEY, null);
    }

    /**
     * 订单释放直接和库存释放进行绑定
     * @return
     */
    @Bean
    public Binding orderCloseOtherBinging() {
        return new Binding(RabbitMQConstant.STOCK_RELEASE_QUEUE, Binding.DestinationType.QUEUE, RabbitMQConstant.ORDER_EVENT_EXCHANGE, RabbitMQConstant.ORDER_CLOSE_RELEASE_OTHER_ROUTING_KEY, null);
    }

    @Bean
    public Queue orderSeckillQueue() {
        return new Queue(RabbitMQConstant.ORDER_SECKILL_QUEUE, true, false, false);
    }

    @Bean
    public Exchange orderSeckillExchange() {
        return new TopicExchange(RabbitMQConstant.ORDER_SECKILL_EVENT_EXCHANGE, true, false);
    }

    @Bean
    public Binding orderSeckillBinging() {
        return new Binding(RabbitMQConstant.ORDER_SECKILL_QUEUE, Binding.DestinationType.QUEUE, RabbitMQConstant.ORDER_SECKILL_EVENT_EXCHANGE, RabbitMQConstant.ORDER_SECKILL_ROUTING_KEY, null);
    }

}
