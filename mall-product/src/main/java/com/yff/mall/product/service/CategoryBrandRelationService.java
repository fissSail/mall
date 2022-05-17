package com.yff.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yff.common.utils.PageUtils;
import com.yff.mall.product.entity.BrandEntity;
import com.yff.mall.product.entity.CategoryBrandRelationEntity;

import java.util.List;
import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author yff
 * @email 1335799488@qq.com
 * @date 2021-12-07 21:18:59
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryCategoryBrandRelationPage(Map<String, Object> params);

    void saveAndName(CategoryBrandRelationEntity categoryBrandRelation);

    boolean updateByBrand(Long brandId, String brandName);

    boolean updateByCategory(Long catelogId, String catelogName);

    List<BrandEntity> queryBrandListByCatId(Long catId);
}

