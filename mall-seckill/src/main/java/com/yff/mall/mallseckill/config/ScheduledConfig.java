package com.yff.mall.mallseckill.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author yanfeifan
 * @Package com.yff.mall.mallseckill.config
 * @Description
 * @date 2022/2/10 16:45
 */
@Configuration
@EnableAsync //开启异步任务
@EnableScheduling //开启定时任务
public class ScheduledConfig {
}
