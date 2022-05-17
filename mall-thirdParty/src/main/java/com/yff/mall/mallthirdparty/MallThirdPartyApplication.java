package com.yff.mall.mallthirdparty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.sleuth.autoconfig.instrument.redis.TraceRedisAutoConfiguration;

@SpringBootApplication(exclude = TraceRedisAutoConfiguration.class)
@EnableDiscoveryClient
public class MallThirdPartyApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallThirdPartyApplication.class, args);
    }

}
