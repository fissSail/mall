package com.yff.mall.member.exception;

/**
 * @author yanfeifan
 * @Package com.yff.mall.member.exception
 * @Description
 * @date 2022/1/29 20:02
 */

public class UserNameExistException extends RuntimeException {

    public UserNameExistException() {
        super("用户名已存在");
    }
}
