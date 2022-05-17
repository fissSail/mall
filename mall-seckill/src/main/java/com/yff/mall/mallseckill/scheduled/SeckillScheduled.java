package com.yff.mall.mallseckill.scheduled;

import com.yff.mall.mallseckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author yanfeifan
 * @Package com.yff.mall.mallseckill.scheduled
 * @Description 秒杀商品定时上架 ，每天晚上3点
 * @date 2022/2/10 16:44
 */
@Service
@Slf4j
public class SeckillScheduled {

    @Autowired
    private SeckillService seckillService;

    @Autowired
    private RedissonClient redissonClient;

    private final String SECKILL_UPLOAD_LOCK = "seckill:upload:lock";

    @Scheduled(cron = "0 0 3 * * ?")
    public void putawaySeckillSkuByLastThreeDays(){
        log.info("上架秒杀的商品信息");
        //使用分布式锁，防止分布式系统下有多个定时任务在执行
        RLock lock = redissonClient.getLock(SECKILL_UPLOAD_LOCK);

        lock.lock(10, TimeUnit.SECONDS);
        //上架
        try {
            seckillService.putawaySeckillSkuByLastThreeDays();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
