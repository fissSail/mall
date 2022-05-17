package com.yff.mall.search.vo;

import lombok.Data;

import java.util.List;

/**
 * @author yanfeifan
 * @Package com.yff.mall.search.vo
 * @Description
 * @date 2022/1/18 13:54
 */
@Data
public class SearchParamVo {

    /**
     * 关键字
     */
    private String keyword;

    /**
     * 2级分类id
     */
    private Long catalog2Id;

    /**
     * 3级分类id
     */
    private Long catalog3Id;

    /**
     * 排序字段
     * saleCount_desc/asc 销量
     * hotScore_desc/asc 热度
     * skuPrice_desc/asc 价格
     */
    private String sort;

    /**
     * 是否有货
     */
    private Integer hasStock;

    /**
     * 价格区间 1_100
     */
    private String skuPrice;

    /**
     * 品牌id
     */
    private List<Long> brandId;

    /**
     * 属性
     */
    private List<String> attrs;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    private String _queryString;
}
