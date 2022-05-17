package com.yff.mall.product.vo;

import lombok.Data;

/**
 * @author yanfeifan
 * @Package com.yff.mall.product.vo
 * @Description 商品属性响应返回实体
 * @date 2021/12/21 15:43
 */
@Data
public class AttrRespVo extends AttrVo{

    /**
     * 所属分类名称
     */
    private String catelogName;

    /**
     * 所属分组名称
     */
    private String groupName;

    /**
     * 所属分类的三级分类id
     */
    private Long[] catelogPath;

}
