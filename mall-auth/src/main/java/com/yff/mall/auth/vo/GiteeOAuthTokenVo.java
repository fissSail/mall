package com.yff.mall.auth.vo;

import lombok.Data;

/**
 * @author yanfeifan
 * @Package com.yff.mall.mallthirdparty.vo
 * @Description
 * @date 2022/1/31 15:51
 */
@Data
public class GiteeOAuthTokenVo {

    private String accessToken;
    private String tokenType;
    private Long expiresIn;
    private String refreshToken;
    private String scope;
    private Long createdAt;
}
