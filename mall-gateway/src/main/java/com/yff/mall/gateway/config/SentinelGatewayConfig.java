package com.yff.mall.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.fastjson.JSON;
import com.yff.common.utils.R;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * @author yanfeifan
 * @Package com.yff.mall.gateway.config
 * @Description
 * @date 2022/2/13 11:23
 */
@Configuration
public class SentinelGatewayConfig {

    public SentinelGatewayConfig(){
        GatewayCallbackManager.setBlockHandler((v1,v2)->{
            String s = JSON.toJSONString(R.error());
            Mono<ServerResponse> body = ServerResponse.ok().body(Mono.just(s), String.class);
            return body;
        });
    }
}
