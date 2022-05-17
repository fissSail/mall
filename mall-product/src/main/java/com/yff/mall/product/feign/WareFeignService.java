package com.yff.mall.product.feign;

import com.yff.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author yanfeifan
 * @Package com.yff.mall.product.feign
 * @Description
 * @date 2022/1/4 15:51
 */
@FeignClient("mall-ware")
public interface WareFeignService {

    @PostMapping("/ware/waresku/hasStock")
    R getSkuStock(@RequestBody List<Long> skuIds);
}
