package com.yff.mall.ware.feign;

import com.yff.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author yanfeifan
 * @Package com.yff.mall.ware.feign
 * @Description
 * @date 2022/2/4 19:11
 */
@FeignClient("mall-member")
public interface MemberFeignService {

    @GetMapping("/member/memberreceiveaddress/info/{id}")
    R info(@PathVariable("id") Long id);
}
