package com.yff.common.to.feign;

import lombok.Data;

/**
 * @author yanfeifan
 * @Package com.yff.common.to.feign
 * @Description
 * @date 2022/2/10 17:49
 */
@Data
public class SeckillSkuRedisTo {
    private SeckillSkuRelationTo seckillSkuRelationTo;
    private SkuInfoFeignTo skuInfoFeignTo;

    private Long startTime;
    private Long endTime;
    //随机码
    private String randomCode;

}
