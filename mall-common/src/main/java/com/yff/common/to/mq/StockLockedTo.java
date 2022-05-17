package com.yff.common.to.mq;

import lombok.Data;

/**
 * @author yanfeifan
 * @Package com.yff.common.to.mq
 * @Description
 * @date 2022/2/6 14:25
 */
@Data
public class StockLockedTo {
    private Long taskId;//库存工作单id
    private StockDetailTo stockDetailTo; //详情id
}
