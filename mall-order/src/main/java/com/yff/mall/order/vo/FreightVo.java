package com.yff.mall.order.vo;

import com.yff.common.to.feign.MemberAddressTo;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author yanfeifan
 * @Package com.yff.mall.ware.vo
 * @Description
 * @date 2022/2/4 20:59
 */
@Data
public class FreightVo {
    private MemberAddressTo memberAddressTo;

    private BigDecimal freight;
}
