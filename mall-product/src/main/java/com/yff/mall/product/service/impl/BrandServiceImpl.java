package com.yff.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yff.common.utils.PageUtils;
import com.yff.common.utils.Query;
import com.yff.mall.product.dao.BrandDao;
import com.yff.mall.product.entity.BrandEntity;
import com.yff.mall.product.service.BrandService;
import com.yff.mall.product.service.CategoryBrandRelationService;
import com.yff.mall.product.vo.BrandVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
                new QueryWrapper<BrandEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void updateRelevanceById(BrandVo vo) {
        BrandEntity brand = new BrandEntity();
        BeanUtils.copyProperties(vo,brand);
        this.updateById(brand);
        if(StringUtils.hasText(brand.getName())){
            categoryBrandRelationService.updateByBrand(brand.getBrandId(),brand.getName());
        }
    }

    @Override
    public void saveBrand(BrandVo vo) {
        BrandEntity brandEntity = new BrandEntity();
        BeanUtils.copyProperties(vo,brandEntity);
        this.save(brandEntity);
    }

    @CacheEvict(value = "brand",key = "'getByBrandIds:'+#root.args[0]")
    @Override
    public List<BrandEntity> getByBrandIds(List<Long> brandIds) {
        List<BrandEntity> brandEntities = baseMapper.selectBatchIds(brandIds);
        return brandEntities;
    }

}
