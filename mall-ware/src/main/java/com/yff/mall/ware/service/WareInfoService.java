package com.yff.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yff.common.utils.PageUtils;
import com.yff.mall.ware.entity.WareInfoEntity;
import com.yff.mall.ware.vo.FreightVo;

import java.util.Map;

/**
 * 仓库信息
 *
 * @author yff
 * @email 1335799488@qq.com
 * @date 2021-12-08 21:26:25
 */
public interface WareInfoService extends IService<WareInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    FreightVo getFreight(Long addrId);
}

