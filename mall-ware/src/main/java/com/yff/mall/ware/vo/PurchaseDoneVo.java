package com.yff.mall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @author yanfeifan
 * @Package com.yff.mall.ware.vo
 * @Description
 * @date 2021/12/26 20:28
 */
@Data
public class PurchaseDoneVo {

    private Long purchaseId;
    private List<PurchaseItemDoneVo> itemVos;
}
