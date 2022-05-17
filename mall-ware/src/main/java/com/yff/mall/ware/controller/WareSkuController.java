package com.yff.mall.ware.controller;

import com.yff.common.to.feign.WareSkuTo;
import com.yff.common.utils.PageUtils;
import com.yff.common.utils.R;
import com.yff.mall.ware.entity.WareSkuEntity;
import com.yff.mall.ware.service.WareSkuService;
import com.yff.mall.ware.vo.WareSkuLockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;



/**
 * 商品库存
 *
 * @author yff
 * @email 1335799488@qq.com
 * @date 2021-12-08 21:26:26
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareSkuService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WareSkuEntity wareSku){
		wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WareSkuEntity wareSku){
		wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    /**
     * 信息
     */
    @PostMapping("/hasStock")
    public R getSkuStock(@RequestBody List<Long> skuIds){
        List<WareSkuTo> voList = wareSkuService.getSkuStock(skuIds);
        return R.ok().setData(voList);
    }

    /**
     * 信息
     */
    @PostMapping("/order/lock")
    public R orderLock(@RequestBody WareSkuLockVo wareSkuLockVo){
        Boolean hasLock = null;
        try {
            hasLock = wareSkuService.orderLock(wareSkuLockVo);
            return R.ok();
        } catch (Exception e) {
            return R.error();
        }
    }

}
