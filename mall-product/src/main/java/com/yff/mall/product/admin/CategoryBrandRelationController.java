package com.yff.mall.product.admin;

import com.yff.common.utils.PageUtils;
import com.yff.common.utils.R;
import com.yff.mall.product.entity.BrandEntity;
import com.yff.mall.product.entity.CategoryBrandRelationEntity;
import com.yff.mall.product.service.CategoryBrandRelationService;
import com.yff.mall.product.vo.BrandRespVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 品牌分类关联
 *
 * @author yff
 * @email 1335799488@qq.com
 * @date 2021-12-07 21:55:53
 */
@RestController
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = categoryBrandRelationService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/catelog/list")
    public R catelogList(@RequestParam Map<String, Object> params){
        PageUtils page = categoryBrandRelationService.queryCategoryBrandRelationPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/brands/list")
    public R catelogList(@RequestParam(value = "catId",required = true) Long catId){
        List<BrandEntity> list = categoryBrandRelationService.queryBrandListByCatId(catId);

        List<BrandRespVo> respVoList = list.stream().map(data -> {
            BrandRespVo vo = new BrandRespVo();
            vo.setBrandId(data.getBrandId());
            vo.setBrandName(data.getName());
            return vo;
        }).collect(Collectors.toList());

        return R.ok().put("data", respVoList);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.saveAndName(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.updateById(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		categoryBrandRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
