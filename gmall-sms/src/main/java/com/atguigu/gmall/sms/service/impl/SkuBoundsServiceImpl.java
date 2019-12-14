package com.atguigu.gmall.sms.service.impl;

import com.atguigu.gmall.sms.Vo.SkuSaveSale;
import com.atguigu.gmall.sms.dao.SkuFullReductionDao;
import com.atguigu.gmall.sms.dao.SkuLadderDao;
import com.atguigu.gmall.sms.entity.SkuFullReductionEntity;
import com.atguigu.gmall.sms.entity.SkuLadderEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.sms.dao.SkuBoundsDao;
import com.atguigu.gmall.sms.entity.SkuBoundsEntity;
import com.atguigu.gmall.sms.service.SkuBoundsService;
import org.springframework.transaction.annotation.Transactional;


@Service("skuBoundsService")
public class SkuBoundsServiceImpl extends ServiceImpl<SkuBoundsDao, SkuBoundsEntity> implements SkuBoundsService {

    @Autowired
    SkuLadderDao skuLadderDao;
    @Autowired
    SkuFullReductionDao skuFullReductionDao;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SkuBoundsEntity> page = this.page(
                new Query<SkuBoundsEntity>().getPage(params),
                new QueryWrapper<SkuBoundsEntity>()
        );

        return new PageVo(page);
    }
    @Transactional
    @Override
    public void savesale(SkuSaveSale skuSaveSale) {
        SkuBoundsEntity skuBoundsEntity = new SkuBoundsEntity();
        skuBoundsEntity.setSkuId(skuSaveSale.getSkuId());
        skuBoundsEntity.setBuyBounds(skuSaveSale.getBuyBounds());
        skuBoundsEntity.setGrowBounds(skuSaveSale.getGrowBounds());
        List<Integer> work = skuSaveSale.getWork();
        skuBoundsEntity.setWork(work.get(3)*1+work.get(2)*2+work.get(1)*3+work.get(0)*8);
        this.save(skuBoundsEntity);

        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        skuLadderEntity.setSkuId(skuSaveSale.getSkuId());
        skuLadderEntity.setDiscount(skuSaveSale.getDiscount());
        skuLadderEntity.setFullCount(skuSaveSale.getFullCount());
        skuLadderEntity.setAddOther(skuSaveSale.getLadderAddOther());
        skuLadderDao.insert(skuLadderEntity);

        SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
        skuFullReductionEntity.setSkuId(skuSaveSale.getSkuId());
        skuFullReductionEntity.setFullPrice(skuSaveSale.getFullPrice());
        skuFullReductionEntity.setAddOther(skuSaveSale.getFullAddOther());
        skuFullReductionEntity.setReducePrice(skuSaveSale.getReducePrice());
        skuFullReductionDao.insert(skuFullReductionEntity);
    }

}