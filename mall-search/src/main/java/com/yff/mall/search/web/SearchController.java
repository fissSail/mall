package com.yff.mall.search.web;

import com.alibaba.fastjson.TypeReference;
import com.yff.common.to.feign.CategoryRespTo;
import com.yff.common.to.feign.CategoryTo;
import com.yff.common.utils.R;
import com.yff.mall.search.feign.ProductFeignService;
import com.yff.mall.search.service.MallSearchService;
import com.yff.mall.search.vo.SearchParamVo;
import com.yff.mall.search.vo.SearchRespVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author yanfeifan
 * @Package com.yff.mall.search.web
 * @Description
 * @date 2022/1/17 16:25
 */
@Controller
public class SearchController {

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private MallSearchService mallSearchService;

    @GetMapping({"/","search.html"})
    public String searchIndex(Model model, SearchParamVo vo, HttpServletRequest request){
        vo.set_queryString(request.getQueryString());

        R r = productFeignService.getCategoryStair(1);
        List<CategoryTo> data = r.getData(new TypeReference<List<CategoryTo>>() {});

        SearchRespVo searchRespVo = mallSearchService.search(vo);

        model.addAttribute("categories", data);
        model.addAttribute("result", searchRespVo);
        return "index";
    }

    /**
     * 3级分类
     *
     * @return
     */
    @GetMapping("index/categoryJson")
    @ResponseBody
    public R getCategoryJson() {
        R r = productFeignService.getCategoryJson();
        Map<Long, List<CategoryRespTo>> map = r.getData(new TypeReference<Map<Long, List<CategoryRespTo>>>() {});

        return R.ok().put("data", map);
    }
}
