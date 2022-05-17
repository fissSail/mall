package com.yff.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yff.common.utils.PageUtils;
import com.yff.common.utils.Query;
import com.yff.mall.product.dao.ProductAttrValueDao;
import com.yff.mall.product.entity.AttrEntity;
import com.yff.mall.product.entity.ProductAttrValueEntity;
import com.yff.mall.product.service.AttrService;
import com.yff.mall.product.service.ProductAttrValueService;
import com.yff.mall.product.vo.BaseAttrs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {

    @Autowired
    private AttrService attrService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductAttrValueEntity> page = this.page(new Query<ProductAttrValueEntity>().getPage(params));

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public boolean saveProductAttrValueList(List<BaseAttrs> baseAttrs, Long spuId) {
        if(CollectionUtils.isEmpty(baseAttrs)){
            return false;
        }

        List<ProductAttrValueEntity> productAttrValueEntityList = baseAttrs.stream().map(data -> {
            AttrEntity attr = attrService.getById(data.getAttrId());
            ProductAttrValueEntity productAttrValue = new ProductAttrValueEntity();
            productAttrValue.setSpuId(spuId);
            productAttrValue.setAttrId(data.getAttrId());
            productAttrValue.setAttrName(attr.getAttrName());
            productAttrValue.setAttrValue(data.getAttrValues());
            productAttrValue.setQuickShow(data.getShowDesc());
            return productAttrValue;
        }).collect(Collectors.toList());

        return this.saveBatch(productAttrValueEntityList);
    }

    @Override
    public List<ProductAttrValueEntity> queryProductAttrValueBySpuId(Long spuId) {
        List<ProductAttrValueEntity> productAttrValueEntityList = this
                .list(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));

        return productAttrValueEntityList;
    }

    @Override
    @Transactional
    public void update(List<ProductAttrValueEntity> productAttrValues, Long spuId) {
        //先删除
        this.remove(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));

        //再新增
        List<ProductAttrValueEntity> productAttrValueEntityList = productAttrValues.stream().map(data -> {
            data.setSpuId(spuId);
            return data;
        }).collect(Collectors.toList());

        this.saveBatch(productAttrValueEntityList);
    }

}
