package com.yff.mall.ware.feign;

import com.yff.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author yanfeifan
 * @Package com.yff.mall.ware.feign
 * @Description
 * @date 2021/12/27 9:39
 */
@FeignClient("mall-product")
public interface ProductFeignService {

    /**
     * 信息
     */
    @GetMapping("/product/skuinfo/info/{skuId}")
    R info(@PathVariable("skuId") Long skuId);
}
