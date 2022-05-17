package com.yff.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yff.common.constant.ProductConstant;
import com.yff.common.utils.PageUtils;
import com.yff.common.utils.Query;
import com.yff.mall.product.dao.AttrAttrgroupRelationDao;
import com.yff.mall.product.dao.AttrDao;
import com.yff.mall.product.dao.AttrGroupDao;
import com.yff.mall.product.entity.AttrAttrgroupRelationEntity;
import com.yff.mall.product.entity.AttrEntity;
import com.yff.mall.product.entity.AttrGroupEntity;
import com.yff.mall.product.entity.CategoryEntity;
import com.yff.mall.product.service.AttrService;
import com.yff.mall.product.service.CategoryService;
import com.yff.mall.product.vo.AttrRespVo;
import com.yff.mall.product.vo.AttrVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AttrAttrgroupRelationDao relationDao;

    @Autowired
    private AttrGroupDao attrGroupDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @CacheEvict(value = "attr",key="'getAllById:'+#root.args[0]")
    @Override
    public AttrRespVo getAllById(Long attrId) {
        AttrRespVo respVo = new AttrRespVo();

        AttrEntity attr = this.getById(attrId);

        BeanUtils.copyProperties(attr, respVo);

        Long[] catelogPath = categoryService.getCatelogPath(attr.getCatelogId());

        respVo.setCatelogPath(catelogPath);

        //规格参数
        if (isAttrTypeBase(respVo.getAttrType())) {

            AttrAttrgroupRelationEntity entity = relationDao.selectOne(
                    new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));

            if (!Objects.isNull(entity) && !Objects.isNull(entity.getAttrGroupId())) {

                respVo.setAttrGroupId(entity.getAttrGroupId());
            }
        }
        return respVo;

    }

    @Transactional
    @Override
    public void saveAttr(AttrVo vo) {
        AttrEntity attrEntity = new AttrEntity();

        BeanUtils.copyProperties(vo, attrEntity);

        this.save(attrEntity);

        //规格参数
        if (isAttrTypeBase(attrEntity.getAttrType()) && !Objects.isNull(vo.getAttrGroupId())) {

            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();

            relationEntity.setAttrId(attrEntity.getAttrId());

            relationEntity.setAttrGroupId(vo.getAttrGroupId());

            relationDao.insert(relationEntity);
        }
    }

    @Override
    @Transactional
    public void updateAttrById(AttrVo vo) {
        AttrEntity attrEntity = new AttrEntity();

        BeanUtils.copyProperties(vo, attrEntity);

        this.updateById(attrEntity);

        //规格参数
        if (isAttrTypeBase(attrEntity.getAttrType())) {
            AttrAttrgroupRelationEntity attrAttrgroupRelation = new AttrAttrgroupRelationEntity();

            attrAttrgroupRelation.setAttrGroupId(vo.getAttrGroupId());

            attrAttrgroupRelation.setAttrId(attrEntity.getAttrId());

            Integer count = relationDao.selectCount(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));

            if (count > 0) {
                //修改
                relationDao.update(attrAttrgroupRelation, new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
            } else {
                //新增
                relationDao.insert(attrAttrgroupRelation);
            }
        }
    }

    @Transactional
    @Override
    public void removeRelevanceByIds(List<AttrAttrgroupRelationEntity> voList) {

        List<Long> attrIds = voList.stream().map(data -> data.getAttrId()).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(attrIds)) {
            return;
        }

        this.removeByIds(attrIds);
        //根据attrid与attrgroupId删除关联关系
        relationDao.deleteByAttrGroupIdAndAttrId(voList);
    }

    @Override
    public PageUtils queryAttrTypePage(Map<String, Object> params, Long catId, String attrType) {
        String key = (String) params.get("key");
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>()
                .eq(!Objects.isNull(catId) && catId != 0, "catelog_id", catId)
                .eq("attr_type", "base".equals(attrType) ?
                        ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() :
                        ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());

        if (StringUtils.hasText(key)) {
            wrapper.and(data ->
                    data.like(StringUtils.hasText(key), "attr_name", key)
                            .or()
                            .like(StringUtils.hasText(key), "value_select", key)
            );
        }

        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), wrapper);

        PageUtils pageUtils = new PageUtils(page);

        List<AttrEntity> records = page.getRecords();

        List<AttrRespVo> collect = records.stream().map(data -> {

            AttrRespVo respVo = new AttrRespVo();

            BeanUtils.copyProperties(data, respVo);

            if (!Objects.isNull(data.getCatelogId())) {

                CategoryEntity category = categoryService.getById(data.getCatelogId());

                respVo.setCatelogName(category.getName());
            }

            if (isAttrTypeBase(data.getAttrType())) {
                AttrAttrgroupRelationEntity entity = relationDao.selectOne(
                        new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", data.getAttrId()));

                if (!Objects.isNull(entity) && !Objects.isNull(entity.getAttrGroupId())) {

                    AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(entity.getAttrGroupId());

                    respVo.setGroupName(attrGroupEntity.getAttrGroupName());
                    respVo.setAttrGroupId(entity.getAttrGroupId());
                }
            }
            return respVo;
        }).collect(Collectors.toList());

        pageUtils.setList(collect);

        return pageUtils;
    }

    @Override
    public PageUtils queryRelevanceAttrRelationPage(Map<String, Object> params, Long attrGroupId) {

        List<AttrAttrgroupRelationEntity> relationEntityList = relationDao
                .selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq(attrGroupId != 0, "attr_group_id", attrGroupId));

        List<Long> attrIdList = relationEntityList.stream().map(data -> data.getAttrId()).collect(Collectors.toList());

        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>().eq("catelog_id", attrGroupId).in(CollectionUtils.isNotEmpty(attrIdList), "attr_id", attrIdList)
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryIrrelevantAttrRelationPage(Map<String, Object> params, Long attrGroupId) {

        //当前分组只能关联自己所属分类里面的属性
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrGroupId);
        Long catelogId = attrGroupEntity.getCatelogId();
        //当前分类下的所有分组的id
        List<Long> attrGroupIdList = attrGroupDao.selectList(new QueryWrapper<AttrGroupEntity>()
                        .eq("catelog_id", catelogId)).stream()
                .map(data -> data.getAttrGroupId()).collect(Collectors.toList());
        //当前分类下所有分组关联的属性
        List<AttrAttrgroupRelationEntity> relationEntityList = relationDao
                .selectList(new QueryWrapper<AttrAttrgroupRelationEntity>()
                        .in(CollectionUtils.isNotEmpty(attrGroupIdList), "attr_group_id", attrGroupIdList));

        List<Long> attrIdList = relationEntityList.stream().map(data -> data.getAttrId()).collect(Collectors.toList());

        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>().eq("catelog_id", catelogId).notIn(CollectionUtils.isNotEmpty(attrIdList), "attr_id", attrIdList)
        );

        return new PageUtils(page);
    }

    /**
     * 判断规格参数
     *
     * @param attrType
     * @return
     */
    private boolean isAttrTypeBase(int attrType) {
        return ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() == attrType;
    }

}
