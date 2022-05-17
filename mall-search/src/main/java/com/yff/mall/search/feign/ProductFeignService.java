package com.yff.mall.search.feign;

import com.yff.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author yanfeifan
 * @Package com.yff.mall.search.fegin
 * @Description
 * @date 2022/1/17 16:27
 */
@FeignClient("mall-product")
public interface ProductFeignService {

    /**
     * 查询一级商品分类
     * @param i
     * @return
     */
    @GetMapping("/product/category/getCategoryStair")
    R getCategoryStair(@RequestParam Integer i);

    /**
     * 查询三级分类导航
     * @return
     */
    @GetMapping("/index/categoryJson")
    R getCategoryJson();

    /**
     * 根据属性id查询
     * @param attrId
     * @return
     */
    @GetMapping("/product/attr/info/{attrId}")
    R attrInfo(@PathVariable("attrId") Long attrId);

    /**
     * 根据品牌id查询
     * @param brandIds
     * @return
     */
    @GetMapping("/product/brand/getByBrandIds")
    R getByBrandIds(@RequestParam("brandIds") List<Long> brandIds);

}
