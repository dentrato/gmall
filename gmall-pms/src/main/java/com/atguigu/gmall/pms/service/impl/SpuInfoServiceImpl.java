package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.Vo.BaseAttrValue;
import com.atguigu.gmall.pms.Vo.SkuVo;
import com.atguigu.gmall.pms.Vo.SpuVo;
import com.atguigu.gmall.pms.dao.SkuInfoDao;
import com.atguigu.gmall.pms.dao.SpuInfoDescDao;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.feign.SaveSale;
import com.atguigu.gmall.pms.service.ProductAttrValueService;
import com.atguigu.gmall.pms.service.SkuImagesService;
import com.atguigu.gmall.pms.service.SkuSaleAttrValueService;
import com.atguigu.gmall.sms.Vo.SkuSaveSale;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.pms.dao.SpuInfoDao;
import com.atguigu.gmall.pms.service.SpuInfoService;
import org.springframework.util.CollectionUtils;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private SpuInfoDescDao SpuInfoDescDao;
    @Autowired
    private ProductAttrValueService productAttrValueService;
    @Autowired
    private SkuInfoDao skuInfoDao;
    @Autowired
    SkuImagesService skuImagesService;
    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    SaveSale saveSale;


    @Autowired
    AmqpTemplate amqpTemplate;
    @Value("${item.rabbitmq.exchange}")
    private String EXCHANGE_NAME;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public PageVo querySpuPage(QueryCondition condition, Long cid) {
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
        if (cid != 0) {
            wrapper.eq("catalog_id",cid);
        }

        String key = condition.getKey();
        if (StringUtils.isNotBlank(key)){
            wrapper.and(t->t.eq("id",key).or().like("spu_name",key));
        }

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(condition),
                wrapper
        );

        return new PageVo(page);
    }
    @GlobalTransactional
    @Override
    public void bigsave(SpuVo spuVo) {

        //保存spu相关的三张表信息
        //1.pms_spu_info
        spuVo.setCreateTime(new Date());
        spuVo.setUodateTime(spuVo.getCreateTime());
        this.save(spuVo);
        Long spuId = spuVo.getId();

        //2.pms_spu_info_desc
        List<String> spuImages = spuVo.getSpuImages();
        if (!CollectionUtils.isEmpty(spuImages)) {
            SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
            spuInfoDescEntity.setSpuId(spuId);
            spuInfoDescEntity.setDecript(StringUtils.join(spuImages, ","));
            SpuInfoDescDao.insert(spuInfoDescEntity);
        }
        //3.pms_product_attr_value
        List<BaseAttrValue> baseAttrs = spuVo.getBaseAttrs();
        if (!CollectionUtils.isEmpty(baseAttrs)){
            List<ProductAttrValueEntity> attrValueEntities=baseAttrs.stream().map(t->{
                ProductAttrValueEntity productAttrValueEntity=t;
                productAttrValueEntity.setSpuId(spuId);
                return productAttrValueEntity;
            }).collect(Collectors.toList());
            this.productAttrValueService.saveBatch(attrValueEntities);
        }
        //保存sku的相关三张表的信息
        //1.pms_sku_info
        List<SkuVo> skus = spuVo.getSkus();
        if (CollectionUtils.isEmpty(skus)){
            return;
        }
        skus.stream().forEach(t->{
            t.setSpuId(spuId);
            t.setSkuCode(UUID.randomUUID().toString().replace("-",""));
            t.setBrandId(spuVo.getBrandId());
            t.setCatalogId(spuVo.getCatalogId());
            List<String> images = t.getImages();
            //设置默认图片
            if (CollectionUtils.isEmpty(images)){
                t.setSkuDefaultImg(StringUtils.isNotBlank(t.getSkuDefaultImg())?t.getSkuDefaultImg():images.get(0));
            }
            skuInfoDao.insert(t);
            Long skuId = t.getSkuId();
            //2.pms_sku_images
            if (!CollectionUtils.isEmpty(images)){
                List<SkuImagesEntity> skuImagesEntities=images.stream().map(image->{
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setImgUrl(image);
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setDefaultImg(StringUtils.equals(t.getSkuDefaultImg(),image)?1:0);
                    return skuImagesEntity;
                }).collect(Collectors.toList());
                this.skuImagesService.saveBatch(skuImagesEntities);
            }
            //3.pms_sku_sale_attr_value
            List<SkuSaleAttrValueEntity> saleAttrs = t.getSaleAttrs();
            if (!CollectionUtils.isEmpty(saleAttrs)){
                saleAttrs.stream().forEach(saleattr->saleattr.setSkuId(skuId));
                skuSaleAttrValueService.saveBatch(saleAttrs);
            }
            //保存sms相关的三张表信息
            //1.sms_sku_bounds
            SkuSaveSale skuSaveSale = new SkuSaveSale();
            BeanUtils.copyProperties(t,skuSaveSale);
            skuSaveSale.setSkuId(t.getSkuId());
            saveSale.savesale(skuSaveSale);
            //2.sms_sku_ladder
            //3.sms_sku_full_reduction
            sendMsg("insert",spuId);
        });
    }
    public void sendMsg(String type,Long spuId){
        amqpTemplate.convertAndSend(EXCHANGE_NAME,"item."+type,spuId);
    }
}