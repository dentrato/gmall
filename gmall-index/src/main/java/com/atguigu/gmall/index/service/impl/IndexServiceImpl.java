package com.atguigu.gmall.index.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.index.feign.GmallPmsFeign;
import com.atguigu.gmall.index.service.IndexService;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.vo.CategoryEntityVo;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class IndexServiceImpl implements IndexService {
    @Autowired
    private GmallPmsFeign gmallPmsFeign;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    RedissonClient redissonClient;

    private static final String KEY_PREFIX="INDEX:CATEGORY";

    @Override
    public List<CategoryEntity> queryLevelOneCategory() {
        Resp<List<CategoryEntity>> listResp = gmallPmsFeign.queryCategoryBypidOrLevel(1, null);
        List<CategoryEntity> data = listResp.getData();
        return data;
    }

    @Override
    public List<CategoryEntityVo> querySubsCategory(Long pid) {
        //解决缓存雪崩，击穿，穿透
        //判断缓存中是否有数据
        String cateJson = redisTemplate.opsForValue().get(KEY_PREFIX + pid);
        //有，直接返回
        if (!StringUtils.isEmpty(cateJson)){
            return JSON.parseArray(cateJson,CategoryEntityVo.class);
        }

        RLock lock = this.redissonClient.getLock("lock" + pid);
        lock.lock();
        //判断缓存中是否有数据
        String cateJson2 = redisTemplate.opsForValue().get(KEY_PREFIX + pid);
        //有，直接返回
        if (!StringUtils.isEmpty(cateJson2)){
            lock.unlock();
            return JSON.parseArray(cateJson2,CategoryEntityVo.class);
        }
        //没有，查询数据库
        Resp<List<CategoryEntityVo>> listResp = this.gmallPmsFeign.querySubCategory(pid);
        List<CategoryEntityVo> categoryVOS = listResp.getData();
        //查询完成后放入缓存
        this.redisTemplate.opsForValue().set(KEY_PREFIX+pid,JSON.toJSONString(categoryVOS),7+new Random().nextInt(7), TimeUnit.DAYS);
        lock.unlock();
        return listResp.getData();
    }


}
