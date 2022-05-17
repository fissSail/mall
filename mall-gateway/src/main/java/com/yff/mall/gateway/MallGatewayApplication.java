package com.yff.mall.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.sleuth.autoconfig.instrument.redis.TraceRedisAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class,TraceRedisAutoConfiguration.class})
@EnableDiscoveryClient
public class MallGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallGatewayApplication.class, args);
    }

}
