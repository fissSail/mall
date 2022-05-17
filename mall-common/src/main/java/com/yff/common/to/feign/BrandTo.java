package com.yff.common.to.feign;

import lombok.Data;

/**
 * @author yanfeifan
 * @Package com.yff.common.to.feign
 * @Description
 * @date 2022/1/22 21:03
 */
@Data
public class BrandTo {

    /**
     * 品牌id
     */
    private Long brandId;
    /**
     * 品牌名
     */
    private String name;
    /**
     * 品牌logo地址
     */
    private String logo;
    /**
     * 介绍
     */
    private String descript;
    /**
     * 显示状态[0-不显示；1-显示]
     */
    private Integer showStatus;
    /**
     * 检索首字母
     */
    private String firstLetter;
    /**
     * 排序
     */
    private Integer sort;
}
