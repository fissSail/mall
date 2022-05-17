package com.yff.mall.ware.config;

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
    public MessageConverter getMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    /*@RabbitListener(queues = "stock.delay.queue")
    public void rabbitListener( Channel channel, Message message) throws IOException {
        System.out.println("订单号");
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }*/

    @Bean
    public Queue stockDelayQueue() {
        Map<String, Object> map = new HashMap<>();
        map.put("x-dead-letter-exchange", RabbitMQConstant.STOCK_EVENT_EXCHANGE);
        map.put("x-dead-letter-routing-key", RabbitMQConstant.RELEASE_ROUTING_KEY); //死信消息根据哪个routingkey发送到队列
        map.put("x-message-ttl", 120000);
        return new Queue(RabbitMQConstant.STOCK_DELAY_QUEUE, true, false, false, map);
    }

    @Bean
    public Queue stockReleaseQueue() {
        return new Queue(RabbitMQConstant.STOCK_RELEASE_QUEUE, true, false, false);
    }

    /**
     * 使用主题模式交换机，可以根据不同routingkey绑定队列
     * @return
     */
    @Bean
    public Exchange stockEventExchange() {
        return new TopicExchange(RabbitMQConstant.STOCK_EVENT_EXCHANGE, true, false);
    }

    /**
     * 绑定延时队列
     * @return
     */
    @Bean
    public Binding stockDelayBinging() {
        return new Binding(RabbitMQConstant.STOCK_DELAY_QUEUE, Binding.DestinationType.QUEUE, RabbitMQConstant.STOCK_EVENT_EXCHANGE, RabbitMQConstant.DELAY_ROUTING_KEY, null);
    }

    @Bean
    public Binding stockReleaseBinging() {
        return new Binding(RabbitMQConstant.STOCK_RELEASE_QUEUE, Binding.DestinationType.QUEUE, RabbitMQConstant.STOCK_EVENT_EXCHANGE, RabbitMQConstant.RELEASE_ROUTING_KEY, null);
    }
}
