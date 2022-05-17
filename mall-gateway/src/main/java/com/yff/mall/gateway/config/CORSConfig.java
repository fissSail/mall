package com.yff.mall.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * @author yanfeifan
 * @Package com.yff.mall.gateway.config
 * @Description
 * @date 2021/12/13 17:38
 */
@Configuration
public class CORSConfig {

    @Bean
    public CorsWebFilter corsWebFilter(){
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        //配置跨域
        //允许哪些请求头跨域
        corsConfiguration.addAllowedHeader("*");
        //允许哪些请求方式跨域
        corsConfiguration.addAllowedMethod("*");
        //允许哪些请求来源跨域
        corsConfiguration.addAllowedOriginPattern("*");
        //允许携带cookie跨域
        corsConfiguration.setAllowCredentials(true);

        source.registerCorsConfiguration("/**",corsConfiguration);
        return new CorsWebFilter(source);
    }
}
