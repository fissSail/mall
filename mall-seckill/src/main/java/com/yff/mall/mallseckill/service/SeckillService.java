package com.yff.mall.mallseckill.service;

import com.yff.common.to.feign.SeckillSkuRedisTo;

import java.util.List;

/**
 * @author yanfeifan
 * @Package com.yff.mall.mallseckill.service
 * @Description
 * @date 2022/2/10 16:50
 */

public interface SeckillService {

    void putawaySeckillSkuByLastThreeDays();

    List<SeckillSkuRedisTo> getCurrentSeckillSkus();

    SeckillSkuRedisTo getSkuSeckillInfo(Long skuId);

    String seckill(String seckillId, String token, Integer count);
}
