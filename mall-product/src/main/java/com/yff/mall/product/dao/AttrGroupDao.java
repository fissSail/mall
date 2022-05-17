package com.yff.mall.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yff.mall.product.entity.AttrGroupEntity;
import com.yff.mall.product.vo.SpuItemBaseAttrVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性分组
 *
 * @author yff
 * @email 1335799488@qq.com
 * @date 2021-12-07 21:18:59
 */
@Mapper
public interface AttrGroupDao extends BaseMapper<AttrGroupEntity> {

    List<SpuItemBaseAttrVo> getAttrGroupWithAttrsBySpuId(@Param("spuId") Long spuId, @Param("catalogId") Long catalogId);
}
