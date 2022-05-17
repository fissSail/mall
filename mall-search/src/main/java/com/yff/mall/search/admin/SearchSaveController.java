package com.yff.mall.search.admin;

import com.yff.common.to.es.SkuEsTo;
import com.yff.common.utils.ExceptionCodeEnum;
import com.yff.common.utils.R;
import com.yff.mall.search.service.SearchSaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * @author yanfeifan
 * @Package com.yff.mall.search.controller
 * @Description
 * @date 2022/1/4 16:28
 */
@RestController
@RequestMapping("search/save")
public class SearchSaveController {

    @Autowired
    private SearchSaveService searchSaveService;


    @PostMapping("/product")
    public R saveProduct(@RequestBody List<SkuEsTo> list){
        boolean b = false;
        try {
            b = searchSaveService.saveProduct(list);
        } catch (IOException e) {
            e.printStackTrace();
            return R.error(ExceptionCodeEnum.PRODUCT_EXCEPTION.getCode(), ExceptionCodeEnum.PRODUCT_EXCEPTION.getMsg());
        }

        if(b){
            return R.ok();
        }else{
            return R.error(ExceptionCodeEnum.PRODUCT_EXCEPTION.getCode(), ExceptionCodeEnum.PRODUCT_EXCEPTION.getMsg());
        }
    }
}
