package com.yff.mall.member.web;

import com.alibaba.fastjson.TypeReference;
import com.yff.common.utils.PageUtils;
import com.yff.common.utils.R;
import com.yff.mall.member.feign.OrderFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yanfeifan
 * @Package com.yff.mall.member.web
 * @Description
 * @date 2022/2/7 16:17
 */
@Controller
public class MemberWebController {

    @Autowired
    private OrderFeignService orderFeignService;

    @GetMapping("/memberOrderList.html")
    public String memberOrderList(@RequestParam(value = "pageNum", defaultValue = "1") String pageNum, Model model) {

        //获取到支付宝给我传来的所有请求数据，验证签名
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("page", pageNum);
        R r = orderFeignService.listWithItem(map);
        if (r.getCode() == 0) {
            PageUtils page = r.getDataByKey("page", new TypeReference<PageUtils>() {});
            model.addAttribute("list", page);
        }

        return "orderform";
    }
}
