package com.xmcc.dao;

import com.xmcc.beans.PageBean;
import com.xmcc.model.SysAcl;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysAclMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysAcl record);

    int insertSelective(SysAcl record);

    SysAcl selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysAcl record);

    int updateByPrimaryKey(SysAcl record);

    // 检查当前权限下是否存在权限点
    int countByAclModuleId(@Param("aclModuleId") int aclModuleId);

    // 根据 aclModuleId 查询权限下的权限点
    List<SysAcl> getAclPageByAclModuleId(@Param("aclModuleId") int aclModuleId, @Param("pageBean") PageBean<SysAcl> pageBean);

    // 检查是否存在该权限点
    int countByAclIdAndAclName(@Param("aclModuleId")Integer aclModuleId,@Param("aclName") String aclName, @Param("aclId")Integer aclId);

    // 返回 所有的 权限点
    List<SysAcl> getAll();

    // 通过 权限点 id 查询 权限对象
    List<SysAcl> getByIdList(@Param("userAclIdList") List<Integer> userAclIdList);

    // 通过 URL 查询对应的权限点
    SysAcl getByUrl(@Param("url") String url);
}