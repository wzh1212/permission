package com.xmcc.dao;

import com.xmcc.model.SysRoleUser;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.List;

public interface SysRoleUserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysRoleUser record);

    int insertSelective(SysRoleUser record);

    SysRoleUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysRoleUser record);

    int updateByPrimaryKey(SysRoleUser record);

    // 通过 用户 id 获取用户拥有的角色 id
    List<Integer> getRoleIdListByUserId(@Param("userId") Integer userId);

    // 通过 角色 id 获取所有的 用户id
    List<Integer> getUserIdListByRoleId(@Param("roleId") Integer roleId);

    // 根据 roleId 进行删除
    void deleteByRoleId(@Param("roleId") Integer roleId);

    void bathInsert(@Param("list") ArrayList<SysRoleUser> list);
}