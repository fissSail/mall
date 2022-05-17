package com.yff.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yff.common.utils.PageUtils;
import com.yff.common.utils.Query;
import com.yff.mall.product.dao.BrandDao;
import com.yff.mall.product.dao.CategoryBrandRelationDao;
import com.yff.mall.product.dao.CategoryDao;
import com.yff.mall.product.entity.BrandEntity;
import com.yff.mall.product.entity.CategoryBrandRelationEntity;
import com.yff.mall.product.service.CategoryBrandRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private BrandDao brandDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryCategoryBrandRelationPage(Map<String, Object> params) {

        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>().eq("brand_id", params.get("brandId"))
        );

        return new PageUtils(page);
    }

    @Override
    public void saveAndName(CategoryBrandRelationEntity categoryBrandRelation) {
        categoryBrandRelation.setCatelogName(categoryDao.selectById(categoryBrandRelation.getCatelogId()).getName());
        categoryBrandRelation.setBrandName(brandDao.selectById(categoryBrandRelation.getBrandId()).getName());
        this.save(categoryBrandRelation);
    }

    @Override
    public boolean updateByBrand(Long brandId, String brandName) {
        CategoryBrandRelationEntity categoryBrandRelation = new CategoryBrandRelationEntity();
        categoryBrandRelation.setBrandName(brandName);
        return this.update(categoryBrandRelation,new QueryWrapper<CategoryBrandRelationEntity>().eq("brand_id",brandId));
    }

    @Override
    public boolean updateByCategory(Long catelogId, String catelogName) {
        CategoryBrandRelationEntity categoryBrandRelation = new CategoryBrandRelationEntity();
        categoryBrandRelation.setCatelogName(catelogName);
        return this.update(categoryBrandRelation,new QueryWrapper<CategoryBrandRelationEntity>().eq("catelog_id",catelogId));
    }

    @Override
    public List<BrandEntity> queryBrandListByCatId(Long catId) {

        List<CategoryBrandRelationEntity> categoryBrandRelationEntityList = this.list(
                new QueryWrapper<CategoryBrandRelationEntity>().eq("catelog_id", catId));

        List<Long> brandIdList = categoryBrandRelationEntityList.stream().map(data -> data.getBrandId()).collect(Collectors.toList());

        List<BrandEntity> brandEntities = brandDao.selectBatchIds(brandIdList);

        return brandEntities;
    }
}
