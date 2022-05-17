package com.yff.common.to.feign;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author yanfeifan
 * @Package com.yff.common.to.feign
 * @Description
 * @date 2022/2/10 17:24
 */
@Data
public class SeckillSessionTo {

    private Long id;
    /**
     * 场次名称
     */
    private String name;
    /**
     * 每日开始时间
     */
    private Date startTime;
    /**
     * 每日结束时间
     */
    private Date endTime;
    /**
     * 启用状态
     */
    private Integer status;
    /**
     * 创建时间
     */
    private Date createTime;

    private List<SeckillSkuRelationTo> seckillSkuRelationTos;
}
