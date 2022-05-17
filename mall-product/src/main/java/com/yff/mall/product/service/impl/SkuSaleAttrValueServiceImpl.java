package com.yff.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yff.common.utils.PageUtils;
import com.yff.common.utils.Query;
import com.yff.mall.product.dao.SkuSaleAttrValueDao;
import com.yff.mall.product.entity.SkuSaleAttrValueEntity;
import com.yff.mall.product.service.SkuSaleAttrValueService;
import com.yff.mall.product.vo.Attr;
import com.yff.mall.product.vo.SaleAttrVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity> implements SkuSaleAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuSaleAttrValueEntity> page = this.page(
                new Query<SkuSaleAttrValueEntity>().getPage(params),
                new QueryWrapper<SkuSaleAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public boolean saveSkuSaleAttrValueList(List<Attr> attr, Long skuId) {
        if(CollectionUtils.isEmpty(attr)){
            return false;
        }

        List<SkuSaleAttrValueEntity> skuSaleAttrValueList = attr.stream().map(data -> {
            SkuSaleAttrValueEntity skuSaleAttrValue = new SkuSaleAttrValueEntity();
            BeanUtils.copyProperties(data,skuSaleAttrValue);
            skuSaleAttrValue.setSkuId(skuId);
            return skuSaleAttrValue;
        }).collect(Collectors.toList());

        return this.saveBatch(skuSaleAttrValueList);
    }

    @Override
    public List<SaleAttrVo> getSaleAttrsBySpuId(Long spuId) {
        return this.baseMapper.getSaleAttrsBySpuId(spuId);
    }

    @Override
    public List<String> getSkuSaleAttrValues(Long skuId) {

        return baseMapper.getSkuSaleAttrValues(skuId);
    }

}
