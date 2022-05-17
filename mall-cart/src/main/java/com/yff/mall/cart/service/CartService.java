package com.yff.mall.cart.service;

import com.yff.mall.cart.vo.CartItemVo;
import com.yff.mall.cart.vo.CartVo;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author yanfeifan
 * @Package com.yff.mall.cart.service
 * @Description
 * @date 2022/1/30 15:41
 */

public interface CartService {

    CartItemVo addToCart(Long skuId, Integer count) throws ExecutionException, InterruptedException;

    CartItemVo getCartItem(Long skuId);

    CartVo getCartList() throws ExecutionException, InterruptedException;

    void clearCart(String cartKey);

    void checkCart(Boolean check, Long skuId);

    void countCart(Integer count, Long skuId);

    void deleteCart(Long skuId);

    List<CartItemVo> getCartByMemberId();

}
