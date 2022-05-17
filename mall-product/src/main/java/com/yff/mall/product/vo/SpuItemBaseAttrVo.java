package com.yff.mall.product.vo;

import lombok.Data;

import java.util.List;

/**
 * @author yanfeifan
 * @Package com.yff.mall.product.vo
 * @Description
 * @date 2022/1/26 21:00
 */

@Data
public class SpuItemBaseAttrVo {

    private String groupName;
    private List<Attr> baseAttrs;
}
