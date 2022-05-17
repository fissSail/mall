package com.yff.mall.order.vo;

import com.yff.mall.order.entity.OrderEntity;
import com.yff.mall.order.entity.OrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author yanfeifan
 * @Package com.yff.mall.order.vo
 * @Description
 * @date 2022/2/4 20:47
 */
@Data
public class OrderCreateVo {

    private OrderEntity orderEntity;
    private List<OrderItemEntity> orderItemEntities;
    private BigDecimal payPrice;
    private BigDecimal freight;
}
