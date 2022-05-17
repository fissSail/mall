package com.yff.mall.ware.vo;

import lombok.Data;

/**
 * @author yanfeifan
 * @Package com.yff.mall.ware.vo
 * @Description
 * @date 2021/12/26 20:30
 */
@Data
public class PurchaseItemDoneVo {
    private Long itemId;
    private Integer status;
    private String reason;
}
