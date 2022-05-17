package com.yff.common.to.feign;

import lombok.Data;

/**
 * @author yanfeifan
 * @Package com.yff.mall.ware.vo
 * @Description
 * @date 2022/1/4 17:29
 */
@Data
public class WareSkuTo {

    private Long skuId;
    /**
     * 是否有库存
     */
    private Boolean hasStock;
}
