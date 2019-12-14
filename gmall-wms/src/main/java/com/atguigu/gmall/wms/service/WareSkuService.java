package com.atguigu.gmall.wms.service;

import com.atguigu.core.bean.QueryCondition;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.atguigu.core.bean.PageVo;


/**
 * 商品库存
 *
 * @author yuanxin
 * @email a327952016@qq.com
 * @date 2019-12-02 19:38:47
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageVo queryPage(QueryCondition params);
}

