package com.yff.mall.mallseckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.yff.common.constant.RabbitMQConstant;
import com.yff.common.to.feign.SeckillSessionTo;
import com.yff.common.to.feign.SeckillSkuRedisTo;
import com.yff.common.to.feign.SkuInfoFeignTo;
import com.yff.common.to.mq.QuickSeckillOrderTo;
import com.yff.common.utils.R;
import com.yff.common.vo.MemberRespVo;
import com.yff.mall.mallseckill.feign.CouponFeignService;
import com.yff.mall.mallseckill.feign.ProductFeignService;
import com.yff.mall.mallseckill.interceptor.LoginInterceptor;
import com.yff.mall.mallseckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author yanfeifan
 * @Package com.yff.mall.mallseckill.service.impl
 * @Description
 * @date 2022/2/10 16:50
 */
@Service
@Slf4j
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    private CouponFeignService couponFeignService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private final String SESSION_REDIS_PREFIX = "seckill:sessions:";
    private final String SECKILL_SKU_REDIS_PREFIX = "seckill:skus";
    private final String SKU_STOCK_SEMAPHORE_PREFIX = "seckill:stock:";

    @Override
    public void putawaySeckillSkuByLastThreeDays() {
        //扫描最近三天需要参与秒杀的活动
        R r = couponFeignService.getSeckillSessionByLastThreeDays();
        if (r.getCode() == 0) {
            //获取到最近3天上架秒杀的商品
            List<SeckillSessionTo> data = r.getData(new TypeReference<List<SeckillSessionTo>>() {
            });
            //缓存到redis
            //1，缓存活动信息
            this.saveSessionInfos(data);
            //2，缓存活动相关商品信息
            this.saveSessionSkuInfos(data);
        }
    }

    /**
     * 返回当前时间可以参与秒杀商品信息
     *
     * @return
     */
    @Override
    public List<SeckillSkuRedisTo> getCurrentSeckillSkus() {
        //确定当前时间属于哪个秒杀场次
        long time = new Date().getTime();
        Set<String> keys = stringRedisTemplate.keys(SESSION_REDIS_PREFIX + "*");
        for (String key : keys) {
            String replace = key.replace(SESSION_REDIS_PREFIX, "");
            String[] split = replace.split("_");
            long startTime = Long.parseLong(split[0]);
            long endTime = Long.parseLong(split[1]);
            if (time >= startTime && time <= endTime) {
                //获取这个秒杀场次需要的商品信息
                List<String> range = stringRedisTemplate.opsForList().range(key, -100, 100);
                BoundHashOperations<String, String, String> ops = stringRedisTemplate.boundHashOps(SECKILL_SKU_REDIS_PREFIX);

                List<String> list = ops.multiGet(range);
                if (!CollectionUtils.isEmpty(list)) {
                    List<SeckillSkuRedisTo> collect = list.stream().map(data -> {
                        SeckillSkuRedisTo seckillSkuRedisTo = JSON.parseObject(data, SeckillSkuRedisTo.class);
                        return seckillSkuRedisTo;
                    }).collect(Collectors.toList());
                    return collect;
                }
                break;
            }
        }
        return null;
    }

    @Override
    public SeckillSkuRedisTo getSkuSeckillInfo(Long skuId) {
        BoundHashOperations<String, String, String> ops = stringRedisTemplate.boundHashOps(SECKILL_SKU_REDIS_PREFIX);
        Set<String> keys = ops.keys();
        if (!CollectionUtils.isEmpty(keys)) {
            String regx = "\\d_" + skuId;
            for (String key : keys) {
                if (Pattern.matches(regx, key)) {
                    String json = ops.get(key);
                    SeckillSkuRedisTo seckillSkuRedisTo = JSON.parseObject(json, SeckillSkuRedisTo.class);
                    long time = new Date().getTime();
                    if (!(time >= seckillSkuRedisTo.getStartTime() && time <= seckillSkuRedisTo.getEndTime())) {
                        seckillSkuRedisTo.setRandomCode(null);
                    }
                    return seckillSkuRedisTo;
                }
            }
        }
        return null;
    }

    @Override
    public String seckill(String seckillId, String token, Integer count) {
        long l = System.currentTimeMillis();
        MemberRespVo memberRespVo = LoginInterceptor.threadLocal.get();
        //登录校验
        //合法性校验
        BoundHashOperations<String, String, String> ops = stringRedisTemplate.boundHashOps(SECKILL_SKU_REDIS_PREFIX);
        String json = ops.get(seckillId);
        if (StringUtils.hasText(json)) {
            //获取当前秒杀商品的详细信息
            SeckillSkuRedisTo seckillSkuRedisTo = JSON.parseObject(json, SeckillSkuRedisTo.class);
            //合法性校验
            Long startTime = seckillSkuRedisTo.getStartTime();
            Long endTime = seckillSkuRedisTo.getEndTime();
            long time = new Date().getTime();
            //校验秒杀时间合法
            if (time >= startTime && time <= endTime) {
                //校验随机码和商品id
                String randomCode = seckillSkuRedisTo.getRandomCode();
                String key = seckillSkuRedisTo.getSeckillSkuRelationTo().getPromotionSessionId() + "_" + seckillSkuRedisTo.getSkuInfoFeignTo().getSkuId();
                if (randomCode.equals(token) && key.equals(seckillId)) {
                    //校验购买数量
                    if (count <= seckillSkuRedisTo.getSeckillSkuRelationTo().getSeckillLimit()) {
                        //校验这个人是否已经购买过了
                        Long userId = memberRespVo.getId();
                        String redisKey = userId + "_" + seckillId;
                        long ttl = endTime - time;
                        Boolean aBoolean = stringRedisTemplate.opsForValue().setIfAbsent(redisKey, count.toString(), ttl, TimeUnit.MILLISECONDS);
                        if (aBoolean) {
                            //占位成功，说明从未买过
                            RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE_PREFIX + randomCode);

                            boolean b = semaphore.tryAcquire(count);
                            if (b) {
                                //秒杀成功
                                //快速下单，往MQ发送消息
                                String orderSn = IdWorker.getTimeId();
                                QuickSeckillOrderTo quickSeckillOrderTo = new QuickSeckillOrderTo();
                                quickSeckillOrderTo.setOrderSn(orderSn);
                                quickSeckillOrderTo.setSeckillPrice(seckillSkuRedisTo.getSeckillSkuRelationTo().getSeckillPrice());
                                quickSeckillOrderTo.setCount(count);
                                quickSeckillOrderTo.setMemberId(userId);
                                quickSeckillOrderTo.setSkuId(seckillSkuRedisTo.getSkuInfoFeignTo().getSkuId());
                                quickSeckillOrderTo.setPromotionSessionId(seckillSkuRedisTo.getSeckillSkuRelationTo().getPromotionSessionId());
                                rabbitTemplate.convertAndSend(RabbitMQConstant.ORDER_SECKILL_EVENT_EXCHANGE, RabbitMQConstant.ORDER_SECKILL_ROUTING_KEY, quickSeckillOrderTo);
                                long l1 = System.currentTimeMillis();
                                log.info("耗时：{}",l1-l);
                                return orderSn;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private void saveSessionInfos(List<SeckillSessionTo> sessionToList) {
        sessionToList.stream().forEach(item -> {
            long startTimeLong = item.getStartTime().getTime();
            long endTimeLong = item.getEndTime().getTime();
            String key = SESSION_REDIS_PREFIX + startTimeLong + "_" + endTimeLong;
            List<String> skuIds = item.getSeckillSkuRelationTos().stream().map(data -> data.getPromotionSessionId() + "_" + data.getSkuId()).collect(Collectors.toList());
            //幂等性处理
            if (!stringRedisTemplate.hasKey(key)) {
                stringRedisTemplate.opsForList().leftPushAll(key, skuIds);
            }
        });
    }

    private void saveSessionSkuInfos(List<SeckillSessionTo> sessionToList) {
        sessionToList.stream().forEach(item -> {
            BoundHashOperations<String, Object, Object> ops = stringRedisTemplate.boundHashOps(SECKILL_SKU_REDIS_PREFIX);
            item.getSeckillSkuRelationTos().stream().forEach(data -> {
                String key = data.getPromotionSessionId() + "_" + data.getSkuId();
                if (!ops.hasKey(key)) {
                    //缓存商品
                    SeckillSkuRedisTo seckillSkuRedisTo = new SeckillSkuRedisTo();
                    //sku秒杀信息
                    seckillSkuRedisTo.setSeckillSkuRelationTo(data);
                    //sku基本信息
                    R r = productFeignService.skuinfoInfo(data.getSkuId());
                    if (r.getCode() == 0) {
                        SkuInfoFeignTo skuInfo = r.getDataByKey("skuInfo", new TypeReference<SkuInfoFeignTo>() {
                        });
                        seckillSkuRedisTo.setSkuInfoFeignTo(skuInfo);
                    }
                    //设置活动的时间
                    seckillSkuRedisTo.setStartTime(item.getStartTime().getTime());
                    seckillSkuRedisTo.setEndTime(item.getEndTime().getTime());
                    //商品的随机码
                    String randomCode = UUID.randomUUID().toString().replace("-", "");
                    seckillSkuRedisTo.setRandomCode(randomCode);

                    String json = JSON.toJSONString(seckillSkuRedisTo);

                    ops.put(key, json);

                    //使用redisson设置信号量 限流
                    RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE_PREFIX + randomCode);
                    //设置商品可以秒杀的数量作为信号量
                    semaphore.trySetPermits(data.getSeckillCount());
                }
            });
        });
    }
}
