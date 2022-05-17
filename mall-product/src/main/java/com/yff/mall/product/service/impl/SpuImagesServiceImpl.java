package com.yff.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yff.common.utils.PageUtils;
import com.yff.common.utils.Query;
import com.yff.mall.product.dao.SpuImagesDao;
import com.yff.mall.product.entity.SpuImagesEntity;
import com.yff.mall.product.service.SpuImagesService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("spuImagesService")
public class SpuImagesServiceImpl extends ServiceImpl<SpuImagesDao, SpuImagesEntity> implements SpuImagesService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuImagesEntity> page = this.page(
                new Query<SpuImagesEntity>().getPage(params),
                new QueryWrapper<SpuImagesEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public boolean saveSpuImages(List<String> images, Long spuId) {

        if(CollectionUtils.isEmpty(images)){
            return false;
        }

        List<SpuImagesEntity> spuImagesEntityList = images
                .stream()
                .map(image -> {
            SpuImagesEntity spuImages = new SpuImagesEntity();
            spuImages.setSpuId(spuId);
            spuImages.setImgName(image.substring(image.lastIndexOf("/")+1));
            spuImages.setImgUrl(image);
            return spuImages;
        }).collect(Collectors.toList());

        return this.saveBatch(spuImagesEntityList);
    }

}
