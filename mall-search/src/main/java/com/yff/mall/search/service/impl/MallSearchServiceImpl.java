package com.yff.mall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.yff.common.constant.AuthServerConstant;
import com.yff.common.to.es.SkuEsTo;
import com.yff.common.to.feign.BrandTo;
import com.yff.common.utils.R;
import com.yff.mall.search.config.MallElasticSearchConfig;
import com.yff.mall.search.constant.EsConstant;
import com.yff.mall.search.feign.ProductFeignService;
import com.yff.mall.search.service.MallSearchService;
import com.yff.mall.search.vo.SearchParamVo;
import com.yff.mall.search.vo.SearchRespVo;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author yanfeifan
 * @Package com.yff.mall.search.service.impl
 * @Description
 * @date 2022/1/18 13:57
 */
@Service
public class MallSearchServiceImpl implements MallSearchService {

    @Autowired
    private RestHighLevelClient client;

    @Autowired
    private ProductFeignService productFeignService;

    @Override
    public SearchRespVo search(SearchParamVo vo) {
        SearchRespVo result = new SearchRespVo();


        //创建检索请求
        SearchRequest searchRequest = this.buildSearchRequest(vo);

        try {
            SearchResponse search = client.search(searchRequest, MallElasticSearchConfig.COMMON_OPTIONS);
            result = this.buildSearchResult(search, vo);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 组装检索请求
     *
     * @return
     */
    private SearchRequest buildSearchRequest(SearchParamVo vo) {
        //指定DSL检索条件
        SearchSourceBuilder builder = new SearchSourceBuilder();

        /**
         * 模糊匹配，过滤（属性，分类，品牌，价格，库存）,高亮
         */
        //构建bool
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        //模糊匹配 must
        if (StringUtils.hasText(vo.getKeyword())) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle");
            highlightBuilder.preTags("<b style='color:red'>");
            highlightBuilder.postTags("</b>");
            //高亮
            builder.highlighter(highlightBuilder);

            boolQueryBuilder.must(QueryBuilders.matchQuery("skuTitle", vo.getKeyword()));
        }
        // filter catalogId 根据分类id查询
        Optional.ofNullable(vo.getCatalog3Id()).ifPresent(data -> boolQueryBuilder.filter(QueryBuilders.termQuery("catalogId", data)));

        //brandId
        List<Long> brandId = vo.getBrandId();
        if (!ObjectUtils.isEmpty(brandId) && !brandId.isEmpty()) {
            boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId", brandId));
        }

        //hasStock
        Optional.ofNullable(vo.getHasStock()).ifPresent(hasStock -> boolQueryBuilder.filter(QueryBuilders.termQuery("hasStock", hasStock == 1)));

        //skuPrice
        String skuPrice = vo.getSkuPrice();
        if (StringUtils.hasText(skuPrice)) {
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("skuPrice");

            String[] s = skuPrice.split("_");
            if (s.length == 2) {
                //区间
                rangeQueryBuilder.gte(s[0]).lte(s[1]);
            } else if(s.length == 1){
                if (skuPrice.startsWith("_")) {
                    rangeQueryBuilder.lte(s[0]);
                }
                if (skuPrice.endsWith("_")) {
                    rangeQueryBuilder.gte(s[0]);
                }
            }

            boolQueryBuilder.filter(rangeQueryBuilder);
        }

        //attrs
        List<String> attrs = vo.getAttrs();
        if (!ObjectUtils.isEmpty(attrs) && !attrs.isEmpty()) {
            for (String attr : attrs) {
                BoolQueryBuilder nestedBoolQueryBuilder = QueryBuilders.boolQuery();
                String[] s = attr.split("_");
                //属性id
                String attrId = s[0];
                //属性value
                String[] attrValues = s[1].split(":");

                nestedBoolQueryBuilder.must(QueryBuilders.termQuery("attrs.attrId", attrId));
                nestedBoolQueryBuilder.must(QueryBuilders.termsQuery("attrs.attrValue", attrValues));
                //每一个必须都得生成一个nested查询
                NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("attrs", nestedBoolQueryBuilder, ScoreMode.None);
                boolQueryBuilder.filter(nestedQueryBuilder);
            }
        }

        builder.query(boolQueryBuilder);

        /**
         * 排序，分页
         */
        //排序
        String sort = vo.getSort();
        if (StringUtils.hasText(sort)) {
            String[] s = sort.split("_");

            SortOrder sortOrder = s[1].equalsIgnoreCase("asc") ? SortOrder.ASC : SortOrder.DESC;

            builder.sort(s[0], sortOrder);
        }
        //分页
        //(pageNum-1)*pageSize
        builder.from((vo.getPageNum() - 1) * EsConstant.PRODUCT_PAGESIZE);
        builder.size(EsConstant.PRODUCT_PAGESIZE);

        /**
         * 聚合
         */
        //品牌聚合
        TermsAggregationBuilder brandAggBuilder = AggregationBuilders.terms("brand_agg");
        brandAggBuilder.field("brandId");
        //子聚合
        brandAggBuilder.subAggregation(AggregationBuilders.terms("brandName_agg").field("brandName"));
        brandAggBuilder.subAggregation(AggregationBuilders.terms("brandImg_agg").field("brandImg"));

        builder.aggregation(brandAggBuilder);

        //分类聚合
        TermsAggregationBuilder catalogAggBuilder = AggregationBuilders.terms("catalog_agg");
        catalogAggBuilder.field("catalogId");
        catalogAggBuilder.subAggregation(AggregationBuilders.terms("catalogName_agg").field("catalogName"));
        builder.aggregation(catalogAggBuilder);

        //属性聚合
        NestedAggregationBuilder attrsNestedBuilder = AggregationBuilders.nested("attr_agg", "attrs");
        TermsAggregationBuilder attrIdAggBuilder = AggregationBuilders.terms("attrId_agg").field("attrs.attrId");
        //子聚合
        attrIdAggBuilder.subAggregation(AggregationBuilders.terms("attrName_agg").field("attrs.attrName"));
        attrIdAggBuilder.subAggregation(AggregationBuilders.terms("attrValue_agg").field("attrs.attrValue"));
        attrsNestedBuilder.subAggregation(attrIdAggBuilder);
        builder.aggregation(attrsNestedBuilder);

        System.out.println(builder.toString());

        //指定索引
        SearchRequest searchRequest = new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, builder);
        return searchRequest;
    }

    /**
     * 组装返回结果
     *
     * @param search
     * @return
     */
    private SearchRespVo buildSearchResult(SearchResponse search, SearchParamVo paramVo) {
        SearchRespVo vo = new SearchRespVo();

        SearchHits searchHits = search.getHits();

        SearchHit[] hits = searchHits.getHits();

        List<SkuEsTo> list = new ArrayList<>();
        for (SearchHit hit : hits) {
            String sourceAsString = hit.getSourceAsString();
            SkuEsTo skuEsTo = JSON.parseObject(sourceAsString, SkuEsTo.class);
            if (StringUtils.hasText(paramVo.getKeyword())) {
                HighlightField highlightField = hit.getHighlightFields().get("skuTitle");
                skuEsTo.setSkuTitle(highlightField.fragments()[0].toString());
            }
            list.add(skuEsTo);
        }
        //商品
        vo.setProducts(list);

        long value = searchHits.getTotalHits().value;
        //总数
        vo.setTotal(value);
        //总页码
        vo.setTotalPages(value % EsConstant.PRODUCT_PAGESIZE == 0 ? (int) value / EsConstant.PRODUCT_PAGESIZE : (int) (value / EsConstant.PRODUCT_PAGESIZE) + 1);
        //页码
        vo.setPageNum(paramVo.getPageNum());
        //页码集
        List<Integer> totalPagesList = new ArrayList<>();
        for (Integer i = 1; i <= vo.getTotalPages(); i++) {
            totalPagesList.add(i);
        }
        vo.setTotalPagesList(totalPagesList);

        //聚合
        Aggregations aggregations = search.getAggregations();
        ParsedLongTerms catalog_agg = aggregations.get("catalog_agg");
        List<? extends Terms.Bucket> buckets = catalog_agg.getBuckets();
        List<SearchRespVo.CategoryVo> categoryVoList = new ArrayList<SearchRespVo.CategoryVo>();
        for (Terms.Bucket bucket : buckets) {
            ParsedStringTerms catalogName_agg = bucket.getAggregations().get("catalogName_agg");

            SearchRespVo.CategoryVo categoryVo = new SearchRespVo.CategoryVo();
            categoryVo.setCatalogId(Long.parseLong(bucket.getKeyAsString()));
            categoryVo.setCatalogName(catalogName_agg.getBuckets().get(0).getKeyAsString());
            categoryVoList.add(categoryVo);
        }
        //分类
        vo.setCategorys(categoryVoList);

        //属性
        ParsedNested attr_agg = aggregations.get("attr_agg");
        Aggregations attrAggAggregations = attr_agg.getAggregations();
        ParsedLongTerms attrIdAggAggregation = attrAggAggregations.get("attrId_agg");
        List<? extends Terms.Bucket> attrIdAggAggregationBuckets = attrIdAggAggregation.getBuckets();
        List<SearchRespVo.Attr> attrList = new ArrayList<>();
        for (Terms.Bucket bucket : attrIdAggAggregationBuckets) {
            Aggregations bucketAggregations = bucket.getAggregations();
            ParsedStringTerms attrName_agg = bucketAggregations.get("attrName_agg");
            ParsedStringTerms attrValue_agg = bucketAggregations.get("attrValue_agg");

            SearchRespVo.Attr attr = new SearchRespVo.Attr();
            attr.setAttrId(Long.parseLong(bucket.getKeyAsString()));
            attr.setAttrName(attrName_agg.getBuckets().get(0).getKeyAsString());
            List<String> attrValueList = attrValue_agg.getBuckets().stream().map(data -> data.getKeyAsString()).collect(Collectors.toList());
            attr.setAttrValue(attrValueList);
            attrList.add(attr);
        }
        vo.setAttrs(attrList);

        ParsedLongTerms brand_agg = aggregations.get("brand_agg");
        List<? extends Terms.Bucket> brandAggBuckets = brand_agg.getBuckets();
        List<SearchRespVo.BrandVo> brandVoList = new ArrayList<>();
        for (Terms.Bucket brandAggBucket : brandAggBuckets) {
            Aggregations bucketAggregations = brandAggBucket.getAggregations();
            ParsedStringTerms brandImg_agg = bucketAggregations.get("brandImg_agg");
            ParsedStringTerms brandName_agg = bucketAggregations.get("brandName_agg");

            SearchRespVo.BrandVo brandVo = new SearchRespVo.BrandVo();
            brandVo.setBrandId(Long.parseLong(brandAggBucket.getKeyAsString()));
            brandVo.setBrandImg(brandImg_agg.getBuckets().get(0).getKeyAsString());
            brandVo.setBrandName(brandName_agg.getBuckets().get(0).getKeyAsString());
            brandVoList.add(brandVo);
        }
        //品牌
        vo.setBrands(brandVoList);

        //面包屑导航
        if (!CollectionUtils.isEmpty(paramVo.getAttrs())) {
            List<Long> attrIds = new ArrayList<>();
            List<SearchRespVo.NavVo> navVos = paramVo.getAttrs().stream().map(attr -> {
                SearchRespVo.NavVo navVo = new SearchRespVo.NavVo();
                String[] s = attr.split("_");
                navVo.setNavValue(s[1]);

                R r = productFeignService.attrInfo(Long.parseLong(s[0]));
                vo.getAttrIds().add(Long.parseLong(s[0]));
                if (r.getCode() == 0) {
                    SearchRespVo.Attr attrVo = r.getDataByKey("attr", new TypeReference<SearchRespVo.Attr>() {
                    });
                    navVo.setNavName(attrVo.getAttrName());
                }
                String replace = getQueryString(paramVo, attr, "attrs");

                navVo.setLink(AuthServerConstant.SEARCH_PAGE +"/?" + replace);

                return navVo;
            }).collect(Collectors.toList());

            vo.setNavs(navVos);
        }

        if (!CollectionUtils.isEmpty(paramVo.getBrandId())) {
            //vo.getNavs()可能为空，需要设置默认值new ArrayList<>();
            List<SearchRespVo.NavVo> navs = vo.getNavs();
            SearchRespVo.NavVo navVo = new SearchRespVo.NavVo();
            navVo.setNavName("品牌");

            R r = productFeignService.getByBrandIds(paramVo.getBrandId());
            if (r.getCode() == 0) {
                List<BrandTo> brandTos = r.getDataByKey("brands", new TypeReference<List<BrandTo>>() {
                });
                StringBuffer sb = new StringBuffer();
                String replace = "";
                for (BrandTo brandTo : brandTos) {
                    sb.append(brandTo.getName() + ";");
                    replace = getQueryString(paramVo, brandTo.getBrandId() + "", "brandId");
                }

                navVo.setNavValue(sb.toString());
                navVo.setLink(AuthServerConstant.SEARCH_PAGE +"/?" + replace);
                navs.add(navVo);
            }
        }

        return vo;
    }

    private String getQueryString(SearchParamVo paramVo, String attr, String key) {
        String attrParam = "";
        try {
            attrParam = URLEncoder.encode(attr, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String replace = paramVo.get_queryString().replaceAll("&" + key + "=" + attrParam, "");
        return replace;
    }
}
