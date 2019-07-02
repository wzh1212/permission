package com.xmcc.dao;

import com.xmcc.model.SysRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysRoleMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysRole record);

    int insertSelective(SysRole record);

    SysRole selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysRole record);

    int updateByPrimaryKey(SysRole record);

    // 查询所有角色
    List<SysRole> findAll();

    // 根据 name 判断角色是否重复
    int countByRoleName(@Param("roleName") String roleName);


}