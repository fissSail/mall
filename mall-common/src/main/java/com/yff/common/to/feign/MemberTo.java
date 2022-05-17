package com.yff.common.to.feign;

import lombok.Data;

/**
 * @author yanfeifan
 * @Package com.yff.common.to.feign
 * @Description
 * @date 2022/1/29 19:37
 */
@Data
public class MemberTo {

    /**
     * 用户名
     */
    private String userName;
    /**
     * 密码
     */
    private String password;
    /**
     * 手机号码
     */
    private String phone;

}
