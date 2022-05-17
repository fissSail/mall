package com.yff.mall.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yff.common.to.SpuBoundsTo;
import com.yff.common.utils.PageUtils;
import com.yff.common.utils.Query;
import com.yff.mall.coupon.dao.SpuBoundsDao;
import com.yff.mall.coupon.entity.SpuBoundsEntity;
import com.yff.mall.coupon.service.SpuBoundsService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("spuBoundsService")
public class SpuBoundsServiceImpl extends ServiceImpl<SpuBoundsDao, SpuBoundsEntity> implements SpuBoundsService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuBoundsEntity> page = this.page(
                new Query<SpuBoundsEntity>().getPage(params),
                new QueryWrapper<SpuBoundsEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveBounds(SpuBoundsTo to) {
        SpuBoundsEntity spuBounds = new SpuBoundsEntity();
        BeanUtils.copyProperties(to,spuBounds);
        this.save(spuBounds);
    }

}
