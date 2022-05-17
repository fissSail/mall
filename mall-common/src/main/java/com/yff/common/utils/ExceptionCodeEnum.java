package com.yff.common.utils;

/**
 * @author yanfeifan
 * @Package com.yff.common.utils
 * @Description
 * @date 2021/12/17 16:22
 */

public enum ExceptionCodeEnum {
    VAILD_EXCEPTION(10001, "参数格式校验失败"),
    UNKNOW_EXCEPTION(10000, "系统未知异常"),
    PRODUCT_EXCEPTION(11000,"商品保存elasticSearch错误");
    private int code;
    private String msg;

    ExceptionCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
