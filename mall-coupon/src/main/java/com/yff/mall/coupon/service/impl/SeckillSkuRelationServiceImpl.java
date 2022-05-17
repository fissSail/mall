package com.yff.mall.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yff.common.utils.PageUtils;
import com.yff.common.utils.Query;
import com.yff.mall.coupon.dao.SeckillSkuRelationDao;
import com.yff.mall.coupon.entity.SeckillSkuRelationEntity;
import com.yff.mall.coupon.service.SeckillSkuRelationService;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Map;


@Service("seckillSkuRelationService")
public class SeckillSkuRelationServiceImpl extends ServiceImpl<SeckillSkuRelationDao, SeckillSkuRelationEntity> implements SeckillSkuRelationService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String promotionSessionId = (String) params.get("promotionSessionId");
        IPage<SeckillSkuRelationEntity> page = this.page(
                new Query<SeckillSkuRelationEntity>().getPage(params),
                new QueryWrapper<SeckillSkuRelationEntity>()
                        .eq(!ObjectUtils.isEmpty(promotionSessionId), "promotion_session_id", promotionSessionId)
        );

        return new PageUtils(page);
    }

}
