package com.yff.mall.testssoserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * @author yanfeifan
 * @Package com.yff.mall.testssoserver.controller
 * @Description
 * @date 2022/2/1 20:50
 */
@Controller
public class LoginController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @GetMapping("/login")
    public String login(@RequestParam("redirect_url") String redirect_url,
                        Model model,
                        @CookieValue(value = "token",required = false) String token) {

        if(StringUtils.hasText(token)){
            return "redirect:"+redirect_url+"?token="+token;
        }


        model.addAttribute("redirect_url", redirect_url);
        return "login";
    }

    @PostMapping("/dologin")
    public String dologin(String redirect_url,
                          String username,
                          String password,
                          HttpServletResponse response) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        stringRedisTemplate.opsForValue().set(uuid, username);
        Cookie cookie = new Cookie("token",uuid);
        response.addCookie(cookie);
        return "redirect:" + redirect_url + "?token=" + uuid;
    }
}
