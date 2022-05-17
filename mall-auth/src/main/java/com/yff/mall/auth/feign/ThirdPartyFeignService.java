package com.yff.mall.auth.feign;

import com.yff.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author yanfeifan
 * @Package com.yff.mall.auth.feign
 * @Description
 * @date 2022/1/29 16:46
 */
@FeignClient("mall-thirdParty")
public interface ThirdPartyFeignService {

    /**
     * 发送短信
     * @param phone
     * @param code
     * @return
     */
    @GetMapping("/thirdParty/sms/send/sms")
    R sendSms(@RequestParam("phone") String phone, @RequestParam("code")String code);
}
