package com.yff.mall.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author yanfeifan
 * @Package com.yff.mall.auth.config
 * @Description
 * @date 2022/2/1 9:51
 */
@ConfigurationProperties(prefix = "oauth2")
@Component
@Data
public class OauthPropertiesConfig {

    private String githubClientId;
    private String githubClientSecret;
    private String githubRedirectUri;
    private String giteeClientId;
    private String giteeClientSecret;
    private String giteeRedirectUri;
}
