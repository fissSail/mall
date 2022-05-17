package com.yff.mall.product.dao;

import com.yff.mall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 *
 * @author yff
 * @email 1335799488@qq.com
 * @date 2021-12-07 21:18:59
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {

}
