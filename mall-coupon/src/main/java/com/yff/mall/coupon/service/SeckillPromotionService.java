package com.yff.mall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yff.common.utils.PageUtils;
import com.yff.mall.coupon.entity.SeckillPromotionEntity;

import java.util.Map;

/**
 * 秒杀活动
 *
 * @author yff
 * @email 1335799488@qq.com
 * @date 2021-12-08 21:08:03
 */
public interface SeckillPromotionService extends IService<SeckillPromotionEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

