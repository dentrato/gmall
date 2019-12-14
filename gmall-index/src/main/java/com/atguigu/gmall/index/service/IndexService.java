package com.atguigu.gmall.index.service;

import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.vo.CategoryEntityVo;

import java.util.List;

public interface IndexService {
    List<CategoryEntity> queryLevelOneCategory();

    List<CategoryEntityVo> querySubsCategory(Long pid);

//    List<CategoryEntityVo> querySubsCategory(Long pid);
}
