package com.yff.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author yanfeifan
 * @Package com.yff.common.to
 * @Description
 * @date 2021/12/26 13:31
 */
@Data
public class SkuReductionTo {

    private Long skuId;
    private int fullCount;
    private BigDecimal discount;
    private int countStatus;
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private int priceStatus;
    private List<MemberPrice> memberPrice;
}
