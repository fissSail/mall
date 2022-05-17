package com.yff.mall.ware.controller;

import com.yff.common.utils.PageUtils;
import com.yff.common.utils.R;
import com.yff.mall.ware.entity.WareInfoEntity;
import com.yff.mall.ware.service.WareInfoService;
import com.yff.mall.ware.vo.FreightVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;



/**
 * 仓库信息
 *
 * @author yff
 * @email 1335799488@qq.com
 * @date 2021-12-08 21:26:25
 */
@RestController
@RequestMapping("ware/wareinfo")
public class WareInfoController {
    @Autowired
    private WareInfoService wareInfoService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareInfoService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		WareInfoEntity wareInfo = wareInfoService.getById(id);

        return R.ok().put("wareInfo", wareInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WareInfoEntity wareInfo){
		wareInfoService.save(wareInfo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WareInfoEntity wareInfo){
		wareInfoService.updateById(wareInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		wareInfoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    @GetMapping("/freight/{addrId}")
    public R getFreight(@PathVariable("addrId")Long addrId){
        FreightVo vo = wareInfoService.getFreight(addrId);
        return R.ok().setData(vo);
    }

}
