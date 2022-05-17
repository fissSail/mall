package com.yff.mall.auth.web;

import com.alibaba.fastjson.TypeReference;
import com.yff.common.constant.AuthServerConstant;
import com.yff.common.utils.R;
import com.yff.mall.auth.vo.UserRegisterVo;
import com.yff.mall.auth.feign.MemberFeignService;
import com.yff.mall.auth.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yanfeifan
 * @Package com.yff.mall.auth.web
 * @Description
 * @date 2022/1/29 15:18
 */
@Controller
@RequestMapping("/register")
public class RegisterController {

    @Autowired
    private RegisterService registerService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private MemberFeignService memberFeignService;

    @GetMapping("/send/sms")
    @ResponseBody
    public R sendSms(@RequestParam("phone") String phone) {
        return registerService.sendSms(phone);
    }

    @PostMapping("/register")
    public String register(@Valid UserRegisterVo userRegisterVo, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = bindingResult.getFieldErrors()
                    .stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            return redirectRegister(redirectAttributes, errorMap);
        }

        //注册
        //校验验证码
        String redisCode = stringRedisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_PREFIX + userRegisterVo.getPhone());
        if (!StringUtils.hasText(redisCode)) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("code", "验证码错误");
            return redirectRegister(redirectAttributes, errorMap);
        }
        if (userRegisterVo.getCode().equalsIgnoreCase(redisCode)) {
            //验证码验证通过，删除redis验证码
            stringRedisTemplate.delete(AuthServerConstant.SMS_CODE_PREFIX + userRegisterVo.getPhone());

            R r = memberFeignService.register(userRegisterVo);

            if (r.getCode() == 0) {
                return "redirect:" + AuthServerConstant.AUTH_PAGE + "/login.html";
            } else {
                Map<String, String> errorMap = new HashMap<>();
                errorMap.put("msg", r.getData(new TypeReference<String>() {
                }));
                return redirectRegister(redirectAttributes, errorMap);
            }
        } else {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("code", "验证码错误");
            return redirectRegister(redirectAttributes, errorMap);
        }
    }

    private String redirectRegister(RedirectAttributes redirectAttributes, Map<String, String> errorMap) {
        redirectAttributes.addFlashAttribute("errors", errorMap);
        //校验错误
        return "redirect:" + AuthServerConstant.AUTH_PAGE + "/register.html";
    }


}
