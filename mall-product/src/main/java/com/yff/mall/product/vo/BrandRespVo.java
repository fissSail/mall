package com.yff.mall.product.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author yanfeifan
 * @Package com.yff.mall.product.vo
 * @Description
 * @date 2021/12/24 15:46
 */
@Data
public class BrandRespVo {

    /**
     * 品牌id
     */
    private Long brandId;
    /**
     * 品牌名
     */
    @NotBlank
    private String brandName;
}
