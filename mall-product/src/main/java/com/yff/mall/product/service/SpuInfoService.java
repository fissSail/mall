package com.yff.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yff.common.utils.PageUtils;
import com.yff.mall.product.entity.SpuInfoEntity;
import com.yff.mall.product.vo.SpuSaveVo;

import java.util.Map;

/**
 * spu信息
 *
 * @author yff
 * @email 1335799488@qq.com
 * @date 2021-12-07 21:18:59
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSpuInfo(SpuSaveVo vo);

    void updatePublishStatusAndSaveEs(Long spuId);

    SpuInfoEntity getSpuInfoBySkuId(Long skuId);
}

