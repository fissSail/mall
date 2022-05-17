package com.yff.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yff.common.utils.PageUtils;
import com.yff.mall.product.entity.BrandEntity;
import com.yff.mall.product.vo.BrandVo;

import java.util.List;
import java.util.Map;

/**
 * 品牌
 *
 * @author yff
 * @email 1335799488@qq.com
 * @date 2021-12-07 21:18:59
 */
public interface BrandService extends IService<BrandEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void updateRelevanceById(BrandVo vo);

    void saveBrand(BrandVo vo);

    List<BrandEntity> getByBrandIds(List<Long> brandIds);
}

