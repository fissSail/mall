package com.yff.mall.product.vo;

import lombok.Data;

import java.util.List;

/**
 * @author yanfeifan
 * @Package com.yff.mall.product.vo
 * @Description
 * @date 2022/1/6 21:13
 */
@Data
public class CategoryRespVo {

    private Long catalogId;

    private Long parentId;

    private String name;

    private List<CategoryRespVo> childrenList;

}
