package com.atguigu.gmall.oms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.oms.entity.OrderOperateHistoryEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;


/**
 * 订单操作历史记录
 *
 * @author yuanxin
 * @email a327952016@qq.com
 * @date 2019-12-02 20:12:39
 */
public interface OrderOperateHistoryService extends IService<OrderOperateHistoryEntity> {

    PageVo queryPage(QueryCondition params);
}

