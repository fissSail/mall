package com.yff.mall.order.feign;

import com.yff.common.utils.R;
import com.yff.mall.order.vo.WareSkuLockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author yanfeifan
 * @Package com.yff.mall.order.feign
 * @Description
 * @date 2022/2/4 18:00
 */
@FeignClient("mall-ware")
public interface WareFeignService {

    @PostMapping("/ware/waresku/hasStock")
    R getSkuStock(@RequestBody List<Long> skuIds);

    @GetMapping("/ware/wareinfo/freight/{addrId}")
    R getFreight(@PathVariable("addrId")Long addrId);

    @PostMapping("/ware/waresku/order/lock")
    R orderLock(@RequestBody WareSkuLockVo wareSkuLockVo);
}
