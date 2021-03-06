package com.yff.mall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.yff.common.to.feign.SkuInfoFeignTo;
import com.yff.common.utils.R;
import com.yff.mall.cart.feign.ProductFeignService;
import com.yff.mall.cart.interceptor.CartInterceptor;
import com.yff.mall.cart.service.CartService;
import com.yff.mall.cart.vo.CartItemVo;
import com.yff.mall.cart.vo.CartVo;
import com.yff.mall.cart.vo.UserInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @author yanfeifan
 * @Package com.yff.mall.cart.service.impl
 * @Description
 * @date 2022/1/30 15:42
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    private final String CART_PREFIX = "guliamll:cart:";

    @Override
    public CartItemVo addToCart(Long skuId, Integer count) throws ExecutionException, InterruptedException {
        BoundHashOperations<String, Object, Object> cartOps = this.getCartOps();
        String get = (String) cartOps.get(skuId.toString());

        if (StringUtils.hasText(get)) {
            CartItemVo cartItemVo = new CartItemVo();
            cartItemVo = JSON.parseObject(get, CartItemVo.class);
            cartItemVo.setCount(cartItemVo.getCount() + count);
            cartOps.put(skuId.toString(), JSON.toJSONString(cartItemVo));
            return cartItemVo;
        } else {
            //???????????????
            //??????????????????????????? ????????????sku??????
            CartItemVo cartItemVo = new CartItemVo();
            CompletableFuture<Void> getSkuInfo = CompletableFuture.runAsync(() -> {
                R r = productFeignService.getSkuinfoBySkuId(skuId);
                if (r.getCode() == 0) {
                    SkuInfoFeignTo skuInfo = r.getDataByKey("skuInfo", new TypeReference<SkuInfoFeignTo>() {
                    });
                    cartItemVo.setImage(skuInfo.getSkuDefaultImg());
                    cartItemVo.setPrice(skuInfo.getPrice());
                    cartItemVo.setSkuId(skuId);
                    cartItemVo.setCount(count);
                    cartItemVo.setTitle(skuInfo.getSkuTitle());
                }
            }, threadPoolExecutor);

            CompletableFuture<Void> getAttrValues = CompletableFuture.runAsync(() -> {
                R r = productFeignService.getSkuSaleAttrValues(skuId);
                if (r.getCode() == 0) {
                    cartItemVo.setSkuAttr(r.getData(new TypeReference<List<String>>() {
                    }));
                }
            }, threadPoolExecutor);

            CompletableFuture.allOf(getSkuInfo, getAttrValues).get();

            cartOps.put(skuId.toString(), JSON.toJSONString(cartItemVo));
            return cartItemVo;
        }
    }

    @Override
    public CartItemVo getCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = this.getCartOps();
        String json = (String) cartOps.get(skuId.toString());
        CartItemVo cartItemVo = JSON.parseObject(json, CartItemVo.class);
        return cartItemVo;
    }

    @Override
    public CartVo getCartList() throws ExecutionException, InterruptedException {
        CartVo cartVo = new CartVo();
        //??????ThreadLocal??????????????????
        UserInfoVo userInfoVo = CartInterceptor.threadLocal.get();
        if (userInfoVo.getUserId() != null) {
            //??????????????????????????????????????????
            String cartUserIdKey = CART_PREFIX + userInfoVo.getUserId();
            String cartUserKey = CART_PREFIX + userInfoVo.getUserKey();
            //??????????????????????????????
            List<CartItemVo> cartItemVosByUserKey = this.getCartItemVos(cartUserKey);

            if (CollectionUtils.isNotEmpty(cartItemVosByUserKey)) {
                //????????????????????????????????????????????????
                for (CartItemVo cartItemVo : cartItemVosByUserKey) {
                    //???????????????????????????????????????
                    this.addToCart(cartItemVo.getSkuId(), cartItemVo.getCount());
                }

                this.clearCart(cartUserKey);
            }

            //?????????????????????
            List<CartItemVo> cartItemVosByUserId = this.getCartItemVos(cartUserIdKey);
            cartVo.setCartItemVos(cartItemVosByUserId);
        } else {
            //?????????
            String cartKey = CART_PREFIX + userInfoVo.getUserKey();
            List<CartItemVo> cartItemVos = this.getCartItemVos(cartKey);
            cartVo.setCartItemVos(cartItemVos);
        }
        return cartVo;
    }

    /**
     * ???????????????????????????
     *
     * @return
     */
    private BoundHashOperations<String, Object, Object> getCartOps() {
        //??????ThreadLocal??????????????????
        UserInfoVo userInfoVo = CartInterceptor.threadLocal.get();
        //??????redis???key
        String cartKey = "";
        if (userInfoVo.getUserId() != null) {
            cartKey = CART_PREFIX + userInfoVo.getUserId();
        } else {
            cartKey = CART_PREFIX + userInfoVo.getUserKey();
        }
        //??????hash??????
        BoundHashOperations<String, Object, Object> stringObjectObjectBoundHashOperations = stringRedisTemplate.boundHashOps(cartKey);
        return stringObjectObjectBoundHashOperations;
    }


    private List<CartItemVo> getCartItemVos(String cartKey) {
        List<CartItemVo> cartItemVoList = new ArrayList<>();
        //??????hash??????
        BoundHashOperations<String, Object, Object> boundHashOps = stringRedisTemplate.boundHashOps(cartKey);
        List<Object> values = boundHashOps.values();
        if (CollectionUtils.isNotEmpty(values)) {
            cartItemVoList = values.stream().map(item -> {
                CartItemVo cartItemVo = JSON.parseObject((String) item, CartItemVo.class);
                return cartItemVo;
            }).collect(Collectors.toList());
        }
        return cartItemVoList;
    }

    @Override
    public void clearCart(String cartKey) {
        stringRedisTemplate.delete(cartKey);
    }

    @Override
    public void checkCart(Boolean check, Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = this.getCartOps();
        CartItemVo cartItem = this.getCartItem(skuId);
        cartItem.setCheck(check);
        cartOps.put(skuId.toString(), JSON.toJSONString(cartItem));
    }

    @Override
    public void countCart(Integer count, Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = this.getCartOps();
        CartItemVo cartItem = this.getCartItem(skuId);
        cartItem.setCount(cartItem.getCount() + count);
        cartOps.put(skuId.toString(), JSON.toJSONString(cartItem));
    }

    @Override
    public void deleteCart(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = this.getCartOps();
        cartOps.delete(skuId.toString());
    }

    @Override
    public List<CartItemVo> getCartByMemberId() {
        UserInfoVo userInfoVo = CartInterceptor.threadLocal.get();

        if (userInfoVo.getUserId() == null) {
            return null;
        }

        String cartKey = CART_PREFIX + userInfoVo.getUserId();
        List<CartItemVo> cartItemVos = this.getCartItemVos(cartKey);
        List<CartItemVo> collect = cartItemVos.stream()
                .filter(item -> item.getCheck())
                .map(item -> {
                    R r = productFeignService.getSkuinfoBySkuId(item.getSkuId());
                    if (r.getCode() == 0) {
                        SkuInfoFeignTo skuInfo = r.getDataByKey("skuInfo", new TypeReference<SkuInfoFeignTo>() {
                        });
                        item.setPrice(skuInfo.getPrice());
                    }
                    return item;
                })
                .collect(Collectors.toList());
        return collect;
    }
}
