package com.yff.mall.ware.controller;

import com.yff.common.utils.PageUtils;
import com.yff.common.utils.R;
import com.yff.mall.ware.entity.PurchaseEntity;
import com.yff.mall.ware.service.PurchaseService;
import com.yff.mall.ware.vo.PurchaseDoneVo;
import com.yff.mall.ware.vo.PurchaseMergeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;


/**
 * 采购信息
 *
 * @author yff
 * @email 1335799488@qq.com
 * @date 2021-12-08 21:26:25
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = purchaseService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/unreceive/list")
    public R unreceiveList(@RequestParam Map<String, Object> params) {
        PageUtils page = purchaseService.queryPageByUnreceive(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        PurchaseEntity purchase = purchaseService.getById(id);

        return R.ok().put("purchase", purchase);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody PurchaseEntity purchase) {
        purchase.setCreateTime(new Date());
        purchase.setUpdateTime(new Date());
        purchaseService.save(purchase);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody PurchaseEntity purchase) {
        purchaseService.updateById(purchase);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        purchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    /**
     * 合并
     */
    @PostMapping("/merge")
    public R merge(@RequestBody PurchaseMergeVo vo) {
        purchaseService.merge(vo);

        return R.ok();
    }

    /**
     * 领取
     */
    @PostMapping("/received")
    public R received(@RequestBody Long[] purchaseIds) {
        purchaseService.received(Arrays.asList(purchaseIds));

        return R.ok();
    }

    /**
     * 完成
     */
    @PostMapping("/done")
    public R done(@RequestBody PurchaseDoneVo vo) {
        purchaseService.done(vo);

        return R.ok();
    }

}
