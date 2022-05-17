package com.yff.mall.cart.config;

import com.yff.common.constant.AuthServerConstant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * @author yanfeifan
 * @Package com.yff.mall.auth.config
 * @Description
 * @date 2022/2/1 11:21
 */
@Configuration
public class SessionConfig {

    @Bean
    public CookieSerializer cookieSerializer(){
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        cookieSerializer.setDomainName(AuthServerConstant.COOKIE_DOMAIN_NAME);
        cookieSerializer.setCookieName("MALL_SESSION");
        return cookieSerializer;
    }

    @Bean
    public RedisSerializer<Object> redisSerializer(){
        return new GenericJackson2JsonRedisSerializer();
    }
}
