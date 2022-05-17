package com.yff.mall.mallseckill.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

}
