package com.yff.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yff.common.utils.PageUtils;
import com.yff.mall.product.entity.SkuSaleAttrValueEntity;
import com.yff.mall.product.vo.Attr;
import com.yff.mall.product.vo.SaleAttrVo;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author yff
 * @email 1335799488@qq.com
 * @date 2021-12-07 21:18:59
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    boolean saveSkuSaleAttrValueList(List<Attr> attr, Long skuId);

    List<SaleAttrVo> getSaleAttrsBySpuId(Long spuId);

    List<String> getSkuSaleAttrValues(Long skuId);

}

