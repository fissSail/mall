package com.yff.mall.product.feign.fallback;

import com.yff.common.utils.R;
import com.yff.mall.product.feign.SeckillFeignService;
import org.springframework.stereotype.Component;

/**
 * @author yanfeifan
 * @Package com.yff.mall.product.feign.fallback
 * @Description
 * @date 2022/2/13 10:47
 */
@Component
public class SeckillFeignFallback implements SeckillFeignService {
    @Override
    public R getSkuSeckillInfo(Long skuId) {
        return R.error();
    }
}
