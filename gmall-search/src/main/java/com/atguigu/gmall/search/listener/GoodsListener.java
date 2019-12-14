package com.atguigu.gmall.search.listener;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.search.feign.GmallPmsClient;
import com.atguigu.gmall.search.feign.GmallWmsClient;
import com.atguigu.gmall.search.pojo.Goods;
import com.atguigu.gmall.search.pojo.SearchAttr;
import com.atguigu.gmall.search.repository.GoodsRepository;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GoodsListener {

    @Autowired
    GmallPmsClient pmsClient;
    @Autowired
    GmallWmsClient wmsClient;
    @Autowired
    GoodsRepository goodsRepository;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "gmall-search-queue",durable = "true"),
            exchange = @Exchange(value = "GMALL-PMS-EXCHANGE",type = ExchangeTypes.TOPIC,ignoreDeclarationExceptions = "true"),
            key = {"item.insert","item.update"}
    ))
    public void listner(Long spuId){
        Resp<List<SkuInfoEntity>> skuResp = this.pmsClient.querySkusBySpuid(spuId);
        //把sku转化成goods对象
        List<SkuInfoEntity> skuInfoEntities = skuResp.getData();
        if (!CollectionUtils.isEmpty(skuInfoEntities)){
            List<Goods> goodsList = skuInfoEntities.stream().map(skuInfoEntity -> {
                Goods goods = new Goods();
                //查询搜索属性及值
                Resp<List<ProductAttrValueEntity>> searchAttrValues = this.pmsClient.querySearchAttrValues(spuId);
                List<ProductAttrValueEntity> listRespData = searchAttrValues.getData();
                if (!CollectionUtils.isEmpty(listRespData)){
                    List<SearchAttr> searchAttrs = listRespData.stream().map(attrValueEntity -> {
                        SearchAttr searchAttr = new SearchAttr();
                        searchAttr.setAttrId(attrValueEntity.getAttrId());
                        searchAttr.setAttrName(attrValueEntity.getAttrName());
                        searchAttr.setAttrValue(attrValueEntity.getAttrValue());
                        return searchAttr;
                    }).collect(Collectors.toList());
                    goods.setAttrs(searchAttrs);
                }
                //查询并设置品牌
                Resp<BrandEntity> brandEntityResp = this.pmsClient.queryBrandById(skuInfoEntity.getBrandId());
                BrandEntity brandEntity = brandEntityResp.getData();
                if (brandEntity != null) {
                    goods.setBrandId(skuInfoEntity.getBrandId());
                    goods.setBrandName(brandEntity.getName());
                }

                //查询并设置分类
                Resp<CategoryEntity> categoryEntityResp = this.pmsClient.queryCategoryById(skuInfoEntity.getCatalogId());
                CategoryEntity categoryEntity = categoryEntityResp.getData();
                if (categoryEntity != null) {
                    goods.setCategoryId(skuInfoEntity.getCatalogId());
                    goods.setCategoryName(categoryEntity.getName());
                }
                Resp<SpuInfoEntity> spuInfoEntityResp = this.pmsClient.querySpuById(spuId);
                SpuInfoEntity spuInfoEntity = spuInfoEntityResp.getData();
                goods.setCreateTime(spuInfoEntity.getCreateTime());
                goods.setPic(skuInfoEntity.getSkuDefaultImg());

                goods.setPrice(skuInfoEntity.getPrice().doubleValue());

                goods.setSale(0L);
                goods.setSkuId(skuInfoEntity.getSkuId());
                //查询库存信息
                Resp<List<WareSkuEntity>> listResp = this.wmsClient.querySkuBySkuid(skuInfoEntity.getSkuId());
                List<WareSkuEntity> wareSkuEntities = listResp.getData();
                if (CollectionUtils.isEmpty(wareSkuEntities)){
                    boolean flag = wareSkuEntities.stream().anyMatch(wareSkuEntity -> wareSkuEntity.getStock() > 0);
                    goods.setStore(flag);
                }
                goods.setTitle(skuInfoEntity.getSkuTitle());

                return goods;
            }).collect(Collectors.toList());

            this.goodsRepository.saveAll(goodsList);
        }
    }
}
