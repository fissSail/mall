package com.yff.mall.ware.feign;

import com.yff.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author yanfeifan
 * @Package com.yff.mall.ware.feign
 * @Description
 * @date 2022/2/6 15:25
 */

@FeignClient("mall-order")
public interface OrderFeignService {

    @GetMapping("/order/order/orderStatus/{orderSn}")
    R getOrderStatus(@PathVariable("orderSn") String orderSn);
}
