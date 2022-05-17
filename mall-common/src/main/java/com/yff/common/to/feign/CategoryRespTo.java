package com.yff.common.to.feign;

import lombok.Data;

import java.util.List;

/**
 * @author yanfeifan
 * @Package com.yff.common.to.feign
 * @Description
 * @date 2022/1/17 21:29
 */
@Data
public class CategoryRespTo {

    private Long catalogId;

    private Long parentId;

    private String name;

    private List<CategoryRespTo> childrenList;
}
