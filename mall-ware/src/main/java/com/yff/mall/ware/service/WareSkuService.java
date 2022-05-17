package com.yff.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yff.common.to.feign.OrderTo;
import com.yff.common.to.feign.WareSkuTo;
import com.yff.common.to.mq.StockLockedTo;
import com.yff.common.utils.PageUtils;
import com.yff.mall.ware.entity.WareSkuEntity;
import com.yff.mall.ware.vo.PurchaseItemDoneVo;
import com.yff.mall.ware.vo.WareSkuLockVo;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author yff
 * @email 1335799488@qq.com
 * @date 2021-12-08 21:26:26
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveOrUpdateBatchBySkuId(PurchaseItemDoneVo vo);

    List<WareSkuTo> getSkuStock(List<Long> skuIds);

    Boolean orderLock(WareSkuLockVo wareSkuLockVo);

    void stockLockedRelease(StockLockedTo to) throws IOException;

    void stockLockedRelease(OrderTo to) throws IOException;
}

