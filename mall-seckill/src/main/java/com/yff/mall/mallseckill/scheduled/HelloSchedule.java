package com.yff.mall.mallseckill.scheduled;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author yanfeifan
 * @Package com.yff.mall.mallseckill.scheduled
 * @Description
 * @date 2022/2/10 16:23
 */
@Slf4j
@Component
public class HelloSchedule {

    /**
     * spring中cron只能有6位，不支持年
     * 在几周的位置，1-7代表周一到周日
     * 定时任务是阻塞的，前一个定时任务没执行完，后面的就得不到执行
     *  1,可以使用异步执行，就不会阻塞
     *  2,设置spring.task.scheduling.pool.size线程数大点，有时不生效
     *  3,让定时任务异步执行
     */
    /*@Async
    @Scheduled(cron = "* * * * * ?")
    public void main() {
        log.info("main");
        //CompletableFuture.runAsync(()->{
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        //});
    }*/
}
