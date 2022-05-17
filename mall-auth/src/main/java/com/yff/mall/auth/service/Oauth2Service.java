package com.yff.mall.auth.service;

import com.yff.common.vo.MemberRespVo;

/**
 * @author yanfeifan
 * @Package com.yff.mall.auth.service
 * @Description
 * @date 2022/1/31 16:45
 */

public interface Oauth2Service {

    MemberRespVo giteeOauth(String code) throws Exception;

    MemberRespVo githubOauth(String code) throws Exception;
}
