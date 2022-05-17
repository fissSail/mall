package com.yff.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yff.common.to.mq.QuickSeckillOrderTo;
import com.yff.common.utils.PageUtils;
import com.yff.mall.order.entity.OrderEntity;
import com.yff.mall.order.vo.*;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 订单
 *
 * @author yff
 * @email 1335799488@qq.com
 * @date 2021-12-08 21:22:59
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException;

    OrderSubmitRespVo submitOrder(OrderSubmitVo orderSubmitVo) throws ExecutionException, InterruptedException;

    OrderEntity getOrderStatusByOrderSn(String orderSn);

    void closeOrder(OrderEntity orderEntity);

    PayVo getPayOrder(String orderSn);

    PageUtils queryPageWithItem(Map<String, Object> params);

    String handlePayResult(PayAsyncVo vo);

    void orderSeckill(QuickSeckillOrderTo to);
}

