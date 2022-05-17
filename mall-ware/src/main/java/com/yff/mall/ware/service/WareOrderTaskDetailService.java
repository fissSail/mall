package com.yff.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yff.common.utils.PageUtils;
import com.yff.mall.ware.entity.WareOrderTaskDetailEntity;

import java.util.List;
import java.util.Map;

/**
 * 库存工作单
 *
 * @author yff
 * @email 1335799488@qq.com
 * @date 2021-12-08 21:26:25
 */
public interface WareOrderTaskDetailService extends IService<WareOrderTaskDetailEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<WareOrderTaskDetailEntity> getOrderTaskDetailByTaskId(Long id);
}

