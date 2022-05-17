package com.yff.mall.mallthirdparty.controller;

import com.yff.common.utils.R;
import com.yff.mall.mallthirdparty.component.SendSmsComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yanfeifan
 * @Package com.yff.mall.mallthirdparty.controller
 * @Description
 * @date 2022/1/29 16:21
 */
@RestController
@RequestMapping("thirdParty/sms")
public class SendSmsController {

    @Autowired
    private SendSmsComponent sendSmsComponent;

    @GetMapping("/send/sms")
    public R sendSms(@RequestParam("phone") String phone, @RequestParam("code")String code){
        sendSmsComponent.sandSms(phone,code);
        return R.ok();
    }

}
