package com.yff.mall.product.vo;

import lombok.Data;

import java.util.List;

/**
 * @author yanfeifan
 * @Package com.yff.mall.product.vo
 * @Description
 * @date 2022/1/29 9:23
 */
@Data
public class SaleAttrVo {

    private Long attrId;

    private String attrName;

    private List<AttrValueWithSkuIdVo> attrValue;
}
