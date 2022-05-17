package com.yff.mall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.yff.common.to.es.SkuEsTo;
import com.yff.mall.search.config.MallElasticSearchConfig;
import com.yff.mall.search.constant.EsConstant;
import com.yff.mall.search.service.SearchSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yanfeifan
 * @Package com.yff.mall.search.service.impl
 * @Description
 * @date 2022/1/4 21:36
 */
@Service
@Slf4j
public class SearchSaveServiceImpl implements SearchSaveService {

    @Autowired
    private RestHighLevelClient client;

    @Override
    public boolean saveProduct(List<SkuEsTo> list) throws IOException {

        BulkRequest bulkRequest = new BulkRequest();

        for (SkuEsTo skuEsTo : list) {
            IndexRequest index = new IndexRequest(EsConstant.PRODUCT_INDEX);
            index.id(skuEsTo.getSkuId().toString());
            index.source(JSON.toJSONString(skuEsTo), XContentType.JSON);
            bulkRequest.add(index);
        }
        //批量存储
        BulkResponse bulk = client.bulk(bulkRequest, MallElasticSearchConfig.COMMON_OPTIONS);

        boolean b = bulk.hasFailures();

        List<String> collect = Arrays.stream(bulk.getItems()).map(data -> data.getId()).collect(Collectors.toList());
        log.error("商品上架成功,{}", collect);

        return !b;
    }
}
