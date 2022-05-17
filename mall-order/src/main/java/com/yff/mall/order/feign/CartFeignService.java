package com.yff.mall.order.feign;

import com.yff.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author yanfeifan
 * @Package com.yff.mall.order.feign
 * @Description
 * @date 2022/2/4 15:54
 */
@FeignClient("mall-cart")
public interface CartFeignService {

    @GetMapping("/getCartByMemberId")
    R getCartByMemberId();
}
