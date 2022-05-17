package com.yff.mall.product.admin;

import com.yff.common.utils.PageUtils;
import com.yff.common.utils.R;
import com.yff.mall.product.entity.AttrAttrgroupRelationEntity;
import com.yff.mall.product.service.AttrService;
import com.yff.mall.product.vo.AttrRespVo;
import com.yff.mall.product.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;



/**
 * 商品属性
 *
 * @author yff
 * @email 1335799488@qq.com
 * @date 2021-12-07 21:55:52
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/{attrType}/list/{catId}")
    public R listByAttrType(@RequestParam Map<String, Object> params
            ,@PathVariable("catId") Long catId
            ,@PathVariable("attrType") String attrType){
        PageUtils page = attrService.queryAttrTypePage(params,catId,attrType);
        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    public R info(@PathVariable("attrId") Long attrId){
        AttrRespVo respVo = attrService.getAllById(attrId);

        return R.ok().put("attr", respVo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrVo vo){
		attrService.saveAttr(vo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrVo vo){
		attrService.updateAttrById(vo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody List<AttrAttrgroupRelationEntity> voList){
		attrService.removeRelevanceByIds(voList);

        return R.ok();
    }

}
