package com.atguigu.gmall.search.service;

import com.atguigu.gmall.search.pojo.SearchParamVo;
import com.atguigu.gmall.search.pojo.SearchResponseVO;

import java.io.IOException;

public interface SearchService {
    SearchResponseVO search(SearchParamVo searchParamVo) throws IOException;
}
