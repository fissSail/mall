package com.yff.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yff.common.utils.PageUtils;
import com.yff.mall.product.entity.SkuImagesEntity;
import com.yff.mall.product.vo.Images;

import java.util.List;
import java.util.Map;

/**
 * sku图片
 *
 * @author yff
 * @email 1335799488@qq.com
 * @date 2021-12-07 21:18:59
 */
public interface SkuImagesService extends IService<SkuImagesEntity> {

    PageUtils queryPage(Map<String, Object> params);

    boolean saveSkuImagesList(List<Images> skusImages, Long skuId);
}

