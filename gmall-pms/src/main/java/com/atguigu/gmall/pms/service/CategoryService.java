package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.vo.CategoryEntityVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;

import java.util.List;


/**
 * 商品三级分类
 *
 * @author yuanxin
 * @email a327952016@qq.com
 * @date 2019-12-02 18:47:02
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageVo queryPage(QueryCondition params);

    List<CategoryEntityVo> querySubCategory(Long pid);
}

