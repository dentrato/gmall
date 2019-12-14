package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.Vo.GroupVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;

import java.util.List;


/**
 * 属性分组
 *
 * @author yuanxin
 * @email a327952016@qq.com
 * @date 2019-12-02 18:47:02
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageVo queryPage(QueryCondition params);

    PageVo queryGroupByPage(QueryCondition condition, Long catId);

    GroupVo queryGroupWithAttrByGid( Long gid);

    List<GroupVo> queryGroupAttrWithCid(Long cid);
}

