package com.yff.mall.product.admin;

import com.yff.common.utils.PageUtils;
import com.yff.common.utils.R;
import com.yff.mall.product.entity.ProductAttrValueEntity;
import com.yff.mall.product.service.ProductAttrValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;



/**
 * spu属性值
 *
 * @author yff
 * @email 1335799488@qq.com
 * @date 2021-12-07 21:55:53
 */
@RestController
@RequestMapping("product/productattrvalue")
public class ProductAttrValueController {
    @Autowired
    private ProductAttrValueService productAttrValueService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = productAttrValueService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/base/listforspu/{spuId}")
    public R listforspu(@PathVariable("spuId") Long spuId){
        List<ProductAttrValueEntity> list = productAttrValueService.queryProductAttrValueBySpuId(spuId);
        return R.ok().put("data", list);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		ProductAttrValueEntity productAttrValue = productAttrValueService.getById(id);

        return R.ok().put("productAttrValue", productAttrValue);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody ProductAttrValueEntity productAttrValue){
		productAttrValueService.save(productAttrValue);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody ProductAttrValueEntity productAttrValue){
		productAttrValueService.updateById(productAttrValue);

        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update/{spuId}")
    public R update(@RequestBody List<ProductAttrValueEntity> productAttrValues,@PathVariable("spuId")Long spuId){
		productAttrValueService.update(productAttrValues,spuId);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		productAttrValueService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
