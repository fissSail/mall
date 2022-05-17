package com.yff.mall.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yff.common.to.feign.SeckillSkuRelationTo;
import com.yff.common.utils.PageUtils;
import com.yff.common.utils.Query;
import com.yff.mall.coupon.dao.SeckillSessionDao;
import com.yff.mall.coupon.entity.SeckillSessionEntity;
import com.yff.mall.coupon.entity.SeckillSkuRelationEntity;
import com.yff.mall.coupon.service.SeckillSessionService;
import com.yff.mall.coupon.service.SeckillSkuRelationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("seckillSessionService")
public class SeckillSessionServiceImpl extends ServiceImpl<SeckillSessionDao, SeckillSessionEntity> implements SeckillSessionService {

    @Autowired
    private SeckillSkuRelationService seckillSessionService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SeckillSessionEntity> page = this.page(
                new Query<SeckillSessionEntity>().getPage(params),
                new QueryWrapper<SeckillSessionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SeckillSessionEntity> getSeckillSessionByLastThreeDays() {
        List<SeckillSessionEntity> list = this.list(new QueryWrapper<SeckillSessionEntity>()
                .between("start_time", this.getStartTime(), this.getEndTime()));

        if (!CollectionUtils.isEmpty(list)) {
            list = list.stream().map(item -> {
                Long id = item.getId();
                List<SeckillSkuRelationEntity> seckillSkuRelationEntities = seckillSessionService.list(new QueryWrapper<SeckillSkuRelationEntity>().eq("promotion_session_id", id));


                List<SeckillSkuRelationTo> seckillSkuRelationTos = seckillSkuRelationEntities.stream().map(data -> {
                    SeckillSkuRelationTo seckillSkuRelationTo = new SeckillSkuRelationTo();
                    BeanUtils.copyProperties(data, seckillSkuRelationTo);
                    return seckillSkuRelationTo;
                }).collect(Collectors.toList());

                item.setSeckillSkuRelationTos(seckillSkuRelationTos);
                return item;
            }).collect(Collectors.toList());
        }

        return list;

    }

    private String getStartTime() {
        LocalDate now = LocalDate.now();
        LocalTime min = LocalTime.MIN;
        LocalDateTime startTime = LocalDateTime.of(now, min);
        String format = startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return format;
    }

    private String getEndTime() {
        LocalDate now = LocalDate.now();
        //第3天
        LocalDate localDate = now.plusDays(2);
        LocalTime max = LocalTime.MAX;
        LocalDateTime endTime = LocalDateTime.of(localDate, max);
        String format = endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return format;
    }

}
