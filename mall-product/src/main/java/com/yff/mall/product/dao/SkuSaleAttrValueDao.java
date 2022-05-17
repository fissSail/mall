package com.yff.mall.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yff.mall.product.entity.SkuSaleAttrValueEntity;
import com.yff.mall.product.vo.SaleAttrVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * sku销售属性&值
 *
 * @author yff
 * @email 1335799488@qq.com
 * @date 2021-12-07 21:18:59
 */
@Mapper
public interface SkuSaleAttrValueDao extends BaseMapper<SkuSaleAttrValueEntity> {

    List<SaleAttrVo> getSaleAttrsBySpuId(@Param("spuId") Long spuId);

    List<String> getSkuSaleAttrValues(@Param("skuId") Long skuId);

}
