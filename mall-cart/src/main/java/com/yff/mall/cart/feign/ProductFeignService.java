package com.yff.mall.cart.feign;

import com.yff.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author yanfeifan
 * @Package com.yff.mall.cart.feign
 * @Description
 * @date 2022/2/3 15:34
 */
@FeignClient("mall-product")
public interface ProductFeignService {

    @GetMapping("/product/skuinfo/info/{skuId}")
    R getSkuinfoBySkuId(@PathVariable("skuId") Long skuId);

    @GetMapping("/product/skusaleattrvalue/getSkuSaleAttr/{skuId}")
    R getSkuSaleAttrValues(@PathVariable("skuId") Long skuId);
}
