package com.atguigu.gmall.search;

import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.search.feign.GmallPmsClient;
import com.atguigu.gmall.search.feign.GmallWmsClient;
import com.atguigu.gmall.search.pojo.Goods;
import com.atguigu.gmall.search.pojo.SearchAttr;
import com.atguigu.gmall.search.repository.GoodsRepository;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
class GmallSearchApplicationTests {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Test
    void contextLoads() {
        this.elasticsearchRestTemplate.createIndex(Goods.class);
        this.elasticsearchRestTemplate.putMapping(Goods.class);
    }
    @Autowired
    GmallPmsClient pmsClient;
    @Autowired
    GmallWmsClient wmsClient;
    @Autowired
    GoodsRepository goodsRepository;
    @Test
    void impoData(){
        Long pageNum=1L;
        Long pageSize=100L;
        do {
            QueryCondition queryCondition = new QueryCondition();
            queryCondition.setPage(pageNum);
            queryCondition.setLimit(pageSize);
            Resp<List<SpuInfoEntity>> spusByPage = this.pmsClient.querySpusByPage(queryCondition);
            List<SpuInfoEntity> spus = spusByPage.getData();
            //分页查询spu
            spus.forEach(spuInfoEntity -> {
                Resp<List<SkuInfoEntity>> skuResp = this.pmsClient.querySkusBySpuid(spuInfoEntity.getId());
                //把sku转化成goods对象
                List<SkuInfoEntity> skuInfoEntities = skuResp.getData();
                if (!CollectionUtils.isEmpty(skuInfoEntities)){
                    List<Goods> goodsList = skuInfoEntities.stream().map(skuInfoEntity -> {
                        Goods goods = new Goods();
                        //查询搜索属性及值
                        Resp<List<ProductAttrValueEntity>> searchAttrValues = this.pmsClient.querySearchAttrValues(spuInfoEntity.getId());
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
            });
            //遍历spu，查询所对应的sku



            //导入索引库
            pageSize=(long)spus.size();
            pageNum++;
        } while (pageSize==100);
    }
}
