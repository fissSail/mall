package com.yff.mall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yff.common.constant.RabbitMQConstant;
import com.yff.common.exception.NoStockException;
import com.yff.common.to.feign.OrderTo;
import com.yff.common.to.feign.WareSkuTo;
import com.yff.common.to.mq.StockDetailTo;
import com.yff.common.to.mq.StockLockedTo;
import com.yff.common.utils.PageUtils;
import com.yff.common.utils.Query;
import com.yff.common.utils.R;
import com.yff.mall.ware.dao.WareSkuDao;
import com.yff.mall.ware.entity.PurchaseDetailEntity;
import com.yff.mall.ware.entity.WareOrderTaskDetailEntity;
import com.yff.mall.ware.entity.WareOrderTaskEntity;
import com.yff.mall.ware.entity.WareSkuEntity;
import com.yff.mall.ware.feign.OrderFeignService;
import com.yff.mall.ware.feign.ProductFeignService;
import com.yff.mall.ware.service.PurchaseDetailService;
import com.yff.mall.ware.service.WareOrderTaskDetailService;
import com.yff.mall.ware.service.WareOrderTaskService;
import com.yff.mall.ware.service.WareSkuService;
import com.yff.mall.ware.vo.OrderItemVo;
import com.yff.mall.ware.vo.PurchaseItemDoneVo;
import com.yff.mall.ware.vo.WareSkuLockVo;
import lombok.Data;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    private PurchaseDetailService purchaseDetailService;

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private WareOrderTaskService wareOrderTaskService;

    @Autowired
    private WareOrderTaskDetailService wareOrderTaskDetailService;

    @Autowired
    private OrderFeignService orderFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String skuId = (String) params.get("skuId");
        String wareId = (String) params.get("wareId");

        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                new QueryWrapper<WareSkuEntity>()
                        .eq(StringUtils.hasText(wareId), "ware_id", wareId)
                        .eq(StringUtils.hasText(skuId), "sku_id", skuId)
        );

        return new PageUtils(page);
    }

    @Override
    public void saveOrUpdateBatchBySkuId(PurchaseItemDoneVo vo) {
        //将成功采购的入库
        WareSkuEntity wareSku = new WareSkuEntity();
        PurchaseDetailEntity purchaseDetail = purchaseDetailService.getById(vo.getItemId());
        wareSku.setSkuId(purchaseDetail.getSkuId());
        wareSku.setWareId(purchaseDetail.getWareId());
        wareSku.setStock(purchaseDetail.getSkuNum());
        wareSku.setStockLocked(0);

        List<WareSkuEntity> wareSkuEntityList = baseMapper.selectList(new QueryWrapper<WareSkuEntity>().eq("sku_id", wareSku.getSkuId())
                .eq("ware_id", wareSku.getWareId()));

        if (wareSkuEntityList.size() > 0) {
            //修改
            baseMapper.updateStockBySkuIdAndWareId(wareSku);
        } else {
            //新增
            R r = productFeignService.info(purchaseDetail.getSkuId());
            if (r.getCode() == 0) {
                //出现问题不回滚全部，抛出异常
                try {
                    Map<String, Object> infoMap = (Map<String, Object>) r.get("skuInfo");
                    wareSku.setSkuName((String) infoMap.get("skuName"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            this.save(wareSku);
        }
    }

    @Override
    public List<WareSkuTo> getSkuStock(List<Long> skuIds) {
        List<WareSkuTo> voList = skuIds.stream().map(skuId -> {
            Long skuStock = baseMapper.getSkuStock(skuId);
            WareSkuTo vo = new WareSkuTo();
            vo.setSkuId(skuId);
            vo.setHasStock(skuStock > 0);
            return vo;
        }).collect(Collectors.toList());
        return voList;
    }

    @Override
    @Transactional
    public Boolean orderLock(WareSkuLockVo wareSkuLockVo) {
        //保存库存工作单
        WareOrderTaskEntity wareOrderTaskEntity = new WareOrderTaskEntity();
        wareOrderTaskEntity.setOrderSn(wareSkuLockVo.getOrderSn());
        wareOrderTaskService.save(wareOrderTaskEntity);


        List<OrderItemVo> orderItemVoList = wareSkuLockVo.getOrderItemVoList();
        //找到每个商品在那个仓库有库存
        List<SkuWareHasStock> collect = orderItemVoList.stream().map(data -> {
            SkuWareHasStock skuWareHasStock = new SkuWareHasStock();
            skuWareHasStock.setNum(data.getCount());
            Long skuId = data.getSkuId();
            skuWareHasStock.setSkuId(skuId);
            List<Long> wareIds = baseMapper.listWareIdHasStock(skuId);
            skuWareHasStock.setWareIds(wareIds);
            return skuWareHasStock;
        }).collect(Collectors.toList());
        //锁定库存
        for (SkuWareHasStock skuWareHasStock : collect) {
            Boolean skuStocked = false;
            Long skuId = skuWareHasStock.getSkuId();
            Integer num = skuWareHasStock.getNum();
            if (CollectionUtils.isEmpty(skuWareHasStock.wareIds)) {
                throw new NoStockException(skuId);
            }
            for (Long wareId : skuWareHasStock.wareIds) {
                Integer count = baseMapper.lockSkuStock(wareId, skuId, num);
                if (count == 1) {
                    //当前仓库锁定商品成功
                    skuStocked = true;
                    //只要有一个仓库锁定成功，就可以返回
                    //锁定商品成功，要往MQ发送消息
                    //保存工作单详情
                    WareOrderTaskDetailEntity wareOrderTaskDetailEntity = new WareOrderTaskDetailEntity();
                    wareOrderTaskDetailEntity.setSkuId(skuId);
                    wareOrderTaskDetailEntity.setWareId(wareId);
                    wareOrderTaskDetailEntity.setSkuNum(num);
                    wareOrderTaskDetailEntity.setTaskId(wareOrderTaskEntity.getId());
                    wareOrderTaskDetailEntity.setLockStatus(1);
                    wareOrderTaskDetailService.save(wareOrderTaskDetailEntity);
                    StockLockedTo stockLockedTo = new StockLockedTo();
                    stockLockedTo.setTaskId(wareOrderTaskEntity.getId());
                    StockDetailTo stockDetailTo = new StockDetailTo();
                    BeanUtils.copyProperties(wareOrderTaskDetailEntity, stockDetailTo);
                    stockLockedTo.setStockDetailTo(stockDetailTo);
                    //每个商品锁定成功，将当前商品锁定了几件的工作单记录发送给MQ
                    //锁定失败，前面保存的工作单信息就回滚了，发送的消息，即使要解锁，由于查不到id，也不用解锁
                    rabbitTemplate.convertAndSend(RabbitMQConstant.STOCK_EVENT_EXCHANGE, RabbitMQConstant.DELAY_ROUTING_KEY, stockLockedTo);
                    break;
                }
            }
            if (!skuStocked) {
                throw new NoStockException(skuId);
            }
        }
        return true;
    }

    /**
     * 监听rabbitMq队列消息 ,库存自动解锁
     * - 下订单成功，订单过期没有支付被系统自动取消，被用户手动取消
     * - 下订单成功，库存锁定成功，接下来业务调用失败，导致订单回滚
     */
    @Override
    @Transactional
    public void stockLockedRelease(StockLockedTo to) throws IOException {
        Long taskId = to.getTaskId();

        StockDetailTo stockDetailTo = to.getStockDetailTo();

        //解锁
        /**
         * 查询数据库关于这个订单的锁定库存信息
         * 有：库存锁定成功
         *      订单情况
         *          有：下单成功
         *              订单状态
         *                  已取消：解锁库存
         *                  未取消，不能解锁
         *          没有：没有订单，订单事务回滚，库存锁定成功，必须解锁库存
         * 没有：库存锁定失败，直接事务回滚，无需解锁
         */

        WareOrderTaskDetailEntity data = wareOrderTaskDetailService.getById(stockDetailTo.getId());

        if (Objects.nonNull(data)) {
            //库存锁定成功
            WareOrderTaskEntity wareOrderTaskEntity = wareOrderTaskService.getById(taskId);
            //远程查询订单详情
            R r = orderFeignService.getOrderStatus(wareOrderTaskEntity.getOrderSn());
            if (r.getCode() == 0) {
                OrderTo order = r.getData(new TypeReference<OrderTo>() {
                });

                if ((order == null || order.getStatus() == 4) && data.getLockStatus() == 1) {
                    //订单已经被取消，才能解锁库存 并且库存工作单是已锁定状态
                    unLockStock(data.getSkuId(), data.getWareId(), data.getSkuNum(), data.getId());
                }
            } else {
                throw new RuntimeException("远程查询订单失败");
            }
        }
    }

    /**
     * 防止订单服务卡顿，导致订单状态消息一直改不了，库存消息到期，查订单状态为新建状态，解锁失败
     * @param to
     * @throws IOException
     */
    @Override
    @Transactional
    public void stockLockedRelease(OrderTo to) throws IOException {
        String orderSn = to.getOrderSn();
        //查询最新库存工作单的状态，防止重复解锁
        WareOrderTaskEntity wareOrderTaskEntity = wareOrderTaskService.getOrderTaskByOrderSn(orderSn);
        //按照工作单找到所有没有解锁的库存，进行解锁
        List<WareOrderTaskDetailEntity> wareOrderTaskDetailEntities = wareOrderTaskDetailService.getOrderTaskDetailByTaskId(wareOrderTaskEntity.getId());
        for (WareOrderTaskDetailEntity wareOrderTaskDetailEntity : wareOrderTaskDetailEntities) {

            unLockStock(wareOrderTaskDetailEntity.getSkuId(), wareOrderTaskDetailEntity.getWareId(), wareOrderTaskDetailEntity.getSkuNum(), wareOrderTaskDetailEntity.getId());
        }
    }

    /**
     * 解锁库存
     *
     * @param skuId
     * @param wareId
     * @param num
     */
    private void unLockStock(Long skuId, Long wareId, Integer num, Long taskDetailId) {
        baseMapper.unLockStock(skuId, wareId, num);
        //更新库存工作单的状态
        WareOrderTaskDetailEntity entity = new WareOrderTaskDetailEntity();
        entity.setLockStatus(2);
        entity.setId(taskDetailId);
        wareOrderTaskDetailService.updateById(entity);
    }

    @Data
    class SkuWareHasStock {
        private Long skuId;
        private Integer num;
        private List<Long> wareIds;
    }

}
