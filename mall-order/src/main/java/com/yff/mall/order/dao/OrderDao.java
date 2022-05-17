package com.yff.mall.order.dao;

import com.yff.mall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 *
 * @author yff
 * @email 1335799488@qq.com
 * @date 2021-12-08 21:22:59
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {

}
