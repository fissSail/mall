package com.yff.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yff.common.utils.PageUtils;
import com.yff.mall.order.entity.OrderSettingEntity;

import java.util.Map;

/**
 * 订单配置信息
 *
 * @author yff
 * @email 1335799488@qq.com
 * @date 2021-12-08 21:22:59
 */
public interface OrderSettingService extends IService<OrderSettingEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

