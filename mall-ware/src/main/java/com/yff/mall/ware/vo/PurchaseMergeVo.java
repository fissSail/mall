package com.yff.mall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @author yanfeifan
 * @Package com.yff.mall.ware.vo
 * @Description
 * @date 2021/12/26 19:10
 */
@Data
public class PurchaseMergeVo {

    private List<Long> items;
    private Long purchaseId;
}
