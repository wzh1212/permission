package com.xmcc.dao;

import com.xmcc.model.SysAclModule;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysAclModuleMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysAclModule record);

    int insertSelective(SysAclModule record);

    SysAclModule selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysAclModule record);

    int updateByPrimaryKey(SysAclModule record);

    // 检查是否存在该权限名
    int countByParentIdAndAclModuleName(@Param("parentId") Integer parentId, @Param("aclModuleName")String aclModuleName, @Param("aclModuleId")Integer aclModuleId);

    // 获取当前所有的权限
    List<SysAclModule> findAllAclModlue();

    // 通过 Level 获取所有的子权限
    List<SysAclModule> getChildAclModuleListByLevel(@Param("level") String level);
}