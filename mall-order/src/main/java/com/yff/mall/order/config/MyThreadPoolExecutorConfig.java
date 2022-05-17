package com.yff.mall.order.config;

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
//@EnableConfigurationProperties(ThreadPoolConfigProperties.class) // 已注入容器，可不写此配置
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
