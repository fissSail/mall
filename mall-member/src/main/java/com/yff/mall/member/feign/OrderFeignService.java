package com.yff.mall.member.feign;

import com.yff.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * @author yanfeifan
 * @Package com.yff.mall.member.feign
 * @Description
 * @date 2022/2/7 16:49
 */
@FeignClient("mall-order")
public interface OrderFeignService {


    @PostMapping("/order/order/listWithItem")
    R listWithItem(@RequestBody Map<String, Object> params);
}
