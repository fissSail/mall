package com.yff.mall.product.admin;

import com.yff.common.utils.PageUtils;
import com.yff.common.utils.R;
import com.yff.mall.product.entity.AttrGroupEntity;
import com.yff.mall.product.service.AttrGroupService;
import com.yff.mall.product.service.AttrService;
import com.yff.mall.product.vo.AttrGroupRespVo;
import com.yff.mall.product.vo.AttrGroupVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;



/**
 * 属性分组
 *
 * @author yff
 * @email 1335799488@qq.com
 * @date 2021-12-07 21:55:53
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private AttrService attrService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrGroupService.queryPage(params);

        return R.ok().put("page", page);
    }



    /**
     * 列表
     */
    @RequestMapping("/list/{catId}")
    public R list(@RequestParam Map<String, Object> params,@PathVariable("catId") Long catId){
        PageUtils page = attrGroupService.queryPage(params,catId);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
        AttrGroupVo attrGroup = attrGroupService.getAllById(attrGroupId);
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

    /**
     * 列表
     */
    @RequestMapping("/attrattrgrouprelation/list/relevance/{attrGroupId}")
    public R queryRelevanceAttrRelationPage(@RequestParam Map<String, Object> params,@PathVariable("attrGroupId")Long attrGroupId){
        PageUtils page = attrService.queryRelevanceAttrRelationPage(params,attrGroupId);

        return R.ok().put("page", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/attrattrgrouprelation/list/irrelevant/{attrGroupId}")
    public R queryIrrelevantAttrRelationPage(@RequestParam Map<String, Object> params,@PathVariable("attrGroupId")Long attrGroupId){
        PageUtils page = attrService.queryIrrelevantAttrRelationPage(params,attrGroupId);

        return R.ok().put("page", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/{catalogId}/withattr")
    public R withattr(@PathVariable("catalogId") Long catalogId){
        List<AttrGroupRespVo> vo = attrGroupService.queryAttrGroupWithAttrsByCatalogId(catalogId);

        return R.ok().put("data", vo);
    }

}
