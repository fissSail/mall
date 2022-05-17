package com.yff.mall.mallseckill.feign;

import com.yff.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author yanfeifan
 * @Package com.yff.mall.mallseckill
 * @Description
 * @date 2022/2/10 16:52
 */
@FeignClient("mall-coupon")
public interface CouponFeignService {

    @GetMapping("/coupon/seckillsession/seckillSession/lastThreeDays")
    R getSeckillSessionByLastThreeDays();
}
