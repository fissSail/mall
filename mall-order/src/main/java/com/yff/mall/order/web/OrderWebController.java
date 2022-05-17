package com.yff.mall.order.web;

import com.alipay.api.AlipayApiException;
import com.yff.common.constant.AuthServerConstant;
import com.yff.mall.order.config.AlipayTemplate;
import com.yff.mall.order.service.OrderService;
import com.yff.mall.order.vo.OrderConfirmVo;
import com.yff.mall.order.vo.OrderSubmitRespVo;
import com.yff.mall.order.vo.OrderSubmitVo;
import com.yff.mall.order.vo.PayVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.ExecutionException;

/**
 * @author yanfeifan
 * @Package com.yff.mall.order.web
 * @Description
 * @date 2022/2/3 20:11
 */
@Controller
public class OrderWebController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private AlipayTemplate alipayTemplate;


  /*  @GetMapping("/queue")
    @ResponseBody
    public String queue() {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(UUID.randomUUID().toString());
        orderEntity.setModifyTime(new Date());
        rabbitTemplate.convertAndSend("order-event-exchange", "order.create", orderEntity);
        return "ok";
    }*/

    @GetMapping("/toTrade")
    public String toTrade(Model model) throws ExecutionException, InterruptedException {
        OrderConfirmVo vo = orderService.confirmOrder();
        model.addAttribute("item", vo);
        return "closeaccount";
    }

    @PostMapping("/submit/order")
    public String submitOrder(OrderSubmitVo orderSubmitVo, Model model) {
        //下单
        OrderSubmitRespVo respVo = null;
        try {
            respVo = orderService.submitOrder(orderSubmitVo);
        } catch (Exception e) {
            e.printStackTrace();
            //失败回到订单确认页
            return "redirect:" + AuthServerConstant.ORDER_PAGE + "/toTrade";
        }
        //成功去支付选择页
        if (respVo.getCode() == 0) {
            model.addAttribute("item", respVo.getOrderEntity());
            return "pay";
        } else {
            //失败回到订单确认页
            return "redirect:" + AuthServerConstant.ORDER_PAGE + "/toTrade";
        }
    }

    /**
     * 跳转订单支付页
     * @param orderSn
     * @return
     * @throws AlipayApiException
     */
    @GetMapping(value="/payOrder", produces = "text/html")
    @ResponseBody
    public String payOrder(@RequestParam("orderSn")String orderSn) throws AlipayApiException {

        PayVo vo = orderService.getPayOrder(orderSn);
        String pay = alipayTemplate.pay(vo);
        return pay;
    }
}
