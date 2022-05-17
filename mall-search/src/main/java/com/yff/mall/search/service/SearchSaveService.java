package com.yff.mall.search.service;

import com.yff.common.to.es.SkuEsTo;

import java.io.IOException;
import java.util.List;

/**
 * @author yanfeifan
 * @Package com.yff.mall.search.service
 * @Description
 * @date 2022/1/4 21:35
 */

public interface SearchSaveService {

    boolean saveProduct(List<SkuEsTo> list) throws IOException;
}
