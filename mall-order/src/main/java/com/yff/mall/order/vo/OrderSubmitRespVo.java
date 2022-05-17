package com.yff.mall.order.vo;

import com.yff.mall.order.entity.OrderEntity;
import lombok.Data;

/**
 * @author yanfeifan
 * @Package com.yff.mall.order.vo
 * @Description
 * @date 2022/2/4 20:28
 */
@Data
public class OrderSubmitRespVo {
    private OrderEntity orderEntity;
    private Integer code; //0成功
}
