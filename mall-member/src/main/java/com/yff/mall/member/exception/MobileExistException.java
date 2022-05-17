package com.yff.mall.member.exception;

/**
 * @author yanfeifan
 * @Package com.yff.mall.member.exception
 * @Description
 * @date 2022/1/29 20:03
 */

public class MobileExistException extends RuntimeException {

    public MobileExistException() {
        super("手机号码已存在");
    }
}
