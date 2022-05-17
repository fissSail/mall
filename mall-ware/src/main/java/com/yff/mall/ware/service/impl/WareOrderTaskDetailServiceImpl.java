package com.yff.mall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yff.common.utils.PageUtils;
import com.yff.common.utils.Query;
import com.yff.mall.ware.dao.WareOrderTaskDetailDao;
import com.yff.mall.ware.entity.WareOrderTaskDetailEntity;
import com.yff.mall.ware.service.WareOrderTaskDetailService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("wareOrderTaskDetailService")
public class WareOrderTaskDetailServiceImpl extends ServiceImpl<WareOrderTaskDetailDao, WareOrderTaskDetailEntity> implements WareOrderTaskDetailService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WareOrderTaskDetailEntity> page = this.page(
                new Query<WareOrderTaskDetailEntity>().getPage(params),
                new QueryWrapper<WareOrderTaskDetailEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<WareOrderTaskDetailEntity> getOrderTaskDetailByTaskId(Long id) {
        return this.list(new QueryWrapper<WareOrderTaskDetailEntity>().eq("task_id",id).eq("lock_status",1));
    }

}
