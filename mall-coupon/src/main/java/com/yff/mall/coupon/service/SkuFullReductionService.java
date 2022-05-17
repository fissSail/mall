package com.yff.mall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yff.common.to.SkuReductionTo;
import com.yff.common.utils.PageUtils;
import com.yff.mall.coupon.entity.SkuFullReductionEntity;

import java.util.Map;

/**
 * 商品满减信息
 *
 * @author yff
 * @email 1335799488@qq.com
 * @date 2021-12-08 21:08:03
 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSkuReduction(SkuReductionTo skuReductionTo);
}

