package com.yff.mall.auth.web;

import com.alibaba.fastjson.TypeReference;
import com.yff.common.constant.AuthServerConstant;
import com.yff.common.utils.R;
import com.yff.common.vo.MemberRespVo;
import com.yff.mall.auth.feign.MemberFeignService;
import com.yff.mall.auth.vo.UserLoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yanfeifan
 * @Package com.yff.mall.auth.web
 * @Description
 * @date 2022/1/29 11:59
 */

@Controller
public class LoginController {

    @Autowired
    private MemberFeignService memberFeignService;

    @GetMapping("/login.html")
    public String loginPage(HttpSession session) {
        Object attribute = session.getAttribute(AuthServerConstant.LOGIN_USER);
        if (attribute == null) {
            return "login";
        }
        return "redirect:" + AuthServerConstant.HOME_PAGE;
    }

    @PostMapping("/login")
    public String login(@Valid UserLoginVo userLoginVo, RedirectAttributes redirectAttributes, HttpSession session) {
        R r = memberFeignService.login(userLoginVo);
        if (r.getCode() == 0) {
            MemberRespVo memberRespVo = r.getData(new TypeReference<MemberRespVo>() {
            });
            session.setAttribute(AuthServerConstant.LOGIN_USER, memberRespVo);
            return "redirect:" + AuthServerConstant.HOME_PAGE;
        } else {
            Map<String, Object> errorMap = new HashMap<String, Object>();
            errorMap.put("msg", "登入失败");
            redirectAttributes.addFlashAttribute("error", errorMap);
            return "redirect:" + AuthServerConstant.AUTH_PAGE + "/login.hmtl";
        }
    }

}
