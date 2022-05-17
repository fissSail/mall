package com.yff.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yff.common.utils.PageUtils;
import com.yff.mall.ware.entity.PurchaseEntity;
import com.yff.mall.ware.vo.PurchaseDoneVo;
import com.yff.mall.ware.vo.PurchaseMergeVo;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author yff
 * @email 1335799488@qq.com
 * @date 2021-12-08 21:26:25
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageByUnreceive(Map<String, Object> params);

    void merge(PurchaseMergeVo vo);

    void received(List<Long> asList);

    void done(PurchaseDoneVo vo);
}

