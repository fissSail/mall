package com.yff.mall.product.dao;

import com.yff.mall.product.entity.CommentReplayEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品评价回复关系
 *
 * @author yff
 * @email 1335799488@qq.com
 * @date 2021-12-07 21:18:59
 */
@Mapper
public interface CommentReplayDao extends BaseMapper<CommentReplayEntity> {

}
