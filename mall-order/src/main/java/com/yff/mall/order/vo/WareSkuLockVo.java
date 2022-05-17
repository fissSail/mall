package com.yff.mall.order.vo;

import lombok.Data;

import java.util.List;

/**
 * @author yanfeifan
 * @Package com.yff.mall.order.vo
 * @Description
 * @date 2022/2/5 11:41
 */
@Data
public class WareSkuLockVo {
    private String orderSn;
    private List<OrderItemVo> orderItemVoList;
}
