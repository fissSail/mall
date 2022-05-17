package com.yff.mall.member.interceptor;


import com.yff.common.constant.AuthServerConstant;
import com.yff.common.vo.MemberRespVo;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author yanfeifan
 * @Package com.yff.mall.cart.interceptor
 * @Description 在执行目标方法之前，判断用户登录状态，并封装传递给controller
 * @date 2022/2/3 10:01
 */
public class LoginInterceptor implements HandlerInterceptor {

    public static ThreadLocal<MemberRespVo> threadLocal = new ThreadLocal<>();

    /**
     * 目标方法执行之前
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String requestURI = request.getRequestURI();
        boolean match = new AntPathMatcher().match("/member/**", requestURI);
        if (match) {
            return true;
        }

        HttpSession session = request.getSession();
        MemberRespVo memberRespVo = (MemberRespVo) session.getAttribute(AuthServerConstant.LOGIN_USER);

        if (memberRespVo != null) {
            //同一线程共享数据
            threadLocal.set(memberRespVo);
            return true;
        } else {
            response.sendRedirect(AuthServerConstant.AUTH_PAGE + "/login.html");
            return false;
        }
    }
}
