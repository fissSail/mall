package com.yff.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yff.common.utils.PageUtils;
import com.yff.common.utils.Query;
import com.yff.mall.product.dao.AttrGroupDao;
import com.yff.mall.product.entity.AttrAttrgroupRelationEntity;
import com.yff.mall.product.entity.AttrEntity;
import com.yff.mall.product.entity.AttrGroupEntity;
import com.yff.mall.product.service.AttrAttrgroupRelationService;
import com.yff.mall.product.service.AttrGroupService;
import com.yff.mall.product.service.AttrService;
import com.yff.mall.product.service.CategoryService;
import com.yff.mall.product.vo.AttrGroupRespVo;
import com.yff.mall.product.vo.AttrGroupVo;
import com.yff.mall.product.vo.SpuItemBaseAttrVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private AttrAttrgroupRelationService relationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catId) {
        String key = (String) params.get("key");
        QueryWrapper<AttrGroupEntity> wrapper =
                new QueryWrapper<AttrGroupEntity>()
                        .eq(!Objects.isNull(catId) && catId != 0, "catelog_id", catId);

        if(StringUtils.hasText(key)){
            wrapper.and((data) -> data.
                    like("attr_group_name", key)
                    .or()
                    .like("descript", key));
        }

        IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params),wrapper);

        return new PageUtils(page);
    }

    @Override
    public AttrGroupVo getAllById(Long attrGroupId) {
        AttrGroupVo vo = new AttrGroupVo();
        AttrGroupEntity attrGroup = this.getById(attrGroupId);
        BeanUtils.copyProperties(attrGroup,vo);
        Long[] catelogPath = categoryService.getCatelogPath(attrGroup.getCatelogId());
        vo.setCatelogPath(catelogPath);
        return vo;
    }

    @Override
    public List<AttrGroupRespVo> queryAttrGroupWithAttrsByCatalogId(Long catalogId) {
        //根据分类id查询属性分组集
        List<AttrGroupEntity> attrGroupEntityList =
                baseMapper.selectList(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catalogId));

        List<AttrGroupRespVo> list = attrGroupEntityList.stream().map(data -> {
            AttrGroupRespVo vo = new AttrGroupRespVo();
            BeanUtils.copyProperties(data,vo);
            List<Long> attrIdList =
                    relationService.list(new QueryWrapper<AttrAttrgroupRelationEntity>()
                            .eq("attr_group_id", data.getAttrGroupId()))
                            .stream()
                            .map(item->item.getAttrId())
                            .collect(Collectors.toList());

            List<AttrEntity> attrEntityList = attrService.listByIds(attrIdList);
            vo.setAttrs(attrEntityList);
            return vo;
        }).collect(Collectors.toList());
        return list;
    }

    @Override
    public List<SpuItemBaseAttrVo> getAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId) {
        //查出spu对应所有属性的分组以及分组下所有属性对应的值
        List<SpuItemBaseAttrVo> list = this.baseMapper.getAttrGroupWithAttrsBySpuId(spuId,catalogId);

        return list;
    }

}
