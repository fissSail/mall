package com.yff.mall.ware.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author yanfeifan
 * @Package com.yff.mall.order.vo
 * @Description
 * @date 2022/2/4 11:14
 */
@Data
public class OrderItemVo {

    private Long skuId;

    private String title;

    private String image;

    private List<String> skuAttr;

    private BigDecimal price;

    private Integer count;

    private BigDecimal totalPrice;
}
