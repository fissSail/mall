package com.yff.mall.search.service;

import com.yff.mall.search.vo.SearchParamVo;
import com.yff.mall.search.vo.SearchRespVo;

/**
 * @author yanfeifan
 * @Package com.yff.mall.search.service
 * @Description
 * @date 2022/1/18 13:56
 */

public interface MallSearchService {

    SearchRespVo search(SearchParamVo vo);
}
