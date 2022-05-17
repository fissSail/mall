package com.yff.common.to.mq;

import lombok.Data;

/**
 * @author yanfeifan
 * @Package com.yff.common.to.mq
 * @Description
 * @date 2022/2/6 14:33
 */
@Data
public class StockDetailTo {
    private Long id;
    /**
     * sku_id
     */
    private Long skuId;
    /**
     * sku_name
     */
    private String skuName;
    /**
     * 购买个数
     */
    private Integer skuNum;
    /**
     * 工作单id
     */
    private Long taskId;

    /**
     * 仓库id
     */
    private Long wareId;

    /**
     * 锁定状态
     */
    private Integer lockStatus;
}
