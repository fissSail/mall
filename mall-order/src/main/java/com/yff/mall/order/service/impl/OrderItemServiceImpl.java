package com.yff.mall.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yff.common.utils.PageUtils;
import com.yff.common.utils.Query;
import com.yff.mall.order.dao.OrderItemDao;
import com.yff.mall.order.entity.OrderItemEntity;
import com.yff.mall.order.service.OrderItemService;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("orderItemService")
//@RabbitListener(queues = {"directQueue"})
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderItemEntity> page = this.page(
                new Query<OrderItemEntity>().getPage(params),
                new QueryWrapper<OrderItemEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 监听rabbitMQ队列消息
     * Message原生消息详细信息
     * msg,接收到消息的类型
     * @param
     */
    //接收String类型
    /*@RabbitHandler
    public void recieveMsg(Message message, String msg, Channel channel){
        System.out.println("String:"+msg);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        //手动应答，非批量
        *//*try {
            channel.basicAck(deliveryTag,false);
            //拒绝消息
            //channel.basicNack(deliveryTag,false,false);
        } catch (IOException e) {
            e.printStackTrace();
        }*//*
    }*/

    //接收Integer类型
    /*@RabbitHandler
    public void recieveMsg(Message message, Integer msg, Channel channel)  {

        System.out.println("Integer:"+msg);

        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            channel.basicAck(deliveryTag,false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

}
