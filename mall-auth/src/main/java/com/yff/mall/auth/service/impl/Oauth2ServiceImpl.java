package com.yff.mall.auth.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.yff.common.to.feign.GitUserTo;
import com.yff.common.utils.R;
import com.yff.mall.auth.config.OauthPropertiesConfig;
import com.yff.mall.auth.feign.MemberFeignService;
import com.yff.mall.auth.service.Oauth2Service;
import com.yff.mall.auth.util.HttpUtils;
import com.yff.mall.auth.vo.GiteeOAuthTokenVo;
import com.yff.common.vo.MemberRespVo;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yanfeifan
 * @Package com.yff.mall.auth.service.impl
 * @Description
 * @date 2022/1/31 16:46
 */
@Service
public class Oauth2ServiceImpl implements Oauth2Service {

    @Autowired
    private OauthPropertiesConfig oauthPropertiesConfig;

    @Autowired
    private MemberFeignService memberFeignService;

    @Override
    public MemberRespVo giteeOauth(String code) throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("client_id", oauthPropertiesConfig.getGiteeClientId());
        map.put("client_secret", oauthPropertiesConfig.getGiteeClientSecret());
        map.put("code", code);
        map.put("redirect_uri", oauthPropertiesConfig.getGiteeRedirectUri());
        map.put("grant_type", "authorization_code");
        HttpResponse post = HttpUtils.doPost("https://gitee.com", "/oauth/token", "post", new HashMap<>(), map, "");

        if (post.getStatusLine().getStatusCode() == 200) {
            String json = EntityUtils.toString(post.getEntity());
            GiteeOAuthTokenVo tokenVo = JSON.parseObject(json, GiteeOAuthTokenVo.class);
            Map<String, String> query = new HashMap<>();
            query.put("access_token", tokenVo.getAccessToken());
            //获取用户信息
            HttpResponse get = HttpUtils.doGet("https://gitee.com", "/api/v5/user", "get", new HashMap<>(), query);
            if (get.getStatusLine().getStatusCode() == 200) {
                String s = EntityUtils.toString(get.getEntity());
                GitUserTo gitUserTo = JSON.parseObject(s, GitUserTo.class);
                //远程查询登录信息
                try {
                    R r = memberFeignService.oauthGiteeLogin(gitUserTo);
                    if(r.getCode() == 0) {
                        MemberRespVo memberRespVo = r.getData(new TypeReference<MemberRespVo>() {});
                        return memberRespVo;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    public MemberRespVo githubOauth(String code) throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("client_id", oauthPropertiesConfig.getGithubClientId());
        map.put("client_secret", oauthPropertiesConfig.getGithubClientSecret());
        map.put("code", code);
        map.put("redirect_uri", oauthPropertiesConfig.getGithubRedirectUri());
        HttpResponse post = HttpUtils.doPost("https://github.com", "/login/oauth/access_token", "post", new HashMap<>(), map, "");

        if (post.getStatusLine().getStatusCode() == 200) {
            String json = EntityUtils.toString(post.getEntity());
            JSONObject jsonObject = this.getJsonStrByQueryUrl(json);
            String access_token = jsonObject.getString("access_token");

            Map<String, String> head = new HashMap<>();
            head.put("Authorization", "token " + access_token);
            HttpResponse get = HttpUtils.doGet("https://api.github.com", "/user", "get", head, new HashMap<>());
            if (get.getStatusLine().getStatusCode() == 200) {
                String s = EntityUtils.toString(get.getEntity());
                GitUserTo gitUserTo = JSON.parseObject(s, GitUserTo.class);
                try {
                    R r = memberFeignService.oauthGithubLogin(gitUserTo);
                    if(r.getCode() == 0){
                        MemberRespVo memberRespVo = r.getData(new TypeReference<MemberRespVo>() {});
                        return memberRespVo;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     *
     * @param paramStr
     * @return
     */
    public JSONObject getJsonStrByQueryUrl(String paramStr) {
        String[] params = paramStr.split("&");
        JSONObject obj = new JSONObject();
        for (int i = 0; i < params.length; i++) {
            String[] param = params[i].split("=");
            if (param.length >= 2) {
                String key = param[0];
                String value = param[1];
                for (int j = 2; j < param.length; j++) {
                    value += "=" + param[j];
                }
                try {
                    obj.put(key, value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return obj;
    }

}
