package com.yff.mall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yff.common.to.feign.MemberAddressTo;
import com.yff.common.utils.PageUtils;
import com.yff.common.utils.Query;
import com.yff.common.utils.R;
import com.yff.mall.ware.dao.WareInfoDao;
import com.yff.mall.ware.entity.WareInfoEntity;
import com.yff.mall.ware.feign.MemberFeignService;
import com.yff.mall.ware.service.WareInfoService;
import com.yff.mall.ware.vo.FreightVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Autowired
    private MemberFeignService memberFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String key = (String) params.get("key");

        QueryWrapper<WareInfoEntity> queryWrapper = new QueryWrapper<>();

        if (StringUtils.hasText(key)) {
            queryWrapper.and(data -> data.like("name", key)
                    .or().like("address", key)
                    .or().like("areacode", key)
            );
        }

        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public FreightVo getFreight(Long addrId) {
        FreightVo vo = new FreightVo();
        R info = memberFeignService.info(addrId);
        if (info.getCode() == 0) {
            MemberAddressTo data = info.getDataByKey("memberReceiveAddress", new TypeReference<MemberAddressTo>() {});
            Optional<BigDecimal> optional = Optional.ofNullable(data).map(item -> {
                String substring = item.getPhone().substring(item.getPhone().length() - 1);
                return new BigDecimal(substring);
            });
            vo.setFreight(optional.get());
            vo.setMemberAddressTo(data);
            return vo;
        }
        return null;
    }

}
