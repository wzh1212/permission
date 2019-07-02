package com.xmcc.dao;

import com.xmcc.model.SysRoleAcl;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.List;

public interface SysRoleAclMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysRoleAcl record);

    int insertSelective(SysRoleAcl record);

    SysRoleAcl selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysRoleAcl record);

    int updateByPrimaryKey(SysRoleAcl record);

    // 通过 角色 id 查询 对应的 权限点 id
    List<Integer> getAclIdListByRoleId(@Param("userRoleIdList") List<Integer> userRoleIdList);

    // 根据 roleId 删除 该 roleId 对应的所有权限
    void deleteByRoleId(@Param("roleId") Integer roleId);

    // 批量新增
    void bathInsert(@Param("sysRoleAcls") ArrayList<SysRoleAcl> sysRoleAcls);
}