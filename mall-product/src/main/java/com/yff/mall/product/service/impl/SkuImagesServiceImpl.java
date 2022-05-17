package com.yff.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yff.common.utils.PageUtils;
import com.yff.common.utils.Query;
import com.yff.mall.product.dao.SkuImagesDao;
import com.yff.mall.product.entity.SkuImagesEntity;
import com.yff.mall.product.service.SkuImagesService;
import com.yff.mall.product.vo.Images;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("skuImagesService")
public class SkuImagesServiceImpl extends ServiceImpl<SkuImagesDao, SkuImagesEntity> implements SkuImagesService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuImagesEntity> page = this.page(
                new Query<SkuImagesEntity>().getPage(params),
                new QueryWrapper<SkuImagesEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public boolean saveSkuImagesList(List<Images> skusImages, Long skuId) {
        if(CollectionUtils.isEmpty(skusImages)){
            return false;
        }

        List<SkuImagesEntity> skuImagesList = skusImages
                .stream()
                .filter(image-> StringUtils.hasText(image.getImgUrl()))
                .map(image -> {
            SkuImagesEntity skuImages = new SkuImagesEntity();
            BeanUtils.copyProperties(image,skuImages);
            skuImages.setSkuId(skuId);
            return skuImages;
        }).collect(Collectors.toList());

        return this.saveBatch(skuImagesList);
    }

}
