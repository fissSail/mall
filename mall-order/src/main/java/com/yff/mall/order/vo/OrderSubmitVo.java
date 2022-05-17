package com.yff.mall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author yanfeifan
 * @Package com.yff.mall.order.vo
 * @Description
 * @date 2022/2/4 20:10
 */
@Data
public class OrderSubmitVo {
    //收货地址id
    private Long addrId;

    //支付方式
    private Integer payType;

    //无需提交购买的商品，去购物车查询选中的商品

    //防重令牌
    private String token;

    //应付价格
    private BigDecimal payPrice;
}
