package com.yff.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yff.common.utils.PageUtils;
import com.yff.mall.product.entity.AttrGroupEntity;
import com.yff.mall.product.vo.AttrGroupRespVo;
import com.yff.mall.product.vo.AttrGroupVo;
import com.yff.mall.product.vo.SpuItemBaseAttrVo;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author yff
 * @email 1335799488@qq.com
 * @date 2021-12-07 21:18:59
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params,Long catId);

    AttrGroupVo getAllById(Long attrGroupId);

    List<AttrGroupRespVo> queryAttrGroupWithAttrsByCatalogId(Long catalogId);

    List<SpuItemBaseAttrVo> getAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId);

}

