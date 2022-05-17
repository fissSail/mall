package com.yff.mall.cart.vo;

import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author yanfeifan
 * @Package com.yff.mall.cart.vo
 * @Description
 * @date 2022/1/30 15:29
 */
public class CartVo {

    private List<CartItemVo> cartItemVos;

    //商品总数量
    private Integer countNum;

    //商品类型总数量
    private Integer countType;

    //商品总价
    private BigDecimal totalAmount;

    //减免价格
    private BigDecimal reduce = new BigDecimal("0.00");

    public List<CartItemVo> getCartItemVos() {
        return cartItemVos;
    }

    public void setCartItemVos(List<CartItemVo> cartItemVos) {
        this.cartItemVos = cartItemVos;
    }

    public Integer getCountNum() {
        int count = 0;
        if (!CollectionUtils.isEmpty(cartItemVos)) {
            for (CartItemVo cartItemVo : cartItemVos) {
                count += cartItemVo.getCount();
            }
        }
        return count;
    }

    public Integer getCountType() {
        return cartItemVos.size();
    }

    public BigDecimal getTotalAmount() {
        BigDecimal amount = new BigDecimal("0");

        if (!CollectionUtils.isEmpty(cartItemVos)) {
            for (CartItemVo cartItemVo : cartItemVos) {
                if(cartItemVo.getCheck()){
                    amount = cartItemVo.getTotalPrice().add(amount);
                }
            }
        }

        amount = amount.subtract(reduce);

        return amount;
    }

    public BigDecimal getReduce() {
        return reduce;
    }

    public void setReduce(BigDecimal reduce) {
        this.reduce = reduce;
    }
}
