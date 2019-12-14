package com.atguigu.gmall.search.Controller;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.search.pojo.SearchParamVo;
import com.atguigu.gmall.search.pojo.SearchResponseVO;
import com.atguigu.gmall.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("search")
public class SearchController {
    @Autowired
    SearchService searchService;
    @GetMapping
    public Resp<SearchResponseVO> search(SearchParamVo searchParamVo) throws IOException {
        SearchResponseVO search = this.searchService.search(searchParamVo);
        return Resp.ok(search);
    }
}
