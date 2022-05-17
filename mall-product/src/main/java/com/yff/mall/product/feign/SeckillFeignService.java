package com.yff.mall.product.feign;

import com.yff.common.utils.R;
import com.yff.mall.product.feign.fallback.SeckillFeignFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author yanfeifan
 * @Package com.yff.mall.product.feign
 * @Description
 * @date 2022/2/11 19:48
 */
@FeignClient(value = "mall-seckill", fallback = SeckillFeignFallback.class)
public interface SeckillFeignService {


    @GetMapping("/sku/seckill/{skuId}")
    R getSkuSeckillInfo(@PathVariable("skuId")Long skuId);
}
