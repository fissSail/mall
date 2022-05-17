package com.yff.mall.cart.interceptor;


import com.yff.common.constant.AuthServerConstant;
import com.yff.common.constant.CartConstant;
import com.yff.common.vo.MemberRespVo;
import com.yff.mall.cart.vo.UserInfoVo;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * @author yanfeifan
 * @Package com.yff.mall.cart.interceptor
 * @Description 在执行目标方法之前，判断用户登录状态，并封装传递给controller
 * @date 2022/2/3 10:01
 */
public class CartInterceptor implements HandlerInterceptor {

    public static ThreadLocal<UserInfoVo> threadLocal = new ThreadLocal<>();

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

        UserInfoVo userInfoVo = new UserInfoVo();

        HttpSession session = request.getSession();
        MemberRespVo memberRespVo = (MemberRespVo) session.getAttribute(AuthServerConstant.LOGIN_USER);
        //登录状态
        if (!ObjectUtils.isEmpty(memberRespVo)) {
            userInfoVo.setUserId(memberRespVo.getId());
        }
        Cookie[] cookies = request.getCookies();
        if (ArrayUtils.isNotEmpty(cookies)) {
            for (Cookie cookie : cookies) {
                String name = cookie.getName();
                if (CartConstant.TEMP_USER_COOKIE_NAME.equals(name)) {
                    userInfoVo.setUserKey(cookie.getValue());
                    userInfoVo.setTempUser(true);
                }
            }
        }

        //如果没有临时用户分配一个临时用户
        if (!StringUtils.hasText(userInfoVo.getUserKey())) {
            String uuid = UUID.randomUUID().toString().replace("-", "");
            userInfoVo.setUserKey(uuid);
        }

        threadLocal.set(userInfoVo);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        UserInfoVo userInfoVo = threadLocal.get();
        if (!userInfoVo.getTempUser()) {
            Cookie cookie = new Cookie(CartConstant.TEMP_USER_COOKIE_NAME, userInfoVo.getUserKey());
            cookie.setDomain("mall.com");
            cookie.setMaxAge(CartConstant.TEMP_USER_COOKIE_TIMEOUT);
            response.addCookie(cookie);
        }
    }
}
