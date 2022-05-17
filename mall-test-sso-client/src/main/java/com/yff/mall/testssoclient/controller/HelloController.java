package com.yff.mall.testssoclient.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;

/**
 * @author yanfeifan
 * @Package com.yff.mall.testssoclient.controller
 * @Description
 * @date 2022/2/1 16:27
 */
@Controller
public class HelloController {

    /**
     * 无需登录
     * @return
     */
    @ResponseBody
    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }

    @GetMapping("/emp")
    public String emp(Model model, HttpSession session,
                      @RequestParam(value = "token",required = false)String token){
        if(StringUtils.hasText(token)){
            //服务端登录成功跳回就会带上
            //获取当前token对应的用户信息
            session.setAttribute("user","张三");
        }

        Object user = session.getAttribute("user");

        if(user == null){
            //未登录，重定向到登录
            return "redirect:http://server.com:8070/login?redirect_url=http://client1.com:8071/emp";
        }
        ArrayList<String> list = new ArrayList<String>();
        list.add("a");
        list.add("b");
        list.add("c");
        model.addAttribute("list",list);
        return "list";
    }
}
