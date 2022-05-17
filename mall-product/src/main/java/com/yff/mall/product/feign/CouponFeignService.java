package com.yff.mall.product.feign;


import com.yff.common.to.SkuReductionTo;
import com.yff.common.to.SpuBoundsTo;
import com.yff.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author yanfeifan
 * @Package com.yff.mall.product.fegin
 * @Description
 * @date 2021/12/25 20:05
 */
@FeignClient("mall-coupon")
public interface CouponFeignService {

    /**
     * 保存
     * @param to
     * @return
     */
    @PostMapping("/coupon/spubounds/save/bounds")
    R saveBounds(@RequestBody SpuBoundsTo to);

    /**
     * 保存
     * @param to
     * @return
     */
    @PostMapping("/coupon/skufullreduction/saveSkuReduction")
    R saveSkuReduction(@RequestBody SkuReductionTo to);
}
