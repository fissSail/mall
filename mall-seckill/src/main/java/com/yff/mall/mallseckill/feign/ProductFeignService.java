package com.yff.mall.mallseckill.feign;

import com.yff.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author yanfeifan
 * @Package com.yff.mall.mallseckill.feign
 * @Description
 * @date 2022/2/11 9:27
 */
@FeignClient("mall-product")
public interface ProductFeignService {

    @GetMapping("/product/skuinfo/info/{skuId}")
    R skuinfoInfo(@PathVariable("skuId") Long skuId);
}
