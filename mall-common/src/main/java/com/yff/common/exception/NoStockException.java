package com.yff.common.exception;

/**
 * @author yanfeifan
 * @Package com.yff.mall.ware.exception
 * @Description
 * @date 2022/2/5 13:48
 */

public class NoStockException extends RuntimeException {
    public NoStockException(Long skuId) {
        super(skuId + "没有足够的库存");
    }

    public NoStockException(String msg) {
        super(msg);
    }
}

