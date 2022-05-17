package com.yff.common.constant;

/**
 * @author yanfeifan
 * @Package com.yff.common.constant
 * @Description
 * @date 2021/12/21 21:38
 */

public class ProductConstant {

    public enum AttrEnum {
        ATTR_TYPE_BASE(1, "规格参数"), ATTR_TYPE_SALE(0, "销售属性");

        private int code;
        private String msg;

        AttrEnum(int code, String msg) {
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

    public enum PublishStatusEnum{
        NEW_SPU(0,"新建状态"),PUTAWAY_SPU(1,"上架状态"),
        SOLDOUT_SPU(2,"下架状态");

        private int code;
        private String msg;

        PublishStatusEnum(int code, String msg) {
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
