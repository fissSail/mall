package com.yff.common.to.mq;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author yanfeifan
 * @Package com.yff.common.to.mq
 * @Description
 * @date 2022/2/12 15:41
 */
@Data
public class QuickSeckillOrderTo {

    /**
     * 订单号
     */
    private String orderSn;
    /**
     * 活动场次id
     */
    private Long promotionSessionId;
    /**
     * 商品id
     */
    private Long skuId;
    /**
     * 秒杀价格
     */
    private BigDecimal seckillPrice;
    /**
     * 秒杀总量
     */
    private Integer count;
    /**
     * 会员id
     */
    private Long memberId;
}
