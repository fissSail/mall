package com.yff.mall.order.vo;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.yff.common.to.feign.MemberAddressTo;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author yanfeifan
 * @Package com.yff.mall.order.vo
 * @Description 订单确认页数据
 * @date 2022/2/4 11:08
 */
public class OrderConfirmVo {

    //收货地址
    @Getter
    @Setter
    private List<MemberAddressTo> addressVos;

    //选中的购物项
    @Getter
    @Setter
    private List<OrderItemVo> orderItemVos;

    //积分
    @Getter
    @Setter
    private Integer integration;

    //订单总额
    private BigDecimal total;

    //应付总额
    private BigDecimal payPrice;

    @Getter
    @Setter
    private Map<Long, Boolean> hasStock;

    //防止重复提交token
    @Getter
    @Setter
    private String token;

    private Integer count;

    public Integer getCount() {
        Integer sum = 0;
        if(CollectionUtils.isNotEmpty(orderItemVos)){
            for (OrderItemVo orderItemVo : orderItemVos) {
                sum += orderItemVo.getCount();
            }
        }
        return sum;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public BigDecimal getTotal() {
        BigDecimal sum = new BigDecimal("0");
        if(CollectionUtils.isNotEmpty(orderItemVos)){
            for (OrderItemVo orderItemVo : orderItemVos) {
                sum = sum.add(orderItemVo.getTotalPrice());
            }
        }
        return sum;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getPayPrice() {
        return this.getTotal();
    }

    public void setPayPrice(BigDecimal payPrice) {
        this.payPrice = payPrice;
    }
}
