package com.yff.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yff.common.utils.PageUtils;
import com.yff.mall.product.entity.AttrAttrgroupRelationEntity;
import com.yff.mall.product.entity.AttrEntity;
import com.yff.mall.product.vo.AttrRespVo;
import com.yff.mall.product.vo.AttrVo;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author yff
 * @email 1335799488@qq.com
 * @date 2021-12-07 21:18:58
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    AttrRespVo getAllById(Long attrId);

    void saveAttr(AttrVo vo);

    void updateAttrById(AttrVo vo);

    void removeRelevanceByIds(List<AttrAttrgroupRelationEntity> voList);

    PageUtils queryAttrTypePage(Map<String, Object> params, Long catId, String attrType);

    PageUtils queryRelevanceAttrRelationPage(Map<String, Object> params, Long attrGroupId);

    PageUtils queryIrrelevantAttrRelationPage(Map<String, Object> params, Long attrGroupId);
}

