package com.yff.common.to.feign;

import lombok.Data;

/**
 * @author yanfeifan
 * @Package com.yff.common.to.feign
 * @Description
 * @date 2022/2/5 11:45
 */
@Data
public class LockStockResult {
    private Long skuId;
    private Integer num;
    private Boolean hasLock;
}
