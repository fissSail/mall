package com.yff.mall.product.vo;

import com.yff.common.annotation.ShowStatus;
import com.yff.common.vaild.AddGroup;
import com.yff.common.vaild.UpdateGroup;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

/**
 * @author yanfeifan
 * @Package com.yff.mall.product.vo
 * @Description
 * @date 2021/12/21 14:47
 */
@Data
public class BrandVo {

    /**
     * 品牌id
     */
    @NotNull(message = "修改必须指定id",groups = {UpdateGroup.class})
    @Null(message = "新增不能指定id",groups = {AddGroup.class})
    private Long brandId;
    /**
     * 品牌名
     */
    @NotBlank
    private String name;
    /**
     * 品牌logo地址
     */
    @NotEmpty(groups = {AddGroup.class})
    @URL(message = "logo必须是url地址",groups = {AddGroup.class, UpdateGroup.class})
    private String logo;
    /**
     * 介绍
     */
    @NotBlank(groups = {AddGroup.class})
    private String descript;
    /**
     * 显示状态[0-不显示；1-显示]
     */
    @ShowStatus(value = {0,1},groups = {AddGroup.class,UpdateGroup.class})
    private Integer showStatus;
    /**
     * 检索首字母
     */
    @NotEmpty(groups = {AddGroup.class})
    @Pattern(regexp = "^[a-zA-Z]$", message = "检索首字母必须是一个字母",groups = {AddGroup.class,UpdateGroup.class})
    private String firstLetter;
    /**
     * 排序
     */
    @NotNull(groups = {AddGroup.class})
    @Min(value = 0, message = "排序必须大于等于0",groups = {AddGroup.class,UpdateGroup.class})
    private Integer sort;
}
