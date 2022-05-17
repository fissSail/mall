package com.yff.mall.product.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author yanfeifan
 * @Package com.yff.mall.product.config
 * @Description
 * @date 2022/1/29 10:41
 */
@Configuration
public class MyThreadPoolExecutorConfig {

    @Bean
    public ThreadPoolExecutor threadPoolExecutor(ThreadPoolConfigProperties threadPoolConfigProperties){
        return new ThreadPoolExecutor(threadPoolConfigProperties.getCorePoolSize(),
                threadPoolConfigProperties.getMaximumPoolSize(),
                threadPoolConfigProperties.getKeepAliveTime(), TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(),
                Executors.defaultThreadFactory(),new ThreadPoolExecutor.AbortPolicy());
    }

}
