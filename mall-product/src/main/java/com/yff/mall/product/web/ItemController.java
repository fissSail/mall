package com.yff.mall.product.web;

import com.yff.mall.product.service.SkuInfoService;
import com.yff.mall.product.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.concurrent.ExecutionException;

/**
 * @author yanfeifan
 * @Package com.yff.mall.product.web
 * @Description
 * @date 2022/1/6 10:48
 */
@Controller
public class ItemController {

    @Autowired
    private SkuInfoService skuInfoService;

    /**
     * 展示当前sku的请求
     *
     * @param
     * @return
     */
    @GetMapping("/{skuId}.html")
    public String item(@PathVariable("skuId")Long skuId, Model model) throws ExecutionException, InterruptedException {
        SkuItemVo vo = skuInfoService.item(skuId);
        model.addAttribute("item",vo);
        return "item";
    }

}
