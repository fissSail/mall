package com.yff.mall.mallseckill.controller;

import com.yff.common.to.feign.SeckillSkuRedisTo;
import com.yff.common.utils.R;
import com.yff.mall.mallseckill.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author yanfeifan
 * @Package com.yff.mall.mallseckill.controller
 * @Description
 * @date 2022/2/11 17:07
 */
@Controller
public class SeckillController {

    @Autowired
    private SeckillService seckillService;

    @GetMapping("/currentSeckillSkus")
    @ResponseBody
    public R getCurrentSeckillSkus(){
        List<SeckillSkuRedisTo> list = seckillService.getCurrentSeckillSkus();
        return R.ok().setData(list);
    }

    @GetMapping("/sku/seckill/{skuId}")
    @ResponseBody
    public R getSkuSeckillInfo(@PathVariable("skuId")Long skuId){
        SeckillSkuRedisTo seckillSkuRedisTo = seckillService.getSkuSeckillInfo(skuId);
        return R.ok().setData(seckillSkuRedisTo);
    }

    @GetMapping("/seckill")
    public String seckill(@RequestParam("seckillId")String seckillId,
                          @RequestParam("token")String token,
                          @RequestParam("count")Integer count, Model model){
        String orderSn = seckillService.seckill(seckillId,token,count);
        model.addAttribute("orderSn",orderSn);
        return "success";
    }
}
