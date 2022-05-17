package com.yff.mall.product.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 * @author yanfeifan
 * @Package com.yff.mall.product.vo
 * @Description
 * @date 2021/12/21 14:38
 */
@Data
public class CategoryVo {
    /**
     * 分类id
     */
    private Long catId;
    /**
     * 分类名称
     */
    private String name;
    /**
     * 父分类id
     */
    private Long parentCid;
    /**
     * 层级
     */
    private Integer catLevel;
    /**
     * 是否显示[0-不显示，1显示]
     */
    private Integer showStatus;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 图标地址
     */
    private String icon;
    /**
     * 计量单位
     */
    private String productUnit;
    /**
     * 商品数量
     */
    private Integer productCount;
    /**
     * 子商品分类
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY) //这个字段不为空才返回
    private List<CategoryVo> children;
}
