package com.yff.mall.mallseckill.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yanfeifan
 * @Package com.yff.mall.product.config
 * @Description
 * @date 2022/1/12 17:02
 */
@Configuration
public class RedissonConfig {

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisson(){
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.213.101:6379");

        return Redisson.create(config);
    }
}
