package com.atguigu.gmall.sms.service;

import com.atguigu.gmall.sms.Vo.SkuSaveSale;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.sms.entity.SkuBoundsEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;


/**
 * 商品sku积分设置
 *
 * @author yuanxin
 * @email a327952016@qq.com
 * @date 2019-12-02 20:04:08
 */
public interface SkuBoundsService extends IService<SkuBoundsEntity> {

    PageVo queryPage(QueryCondition params);

    void savesale(SkuSaveSale skuSaveSale);
}

