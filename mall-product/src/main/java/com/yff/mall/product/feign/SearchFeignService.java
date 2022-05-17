package com.yff.mall.product.feign;

import com.yff.common.to.es.SkuEsTo;
import com.yff.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author yanfeifan
 * @Package com.yff.mall.product.feign
 * @Description
 * @date 2022/1/4 16:31
 */
@FeignClient("mall-search")
public interface SearchFeignService {

    @PostMapping("/search/save/product")
    R saveProduct(@RequestBody List<SkuEsTo> list);
}
