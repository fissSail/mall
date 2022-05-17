package com.yff.mall.order.feign;

import com.yff.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author yanfeifan
 * @Package com.yff.mall.order.feign
 * @Description
 * @date 2022/2/4 15:27
 */
@FeignClient("mall-member")
public interface MemberFeignService {

    @GetMapping("/member/memberreceiveaddress/infoByMemberId/{memberId}")
    R infoByMemberId(@PathVariable("memberId") Long memberId);
}
