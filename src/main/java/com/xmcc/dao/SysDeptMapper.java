package com.xmcc.dao;

import com.xmcc.model.SysDept;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysDeptMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysDept record);

    int insertSelective(SysDept record);

    SysDept selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysDept record);

    int updateByPrimaryKey(SysDept record);

    // 添加部门时检查该层级下是否重复
    int countByParentIdAndDeptName(@Param("parentId") Integer parentId, @Param("deptName")String deptName, @Param("deptId")Integer deptId);

    // 获取当前所有的部门
    List<SysDept> findAll();

    // 通过 Level 获取所有的子部门
    List<SysDept> getChildDeptListByLevel(@Param("level") String level);
}