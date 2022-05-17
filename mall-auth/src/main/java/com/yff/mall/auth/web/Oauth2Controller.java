package com.yff.mall.auth.web;

import com.yff.common.constant.AuthServerConstant;
import com.yff.common.vo.MemberRespVo;
import com.yff.mall.auth.service.Oauth2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

/**
 * @author yanfeifan
 * @Package com.yff.mall.auth.web
 * @Description
 * @date 2022/1/31 14:40
 */
@Controller
@RequestMapping("/oauth")
public class Oauth2Controller {

    @Autowired
    private Oauth2Service oauth2Service;

    @GetMapping("/github/success")
    public String githubOauth(@RequestParam("code") String code, HttpSession session) throws Exception {
        MemberRespVo memberRespVo = oauth2Service.githubOauth(code);
        session.setAttribute(AuthServerConstant.LOGIN_USER,memberRespVo);
        if(!ObjectUtils.isEmpty(memberRespVo)){
            return "redirect:" + AuthServerConstant.HOME_PAGE;
        }else{
            return "redirect:" + AuthServerConstant.AUTH_PAGE + "/login.html";
        }
    }

    @GetMapping("/gitee/success")
    public String giteeOauth(@RequestParam("code") String code, HttpSession session) throws Exception {
        MemberRespVo memberRespVo = oauth2Service.giteeOauth(code);
        if(!ObjectUtils.isEmpty(memberRespVo)){
            session.setAttribute(AuthServerConstant.LOGIN_USER,memberRespVo);
            return "redirect:" + AuthServerConstant.HOME_PAGE;
        }else{
            return "redirect:" + AuthServerConstant.AUTH_PAGE + "/login.html";
        }
    }
}
