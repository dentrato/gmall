package com.atguigu.gmall.ums.dao;

import com.atguigu.gmall.ums.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author yuanxin
 * @email a327952016@qq.com
 * @date 2019-12-02 19:55:31
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
