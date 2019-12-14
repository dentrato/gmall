package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.Vo.SpuVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.pms.entity.SpuInfoEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;


/**
 * spu信息
 *
 * @author yuanxin
 * @email a327952016@qq.com
 * @date 2019-12-02 18:47:02
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageVo queryPage(QueryCondition params);

    PageVo querySpuPage(QueryCondition condition, Long cid);

    void bigsave(SpuVo spuVo);
}

