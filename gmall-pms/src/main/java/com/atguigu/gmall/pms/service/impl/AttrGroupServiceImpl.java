package com.atguigu.gmall.pms.service.impl;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.Vo.GroupVo;
import com.atguigu.gmall.pms.dao.AttrAttrgroupRelationDao;
import com.atguigu.gmall.pms.dao.AttrDao;
import com.atguigu.gmall.pms.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gmall.pms.entity.AttrEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.pms.dao.AttrGroupDao;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import com.atguigu.gmall.pms.service.AttrGroupService;
import org.springframework.util.CollectionUtils;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    AttrAttrgroupRelationDao attrAttrgroupRelationDao;
    @Autowired
    private AttrDao attrDao;
    
    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public PageVo queryGroupByPage(QueryCondition condition, Long catId) {
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<>();
        if (catId != null) {
            wrapper.eq("catelog_id",catId);
        }
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(condition),
                wrapper
        );

        return new PageVo(page);
    }

    @Override
    public GroupVo queryGroupWithAttrByGid(Long gid) {
        GroupVo groupVo = new GroupVo();

        AttrGroupEntity groupEntity = getById(gid);
        BeanUtils.copyProperties(groupEntity,groupVo);
        List<AttrAttrgroupRelationEntity> relations = attrAttrgroupRelationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", gid));
        if(CollectionUtils.isEmpty(relations)){
            return groupVo;
        }
        groupVo.setRelations(relations);
        List<Long> attrIds = relations.stream().map(relationEntities -> relationEntities.getAttrId()).collect(Collectors.toList());
        List<AttrEntity> attrEntities = attrDao.selectBatchIds(attrIds);
        groupVo.setAttrEntities(attrEntities);
        return groupVo;
    }

    @Override
    public List<GroupVo> queryGroupAttrWithCid(Long cid) {
        //根绝cid查询三级目录下的所有属性分组
        List<AttrGroupEntity> groupEntities=list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id",cid));
        //1.根据分组中的id查询中间表
        //2.根绝中间表attrids查询参数
        //3.数据类型的转化 ：attrgroupEntites->groupVo
        return groupEntities.stream().map(t->this.queryGroupWithAttrByGid(t.getAttrGroupId())).collect(Collectors.toList());
    }

}