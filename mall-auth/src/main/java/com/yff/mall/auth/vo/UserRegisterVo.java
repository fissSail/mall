package com.yff.mall.auth.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * @author yanfeifan
 * @Package com.yff.mall.auth
 * @Description
 * @date 2022/1/29 17:54
 */
@Data
public class UserRegisterVo {

    @NotEmpty(message="用户名不能为空")
    @Length(max = 18,min = 6,message="用户名必须是6-18位字符")
    private String userName;

    @NotEmpty(message="密码不能为空")
    @Length(max = 18,min = 6,message="密码必须是6-18位字符")
    private String password;

    @NotEmpty(message="手机号码不能为空")
    @Pattern(regexp = "^[1]([3-9])[0-9]{9}$",message="手机号码格式不正确")
    private String phone;

    @NotEmpty(message="验证码不能为空")
    private String code;
}
