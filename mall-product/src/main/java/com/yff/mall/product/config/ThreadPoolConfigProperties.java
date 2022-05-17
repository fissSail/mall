package com.yff.mall.product.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author yanfeifan
 * @Package com.yff.mall.product.config
 * @Description
 * @date 2022/1/29 10:45
 */
@ConfigurationProperties(prefix = "mall.thread.pool")
@Component //注入容器 使用方可不写@EnableConfigurationProperties
@Data
public class ThreadPoolConfigProperties {

    private Integer corePoolSize;
    private Integer maximumPoolSize;
    private Long keepAliveTime;
}
