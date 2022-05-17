package com.yff.mall.ware.dao;

import com.yff.mall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品库存
 *
 * @author yff
 * @email 1335799488@qq.com
 * @date 2021-12-08 21:26:26
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    void updateStockBySkuIdAndWareId(WareSkuEntity data);

    Long getSkuStock(@Param("skuId") Long skuId);

    List<Long> listWareIdHasStock(@Param("skuId") Long skuId);

    Integer lockSkuStock(@Param("wareId") Long wareId, @Param("skuId") Long skuId, @Param("num") Integer num);

    void unLockStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("num") Integer num);

}
