package com.yff.mall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yff.common.constant.WareConstant;
import com.yff.common.utils.PageUtils;
import com.yff.common.utils.Query;
import com.yff.mall.ware.dao.PurchaseDao;
import com.yff.mall.ware.entity.PurchaseDetailEntity;
import com.yff.mall.ware.entity.PurchaseEntity;
import com.yff.mall.ware.service.PurchaseDetailService;
import com.yff.mall.ware.service.PurchaseService;
import com.yff.mall.ware.service.WareSkuService;
import com.yff.mall.ware.vo.PurchaseDoneVo;
import com.yff.mall.ware.vo.PurchaseItemDoneVo;
import com.yff.mall.ware.vo.PurchaseMergeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    private PurchaseDetailService purchaseDetailService;

    @Autowired
    private WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageByUnreceive(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
                        .eq("status", WareConstant.PurchaseStatusEnum.PURCHASE_NEW.getCode())
                        .or()
                        .eq("status", WareConstant.PurchaseStatusEnum.PURCHASE_ALLOCATED.getCode())
        );

        return new PageUtils(page);

    }

    @Override
    public void merge(PurchaseMergeVo vo) {
        Long purchaseId = vo.getPurchaseId();
        List<Long> items = vo.getItems();
        if (Objects.isNull(purchaseId) && CollectionUtils.isNotEmpty(items)) {
            //新增采购单
            PurchaseEntity purchase = new PurchaseEntity();
            purchase.setCreateTime(new Date());
            purchase.setUpdateTime(new Date());
            purchase.setStatus(WareConstant.PurchaseStatusEnum.PURCHASE_NEW.getCode());
            this.save(purchase);
            purchaseId = purchase.getId();
        }

        Long finalPurchaseId = purchaseId;
        List<PurchaseDetailEntity> purchaseDetailEntityList = items
                .stream()
                .filter(i -> {
                    PurchaseDetailEntity purchaseDetail = purchaseDetailService.getById(i);

                    return purchaseDetail.getStatus() == WareConstant.PurchaseDetailStatusEnum.PURCHASE_NEW.getCode() ||
                            purchaseDetail.getStatus() == WareConstant.PurchaseDetailStatusEnum.PURCHASE_ALLOCATED.getCode();
                })
                .map(data -> {
                    PurchaseDetailEntity purchaseDetail = new PurchaseDetailEntity();
                    purchaseDetail.setId(data);
                    purchaseDetail.setPurchaseId(finalPurchaseId);
                    purchaseDetail.setStatus(WareConstant.PurchaseDetailStatusEnum.PURCHASE_ALLOCATED.getCode());
                    return purchaseDetail;
                }).collect(Collectors.toList());

        purchaseDetailService.updateBatchById(purchaseDetailEntityList);

        PurchaseEntity purchase = new PurchaseEntity();
        purchase.setId(purchaseId);
        purchase.setUpdateTime(new Date());
        this.updateById(purchase);
    }

    @Override
    @Transactional
    public void received(List<Long> asList) {

        List<PurchaseEntity> purchaseEntityList = asList
                .stream()
                .filter(id -> {
                    PurchaseEntity purchase = this.getById(id);

                    return purchase.getStatus() == WareConstant.PurchaseStatusEnum.PURCHASE_NEW.getCode() ||
                            purchase.getStatus() == WareConstant.PurchaseStatusEnum.PURCHASE_ALLOCATED.getCode();
                })
                .map(id -> {
                    PurchaseEntity purchase = new PurchaseEntity();
                    purchase.setId(id);
                    purchase.setStatus(WareConstant.PurchaseStatusEnum.PURCHASE_ALREADY_RECEIVED.getCode());
                    purchase.setUpdateTime(new Date());

                    //修改采购需求状态
                    PurchaseDetailEntity purchaseDetail = new PurchaseDetailEntity();
                    purchaseDetail.setStatus(WareConstant.PurchaseDetailStatusEnum.PURCHASE_BEING.getCode());
                    purchaseDetailService.update(purchaseDetail, new QueryWrapper<PurchaseDetailEntity>().eq("purchase_id", id));

                    return purchase;
                }).collect(Collectors.toList());

        this.updateBatchById(purchaseEntityList);
    }

    @Override
    @Transactional
    public void done(PurchaseDoneVo vo) {
        Long purchaseId = vo.getPurchaseId();
        List<PurchaseItemDoneVo> itemVos = vo.getItemVos();

        //查询已已完成的采购项
        List<PurchaseItemDoneVo> itemIdList = itemVos
                .stream()
                .filter(data ->
                        data.getStatus() == WareConstant.PurchaseDetailStatusEnum.PURCHASE_ACCOMPLISH.getCode())
                .map(data->{
                    wareSkuService.saveOrUpdateBatchBySkuId(data);
                    return data;
                })
                .collect(Collectors.toList());

        //改变采购单状态
        PurchaseEntity purchase = new PurchaseEntity();
        purchase.setUpdateTime(new Date());
        purchase.setStatus(itemIdList.size() != itemVos.size() ?
                WareConstant.PurchaseStatusEnum.PURCHASE_EXCEPTION.getCode() :
                WareConstant.PurchaseStatusEnum.PURCHASE_ACCOMPLISH.getCode());
        purchase.setId(purchaseId);
        this.updateById(purchase);

        //改变采购项状态
        if (CollectionUtils.isNotEmpty(itemVos)) {
            List<PurchaseDetailEntity> purchaseDetailEntityList = itemVos.stream().map(data -> {
                PurchaseDetailEntity purchaseDetail = new PurchaseDetailEntity();
                purchaseDetail.setId(data.getItemId());
                purchaseDetail.setStatus(data.getStatus());
                return purchaseDetail;
            }).collect(Collectors.toList());

            purchaseDetailService.updateBatchById(purchaseDetailEntityList);
        }
    }

}
