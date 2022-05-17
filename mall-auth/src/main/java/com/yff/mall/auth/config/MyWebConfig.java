package com.yff.mall.auth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author yanfeifan
 * @Package com.yff.mall.auth.config
 * @Description
 * @date 2022/1/29 15:57
 */
@Configuration
public class MyWebConfig implements WebMvcConfigurer {
    /**
     * 用于需要路径跳转页面但不需要处理逻辑的视图映射
     * 如下 可使用该配置，不需要再写controller
     * @GetMapping("/register.html")
     *     public String login(){
     *         return "register";
     *     }
     * @param registry
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        //registry.addViewController("/login.html").setViewName("login");
        registry.addViewController("/register.html").setViewName("register");
    }
}
