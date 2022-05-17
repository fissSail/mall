package com.yff.mall.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yff.common.to.MemberPrice;
import com.yff.common.to.SkuReductionTo;
import com.yff.common.utils.PageUtils;
import com.yff.common.utils.Query;
import com.yff.mall.coupon.dao.SkuFullReductionDao;
import com.yff.mall.coupon.entity.MemberPriceEntity;
import com.yff.mall.coupon.entity.SkuFullReductionEntity;
import com.yff.mall.coupon.entity.SkuLadderEntity;
import com.yff.mall.coupon.service.MemberPriceService;
import com.yff.mall.coupon.service.SkuFullReductionService;
import com.yff.mall.coupon.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Autowired
    private SkuLadderService skuLadderService;

    @Autowired
    private MemberPriceService memberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuReduction(SkuReductionTo to) {
        //保存sku优惠，满减信息sms_sku_ladder,sms_sku_full_reduction,sms_member_price
        SkuLadderEntity skuLadder = new SkuLadderEntity();
        skuLadder.setSkuId(to.getSkuId());
        skuLadder.setFullCount(to.getFullCount());
        skuLadder.setDiscount(to.getDiscount());
        skuLadder.setAddOther(to.getCountStatus());
        if(to.getFullCount() > 0){
            skuLadderService.save(skuLadder);
        }

        SkuFullReductionEntity skuFullReduction = new SkuFullReductionEntity();
        BeanUtils.copyProperties(to,skuFullReduction);
        skuFullReduction.setAddOther(to.getPriceStatus());
        if(to.getFullPrice().compareTo(new BigDecimal(0)) == 1){
            this.save(skuFullReduction);
        }

        List<MemberPrice> memberPrices = to.getMemberPrice();

        if(CollectionUtils.isNotEmpty(memberPrices)){
            List<MemberPriceEntity> memberPriceEntityList = memberPrices
                    .stream()
                    .filter(data->data.getPrice().compareTo(new BigDecimal(0)) == 1)
                    .map(data->{
                        MemberPriceEntity memberPrice = new MemberPriceEntity();
                        memberPrice.setSkuId(to.getSkuId());
                        memberPrice.setMemberPrice(data.getPrice());
                        memberPrice.setMemberLevelId(data.getId());
                        memberPrice.setMemberLevelName(data.getName());
                        memberPrice.setAddOther(1);
                        return memberPrice;
                    }).collect(Collectors.toList());

            memberPriceService.saveBatch(memberPriceEntityList);
        }
    }

}
