package com.yff.mall.product.vo;

import com.yff.common.to.feign.SeckillSkuRedisTo;
import com.yff.mall.product.entity.SkuImagesEntity;
import com.yff.mall.product.entity.SkuInfoEntity;
import com.yff.mall.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

/**
 * @author yanfeifan
 * @Package com.yff.mall.product.vo
 * @Description
 * @date 2022/1/26 17:23
 */
@Data
public class SkuItemVo {

    private SkuInfoEntity skuInfoEntity;

    private Boolean hasStock = true;

    private List<SkuImagesEntity> skuImagesEntities;

    private SpuInfoDescEntity spuInfoDescEntity;

    private List<SaleAttrVo> saleAttrVos;

    private List<SpuItemBaseAttrVo> groupAttrVos;

    private SeckillSkuRedisTo seckillSkuRedisTo;

}
