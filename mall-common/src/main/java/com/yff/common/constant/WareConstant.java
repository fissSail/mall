package com.yff.common.constant;

/**
 * @author yanfeifan
 * @Package com.yff.common.constant
 * @Description
 * @date 2021/12/21 21:38
 */

public class WareConstant {

    public enum PurchaseStatusEnum {
        PURCHASE_NEW(0, "新建"),
        PURCHASE_ALLOCATED(1, "已分配"),
        PURCHASE_ALREADY_RECEIVED(2, "已领取"),
        PURCHASE_ACCOMPLISH(3, "已完成"),
        PURCHASE_EXCEPTION(4, "有异常");

        private int code;
        private String msg;

        PurchaseStatusEnum(int code, String msg) {
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

    public enum PurchaseDetailStatusEnum {
        PURCHASE_NEW(0, "新建"),
        PURCHASE_ALLOCATED(1, "已分配"),
        PURCHASE_BEING(2, "正在采购"),
        PURCHASE_ACCOMPLISH(3, "已完成"),
        PURCHASE_FAILED(4, "采购失败");

        private int code;
        private String msg;

        PurchaseDetailStatusEnum(int code, String msg) {
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
}
