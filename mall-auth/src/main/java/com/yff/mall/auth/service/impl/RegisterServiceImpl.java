package com.yff.mall.auth.service.impl;

import com.yff.common.constant.AuthServerConstant;
import com.yff.common.utils.R;
import com.yff.mall.auth.feign.ThirdPartyFeignService;
import com.yff.mall.auth.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author yanfeifan
 * @Package com.yff.mall.auth.service.impl
 * @Description
 * @date 2022/1/29 16:50
 */
@Service
public class RegisterServiceImpl implements RegisterService {

    @Autowired
    private ThirdPartyFeignService thirdPartyFeignService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public R sendSms(String phone) {
        //接口防刷
        String redisCodeMillis = stringRedisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_MILLIS + phone);

        if(StringUtils.hasText(redisCodeMillis)){
            long l = Long.parseLong(redisCodeMillis);

            if(System.currentTimeMillis() - l < 6000000){
                //60秒不能再发
                return  R.error("验证码获取频率太高，请稍后再试");
            }
        }

        //随机验证码
        String code = UUID.randomUUID().toString().substring(0, 5);
        //验证码校验 存入redis
        stringRedisTemplate.opsForValue().set(AuthServerConstant.SMS_CODE_PREFIX + phone, code, 6000, TimeUnit.SECONDS);
        stringRedisTemplate.opsForValue().set(AuthServerConstant.SMS_CODE_MILLIS + phone, System.currentTimeMillis() + "", 6000, TimeUnit.SECONDS);


        return thirdPartyFeignService.sendSms(phone, code);
    }
}
