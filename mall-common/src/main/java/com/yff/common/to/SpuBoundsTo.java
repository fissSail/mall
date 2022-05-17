package com.yff.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author yanfeifan
 * @Package com.yff.common.to
 * @Description
 * @date 2021/12/26 10:56
 */
@Data
public class SpuBoundsTo {

    /**
     *
     */
    private Long spuId;
    /**
     * 成长积分
     */
    private BigDecimal growBounds;
    /**
     * 购物积分
     */
    private BigDecimal buyBounds;
}
