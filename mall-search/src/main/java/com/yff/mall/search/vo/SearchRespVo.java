package com.yff.mall.search.vo;

import com.yff.common.to.es.SkuEsTo;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yanfeifan
 * @Package com.yff.mall.search.vo
 * @Description
 * @date 2022/1/18 14:45
 */
@Data
public class SearchRespVo {

    private List<SkuEsTo> products;

    private Integer pageNum;

    private Long total;

    private Integer totalPages;
    private List<Integer> totalPagesList;

    /**
     * 当前查询到的结果，所有涉及到的品牌
     */
    private List<BrandVo> brands;

    /**
     * 当前查询到的结果，所有涉及到的品牌
     */
    private List<CategoryVo> categorys;

    /**
     * 当前查询到的结果，所有涉及到的品牌
     */
    private List<Attr> attrs;

    /**
     * 面包屑导航
     */
    private List<NavVo> navs = new ArrayList<>(); //防止为null

    private List<Long> attrIds = new ArrayList<>();

    @Data
    public static class NavVo{
        private String navName;
        private String navValue;
        private String link;
    }

    @Data
    public static class BrandVo{
        private Long brandId;
        private String brandName;
        private String brandImg;
    }

    @Data
    public static class Attr{
        private Long attrId;
        private String attrName;
        private List<String> attrValue;
    }

    @Data
    public static class CategoryVo{
        private Long catalogId;
        private String catalogName;
    }
}
