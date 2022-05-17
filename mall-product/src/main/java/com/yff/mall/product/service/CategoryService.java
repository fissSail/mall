package com.yff.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yff.common.utils.PageUtils;
import com.yff.mall.product.entity.CategoryEntity;
import com.yff.mall.product.vo.CategoryRespVo;
import com.yff.mall.product.vo.CategoryVo;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author yff
 * @email 1335799488@qq.com
 * @date 2021-12-07 21:18:59
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryVo> queryPageByTree();

    Long[] getCatelogPath(Long catId);

    void updateRelevanceById(CategoryEntity category);

    List<CategoryEntity> getCategoryStair(int catLevel);

    Map<Long, List<CategoryRespVo>> getCategoryJson();
}

