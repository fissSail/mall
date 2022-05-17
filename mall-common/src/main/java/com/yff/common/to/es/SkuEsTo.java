package com.yff.common.to.es;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author yanfeifan
 * @Package com.yff.common.to.es
 * @Description
 * @date 2022/1/4 15:27
 */
@Data
public class SkuEsTo {

    private Long skuId;

    private Long spuId;

    private String skuTitle;

    private BigDecimal skuPrice;

    private String skuImg;

    private Long saleCount;

    private Boolean hasStock;

    private Long hotScore;

    private Long brandId;

    private Long catalogId;

    private String brandName;

    private String brandImg;

    private String catalogName;

    private List<AttrValue> attrs;
}
