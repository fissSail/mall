package com.yff.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yff.common.utils.PageUtils;
import com.yff.mall.product.entity.ProductAttrValueEntity;
import com.yff.mall.product.vo.BaseAttrs;

import java.util.List;
import java.util.Map;

/**
 * spu属性值
 *
 * @author yff
 * @email 1335799488@qq.com
 * @date 2021-12-07 21:18:59
 */
public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    boolean saveProductAttrValueList(List<BaseAttrs> baseAttrs, Long spuId);

    List<ProductAttrValueEntity> queryProductAttrValueBySpuId(Long spuId);

    void update(List<ProductAttrValueEntity> productAttrValues, Long spuId);
}

