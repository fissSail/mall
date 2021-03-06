package com.yff.mall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yff.common.constant.RabbitMQConstant;
import com.yff.common.exception.NoStockException;
import com.yff.common.to.feign.MemberAddressTo;
import com.yff.common.to.feign.OrderTo;
import com.yff.common.to.feign.SpuInfoFeignTo;
import com.yff.common.to.feign.WareSkuTo;
import com.yff.common.to.mq.QuickSeckillOrderTo;
import com.yff.common.utils.PageUtils;
import com.yff.common.utils.Query;
import com.yff.common.utils.R;
import com.yff.common.vo.MemberRespVo;
import com.yff.mall.order.constant.OrderConstant;
import com.yff.mall.order.dao.OrderDao;
import com.yff.mall.order.entity.OrderEntity;
import com.yff.mall.order.entity.OrderItemEntity;
import com.yff.mall.order.entity.PaymentInfoEntity;
import com.yff.mall.order.enume.OrderStatusEnum;
import com.yff.mall.order.feign.CartFeignService;
import com.yff.mall.order.feign.MemberFeignService;
import com.yff.mall.order.feign.ProductFeignService;
import com.yff.mall.order.feign.WareFeignService;
import com.yff.mall.order.interceptor.LoginInterceptor;
import com.yff.mall.order.service.OrderItemService;
import com.yff.mall.order.service.OrderService;
import com.yff.mall.order.service.PaymentInfoService;
import com.yff.mall.order.vo.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Autowired
    private MemberFeignService memberFeignService;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    private CartFeignService cartFeignService;

    @Autowired
    private WareFeignService wareFeignService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private PaymentInfoService paymentInfoService;

    private ThreadLocal<OrderSubmitVo> orderSubmitVoThreadLocal = new ThreadLocal<>();

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        MemberRespVo memberRespVo = LoginInterceptor.threadLocal.get();
        OrderConfirmVo vo = new OrderConfirmVo();
        CompletableFuture<Void> infoByMemberIdFuture = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            R r = memberFeignService.infoByMemberId(memberRespVo.getId());
            if (r.getCode() == 0) {
                List<MemberAddressTo> addressVos = r.getData(new TypeReference<List<MemberAddressTo>>() {
                });
                vo.setAddressVos(addressVos);
            }
        }, threadPoolExecutor);

        CompletableFuture<Void> getCartByMemberIdFuture = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            R r = cartFeignService.getCartByMemberId();
            if (r.getCode() == 0) {
                List<OrderItemVo> data = r.getData(new TypeReference<List<OrderItemVo>>() {
                });
                vo.setOrderItemVos(data);
            }
        }, threadPoolExecutor).thenRunAsync(() -> {
            List<OrderItemVo> orderItemVos = vo.getOrderItemVos();
            List<Long> skuIdList = orderItemVos.stream().map(data -> data.getSkuId()).collect(Collectors.toList());
            R r = wareFeignService.getSkuStock(skuIdList);
            if (r.getCode() == 0) {
                List<WareSkuTo> data = r.getData(new TypeReference<List<WareSkuTo>>() {
                });
                if (!CollectionUtils.isEmpty(data)) {
                    Map<Long, Boolean> getHasStock = data.stream().collect(Collectors.toMap(WareSkuTo::getSkuId, WareSkuTo::getHasStock));
                    vo.setHasStock(getHasStock);
                }
            }
        }, threadPoolExecutor);

        vo.setIntegration(memberRespVo.getIntegration());
        //????????????
        String uuid = UUID.randomUUID().toString().replace("-", "");
        //?????????reids???????????????????????????
        stringRedisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_KEY_PREFIX + memberRespVo.getId(), uuid, 30, TimeUnit.MINUTES);
        vo.setToken(uuid);
        CompletableFuture.allOf(infoByMemberIdFuture, getCartByMemberIdFuture).get();

        return vo;
    }

    @Override
    //@GlobalTransactional
    @Transactional
    public OrderSubmitRespVo submitOrder(OrderSubmitVo orderSubmitVo) throws ExecutionException, InterruptedException {
        orderSubmitVoThreadLocal.set(orderSubmitVo);
        OrderSubmitRespVo vo = new OrderSubmitRespVo();
        //????????????
        MemberRespVo memberRespVo = LoginInterceptor.threadLocal.get();
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Long result = stringRedisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class),
                Arrays.asList(OrderConstant.USER_ORDER_TOKEN_KEY_PREFIX + memberRespVo.getId()), orderSubmitVo.getToken());
        if (result == 1) {
            //????????????
            OrderCreateVo createVo = this.createOrder(memberRespVo);
            //??????
            BigDecimal payAmount = createVo.getOrderEntity().getPayAmount();
            BigDecimal payPrice = orderSubmitVo.getPayPrice();
            //????????????
            if (Math.abs(payAmount.subtract(payPrice).doubleValue()) < 0.01) {
                //????????????
                this.saveOrder(createVo);
                //????????????
                WareSkuLockVo wareSkuLockVo = new WareSkuLockVo();
                wareSkuLockVo.setOrderSn(createVo.getOrderEntity().getOrderSn());
                List<OrderItemVo> collect = createVo.getOrderItemEntities().stream().map(item -> {
                    OrderItemVo orderItemVo = new OrderItemVo();
                    orderItemVo.setSkuId(item.getSkuId());
                    orderItemVo.setCount(item.getSkuQuantity());
                    orderItemVo.setTitle(item.getSkuName());
                    return orderItemVo;
                }).collect(Collectors.toList());
                wareSkuLockVo.setOrderItemVoList(collect);
                R r = wareFeignService.orderLock(wareSkuLockVo);
                if (r.getCode() == 0) {
                    vo.setCode(0);
                    vo.setOrderEntity(createVo.getOrderEntity());
                    //???MQ????????????
                    rabbitTemplate.convertAndSend(RabbitMQConstant.ORDER_EVENT_EXCHANGE, RabbitMQConstant.ORDER_DELAY_ROUTING_KEY, createVo.getOrderEntity());
                    return vo;
                } else {
                    throw new NoStockException("?????????????????????");
                }
            }
        }
        vo.setCode(1);
        return vo;
    }

    @Override
    public OrderEntity getOrderStatusByOrderSn(String orderSn) {
        return this.getOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
    }

    @Override
    public void closeOrder(OrderEntity orderEntity) {
        //?????????????????????????????????????????????
        OrderEntity order = this.getById(orderEntity.getId());
        if (order.getStatus() == OrderStatusEnum.CREATE_NEW.getCode()) {
            //??????????????????????????????????????????
            OrderEntity updateEntity = new OrderEntity();
            updateEntity.setStatus(OrderStatusEnum.CANCLED.getCode());
            updateEntity.setId(orderEntity.getId());
            this.updateById(updateEntity);

            OrderTo to = new OrderTo();
            BeanUtils.copyProperties(order, to);

            //???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            rabbitTemplate.convertAndSend(RabbitMQConstant.ORDER_EVENT_EXCHANGE, RabbitMQConstant.ORDER_CLOSE_RELEASE_OTHER_ROUTING_KEY, to);
        }
    }

    @Override
    public PayVo getPayOrder(String orderSn) {

        PayVo payVo = new PayVo();

        OrderEntity order = this.getOrderStatusByOrderSn(orderSn);

        payVo.setTotal_amount(order.getPayAmount().setScale(2, BigDecimal.ROUND_UP).toString());
        payVo.setOut_trade_no(orderSn);
        payVo.setSubject(orderSn);
        payVo.setBody(orderSn);
        return payVo;
    }

    @Override
    public PageUtils queryPageWithItem(Map<String, Object> params) {
        MemberRespVo memberRespVo = LoginInterceptor.threadLocal.get();
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>().eq("member_id", memberRespVo.getId())
                        .orderByDesc("id")
        );

        List<OrderEntity> orderEntities = page.getRecords().stream().map(data -> {
            List<OrderItemEntity> orderItemEntities = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", data.getOrderSn()));
            data.setOrderItemEntities(orderItemEntities);
            return data;
        }).collect(Collectors.toList());

        page.setRecords(orderEntities);
        return new PageUtils(page);
    }

    /**
     * ??????????????????????????????
     * @param vo
     * @return
     */
    @Override
    public String handlePayResult(PayAsyncVo vo) {
        //??????????????????
        PaymentInfoEntity paymentInfoEntity = new PaymentInfoEntity();
        paymentInfoEntity.setOrderSn(vo.getOut_trade_no());
        paymentInfoEntity.setAlipayTradeNo(vo.getTrade_no());
        paymentInfoEntity.setPaymentStatus(vo.getTrade_status());
        paymentInfoEntity.setCallbackTime(vo.getNotify_time());
        paymentInfoService.save(paymentInfoEntity);

        //??????????????????
        if (vo.getTrade_status().equals("TRADE_SUCCESS") || vo.getTrade_status().equals("TRADE_FINISHED")) {
            OrderEntity order = new OrderEntity();
            order.setStatus(OrderStatusEnum.PAYED.getCode());
            this.update(order,new QueryWrapper<OrderEntity>().eq("order_sn",vo.getTrade_no()));
        }
        return "success";
    }

    @Override
    public void orderSeckill(QuickSeckillOrderTo to) {
        //??????????????????
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(to.getOrderSn());
        orderEntity.setMemberId(to.getMemberId());
        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        BigDecimal multiply = to.getSeckillPrice().multiply(new BigDecimal(to.getCount().toString()));
        orderEntity.setPayAmount(multiply);
        this.save(orderEntity);
        //?????????????????????
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        orderItemEntity.setOrderSn(to.getOrderSn());
        orderItemEntity.setRealAmount(multiply);
        orderItemEntity.setSkuQuantity(to.getCount());
        orderItemService.save(orderItemEntity);
    }

    /**
     * ??????????????????
     *
     * @param createVo
     */
    public void saveOrder(OrderCreateVo createVo) {
        OrderEntity orderEntity = createVo.getOrderEntity();
        List<OrderItemEntity> orderItemEntities = createVo.getOrderItemEntities();
        this.save(orderEntity);

        orderItemService.saveBatch(orderItemEntities);
    }

    private OrderCreateVo createOrder(MemberRespVo memberRespVo) throws ExecutionException, InterruptedException {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        OrderSubmitVo orderSubmitVo = orderSubmitVoThreadLocal.get();
        OrderCreateVo orderCreateVo = new OrderCreateVo();
        String orderSn = IdWorker.getTimeId();
        //???????????????

        RequestContextHolder.setRequestAttributes(requestAttributes);
        //??????????????????
        OrderEntity orderEntity = this.buildOrderEntity(orderSubmitVo, orderSn, memberRespVo);
        orderCreateVo.setOrderEntity(orderEntity);

        //?????????????????????
        RequestContextHolder.setRequestAttributes(requestAttributes);
        List<OrderItemEntity> orderItemEntities = this.buildOrderItem(orderSn);
        orderCreateVo.setOrderItemEntities(orderItemEntities);

        //????????????
        this.computePrice(orderCreateVo.getOrderEntity(), orderCreateVo.getOrderItemEntities());

        return orderCreateVo;
    }

    private void computePrice(OrderEntity orderEntity, List<OrderItemEntity> orderItemEntities) {

        BigDecimal total = new BigDecimal("0.0");
        BigDecimal c = new BigDecimal("0.0");
        BigDecimal i = new BigDecimal("0.0");
        BigDecimal p = new BigDecimal("0.0");
        BigDecimal giftGrowth = new BigDecimal("0.0");
        BigDecimal giftIntegration = new BigDecimal("0.0");
        for (OrderItemEntity orderItemEntity : orderItemEntities) {
            total = total.add(orderItemEntity.getRealAmount());
            c = c.add(orderItemEntity.getCouponAmount());
            i = i.add(orderItemEntity.getIntegrationAmount());
            p = p.add(orderItemEntity.getPromotionAmount());
            giftGrowth = giftGrowth.add(new BigDecimal(orderItemEntity.getGiftGrowth().toString()));
            giftIntegration = giftIntegration.add(new BigDecimal(orderItemEntity.getGiftIntegration().toString()));
        }

        orderEntity.setTotalAmount(total);
        orderEntity.setPayAmount(total.add(orderEntity.getFreightAmount()));
        orderEntity.setCouponAmount(c);
        orderEntity.setPromotionAmount(p);
        orderEntity.setIntegrationAmount(i);
        orderEntity.setIntegration(giftIntegration.intValue());
        orderEntity.setGrowth(giftGrowth.intValue());
    }

    private OrderEntity buildOrderEntity(OrderSubmitVo orderSubmitVo, String orderSn, MemberRespVo memberRespVo) {
        OrderEntity orderEntity = new OrderEntity();

        orderEntity.setOrderSn(orderSn);

        R r = wareFeignService.getFreight(orderSubmitVo.getAddrId());
        if (r.getCode() == 0) {
            FreightVo freightVo = r.getData(new TypeReference<FreightVo>() {
            });
            orderEntity.setFreightAmount(freightVo.getFreight());
            MemberAddressTo memberAddressTo = freightVo.getMemberAddressTo();
            orderEntity.setReceiverCity(memberAddressTo.getCity());
            orderEntity.setReceiverDetailAddress(memberAddressTo.getDetailAddress());
            orderEntity.setReceiverName(memberAddressTo.getName());
            orderEntity.setReceiverPhone(memberAddressTo.getPhone());
            orderEntity.setReceiverProvince(memberAddressTo.getProvince());
            orderEntity.setReceiverRegion(memberAddressTo.getRegion());
            orderEntity.setReceiverPostCode(memberAddressTo.getPostCode());
        }
        orderEntity.setModifyTime(new Date());
        orderEntity.setMemberId(memberRespVo.getId());
        orderEntity.setMemberUsername(memberRespVo.getUsername());
        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        orderEntity.setAutoConfirmDay(7);
        orderEntity.setDeleteStatus(0);
        return orderEntity;
    }

    private List<OrderItemEntity> buildOrderItem(String orderSn) {
        R r = cartFeignService.getCartByMemberId();
        if (r.getCode() == 0) {
            List<OrderItemVo> itemVos = r.getData(new TypeReference<List<OrderItemVo>>() {
            });
            if (!CollectionUtils.isEmpty(itemVos)) {
                List<OrderItemEntity> orderItemEntities = itemVos.stream().map(item -> {
                    OrderItemEntity orderItemEntity = this.getOrderItemEntity(orderSn, item);
                    return orderItemEntity;
                }).collect(Collectors.toList());
                return orderItemEntities;
            }
        }
        return null;
    }

    private OrderItemEntity getOrderItemEntity(String orderSn, OrderItemVo item) {
        Long skuId = item.getSkuId();
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        //????????????
        orderItemEntity.setOrderSn(orderSn);
        //??????spu??????
        R spuInfoR = productFeignService.getSpuInfoBySkuId(skuId);
        if (spuInfoR.getCode() == 0) {
            SpuInfoFeignTo spuInfoFeignTo = spuInfoR.getData(new TypeReference<SpuInfoFeignTo>() {
            });
            orderItemEntity.setSpuId(spuInfoFeignTo.getId());
            orderItemEntity.setSpuName(spuInfoFeignTo.getSpuName());
            orderItemEntity.setCategoryId(spuInfoFeignTo.getCatalogId());
        }
        //??????sku??????
        orderItemEntity.setSkuId(skuId);
        orderItemEntity.setSkuName(item.getTitle());
        orderItemEntity.setSkuPic(item.getImage());
        orderItemEntity.setSkuPrice(item.getPrice());
        String skuAttr = StringUtils.collectionToDelimitedString(item.getSkuAttr(), ";");
        orderItemEntity.setSkuAttrsVals(skuAttr);
        orderItemEntity.setSkuQuantity(item.getCount());
        //????????????
        orderItemEntity.setGiftGrowth(item.getPrice().multiply(new BigDecimal(item.getCount().toString())).intValue());
        orderItemEntity.setGiftIntegration(item.getPrice().multiply(new BigDecimal(item.getCount().toString())).intValue());
        //????????????
        orderItemEntity.setPromotionAmount(new BigDecimal("0"));
        orderItemEntity.setCouponAmount(new BigDecimal("0"));
        orderItemEntity.setIntegrationAmount(new BigDecimal("0"));
        BigDecimal multiply = item.getPrice().multiply(new BigDecimal(item.getCount().toString()));
        multiply.subtract(orderItemEntity.getPromotionAmount())
                .subtract(orderItemEntity.getCouponAmount())
                .subtract(orderItemEntity.getIntegrationAmount());
        orderItemEntity.setRealAmount(multiply);
        return orderItemEntity;
    }

}
