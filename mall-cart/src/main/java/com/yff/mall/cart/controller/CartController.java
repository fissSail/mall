package com.yff.mall.cart.controller;

import com.yff.common.constant.AuthServerConstant;
import com.yff.common.utils.R;
import com.yff.mall.cart.service.CartService;
import com.yff.mall.cart.vo.CartItemVo;
import com.yff.mall.cart.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author yanfeifan
 * @Package com.yff.mall.cart.controller
 * @Description
 * @date 2022/1/30 14:50
 */
@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * 浏览器有一个cookie，user-key 标识用户身份，一个月后过期
     * 如果第一次使用购物车功能，都会给一个临时的用户身份
     * 浏览器以后保存，每次访问都会带上这个cookie
     * <p>
     * 登录：session有
     * 没登录：按照cookie中的user-key
     * 第一次，没有临时用户，需要创建一个临时用户
     *
     * @return
     */
    @GetMapping("/cart.html")
    public String cartListPage(Model model) throws ExecutionException, InterruptedException {
        CartVo vo = cartService.getCartList();
        model.addAttribute("item", vo);
        return "cart";
    }

    @GetMapping("/addToCart")
    public String addToCart(@RequestParam("skuId") Long skuId,
                            @RequestParam("count") Integer count,
                            RedirectAttributes addAttribute) throws ExecutionException, InterruptedException {
        cartService.addToCart(skuId, count);
        addAttribute.addAttribute("skuId", skuId);
        return "redirect:" + AuthServerConstant.CART_PAGE + "/addToCart.html";
    }

    @GetMapping("/addToCart.html")
    public String sussessPage(@RequestParam("skuId") Long skuId, Model model) {
        CartItemVo vo = cartService.getCartItem(skuId);
        model.addAttribute("item", vo);
        return "success";
    }

    @GetMapping("/checkCart")
    public String checkCart(@RequestParam("check") Boolean check,
                            @RequestParam("skuId") Long skuId) {
        cartService.checkCart(check, skuId);
        return "redirect:" + AuthServerConstant.CART_PAGE + "/cart.html";
    }

    @GetMapping("/countCart")
    public String countCart(@RequestParam("count") Integer count,
                            @RequestParam("skuId") Long skuId) {
        cartService.countCart(count, skuId);
        return "redirect:" + AuthServerConstant.CART_PAGE + "/cart.html";
    }

    @GetMapping("/deleteCart")
    public String deleteCart(@RequestParam("skuId") Long skuId) {
        cartService.deleteCart(skuId);
        return "redirect:" + AuthServerConstant.CART_PAGE + "/cart.html";
    }

    @GetMapping("/getCartByMemberId")
    @ResponseBody
    public R getCartByMemberId() {
        List<CartItemVo> list = cartService.getCartByMemberId();
        return R.ok().setData(list);
    }
}
