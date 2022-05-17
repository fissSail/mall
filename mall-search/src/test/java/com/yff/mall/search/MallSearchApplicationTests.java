package com.yff.mall.search;

import com.alibaba.fastjson.JSON;
import com.yff.common.to.es.SkuEsTo;
import com.yff.mall.search.config.MallElasticSearchConfig;
import lombok.Data;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class MallSearchApplicationTests {
    @Autowired
    private RestHighLevelClient client;

    @Test
    void contextLoads() {
        System.out.println(client);
    }


    @Test
    void index() throws IOException {
        IndexRequest index = new IndexRequest("user");
        index.id("1");
        //index.source("userNmae","张三","age",18,"gender","男");

        User user = new User();
        user.setId(1);
        user.setUserName("张三");
        user.setAge(18);
        String o = JSON.toJSONString(user);
        index.source(o, XContentType.JSON);

        IndexResponse indexResponse = client.index(index, MallElasticSearchConfig.COMMON_OPTIONS);

        System.out.println(indexResponse);

    }

    @Data
    class User{
        private Integer id;
        private String userName;
        private Integer age;
    }

    @Data
    static class BankEntity {
        private int account_number;

        private int balance;

        private String firstname;

        private String lastname;

        private int age;

        private String gender;

        private String address;

        private String employer;

        private String email;

        private String city;

        private String state;
    }

    @Test
    void searchData() throws IOException {
        //创建检索请求
        SearchRequest searchRequest = new SearchRequest();
        //指定索引
        searchRequest.indices("bank");
        //指定DSL检索条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.matchQuery("address","mill"));
        builder.from(0);
        builder.size(5);

        //按照年龄的值分布进行聚合
        builder.aggregation(AggregationBuilders.terms("ageAgg").field("age").size(10));

        builder.aggregation(AggregationBuilders.avg("balanceAvg").field("balance"));
        System.out.println(builder.toString());
        searchRequest.source(builder);
        //执行检索
        SearchResponse response = client.search(searchRequest, MallElasticSearchConfig.COMMON_OPTIONS);

        //获取结果
        System.out.println(response.toString());
        //获取所有查到的数据
        SearchHits hits = response.getHits();

        SearchHit[] hitArr = hits.getHits();

        for (SearchHit hit : hitArr) {
            String str = hit.getSourceAsString();
            BankEntity bankEntity = JSON.parseObject(str, BankEntity.class);
            System.out.println(bankEntity.getAge());
        }

        Aggregations aggregations = response.getAggregations();

        Terms ageAgg = aggregations.get("ageAgg");
        for (Terms.Bucket bucket : ageAgg.getBuckets()) {
            String keyAsString = bucket.getKeyAsString();
            System.out.println(keyAsString+"===="+bucket.getDocCount());
        }

        Avg balanceAvg = aggregations.get("balanceAvg");
        System.out.println(balanceAvg.getValue());


    }

    @Test
    void searchProduct() throws IOException {
        //创建检索请求
        SearchRequest searchRequest = new SearchRequest();
        //指定索引
        searchRequest.indices("product");
        //指定DSL检索条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.matchQuery("skuTitle","华为"));
        searchRequest.source(builder);

        //执行检索
        SearchResponse response = client.search(searchRequest, MallElasticSearchConfig.COMMON_OPTIONS);

        //获取结果
        System.out.println(response.toString());
        //获取所有查到的数据
        SearchHits hits = response.getHits();

        SearchHit[] hitArr = hits.getHits();

        for (SearchHit hit : hitArr) {
            String str = hit.getSourceAsString();
            SkuEsTo skuEsTo = JSON.parseObject(str, SkuEsTo.class);

            System.out.println(skuEsTo.toString());
        }
    }

}
