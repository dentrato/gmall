package com.atguigu.gmall.index.controller;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.index.service.IndexService;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.vo.CategoryEntityVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("index")
public class IndexController {

    @Autowired
    IndexService indexService;

    @GetMapping("cates")
    public Resp<List<CategoryEntity>> queryLevelOneCategory(){
        List<CategoryEntity> categories= this.indexService.queryLevelOneCategory();
        return Resp.ok(categories);
    }

    @GetMapping("cates/{pid}")
    public Resp<List<CategoryEntityVo>> querySubsCategory(@PathVariable("pid") Long pid){
        List<CategoryEntityVo> categoryEntityVos= indexService.querySubsCategory(pid);
        return Resp.ok(categoryEntityVos);
    }
}
