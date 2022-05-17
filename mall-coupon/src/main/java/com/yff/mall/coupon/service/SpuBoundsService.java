package com.yff.mall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yff.common.to.SpuBoundsTo;
import com.yff.common.utils.PageUtils;
import com.yff.mall.coupon.entity.SpuBoundsEntity;

import java.util.Map;

/**
 * 商品spu积分设置
 *
 * @author yff
 * @email 1335799488@qq.com
 * @date 2021-12-08 21:08:03
 */
public interface SpuBoundsService extends IService<SpuBoundsEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveBounds(SpuBoundsTo to);
}

