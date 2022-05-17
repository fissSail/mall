package com.yff.mall.auth.vo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author yanfeifan
 * @Package com.yff.mall.auth.vo
 * @Description
 * @date 2022/1/29 20:43
 */
@Data
public class UserLoginVo {

    @NotEmpty(message="用户名不能为空")
    private String userName;

    @NotEmpty(message="密码不能为空")
    private String password;
}
