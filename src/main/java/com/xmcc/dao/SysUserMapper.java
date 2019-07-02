package com.xmcc.dao;

import com.xmcc.beans.PageBean;
import com.xmcc.model.SysUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysUserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysUser record);

    int insertSelective(SysUser record);

    SysUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysUser record);

    int updateByPrimaryKey(SysUser record);

    // 根据 姓名 查找，判断之后，进行登录
    SysUser findByName(@Param("username") String username);

    // 检查当前部门下是否存在用户
    int countByDeptId(@Param("deptId")int deptId);

    // 根据 deptId 查询部门下的用户
    List<SysUser> getUserPageByDeptId(@Param("deptId")int deptId, @Param("pageBean")PageBean<SysUser> pageBean);

    // 根据 电话号码，验证是否存在该用户
    int countByUserIdAndUserNam(@Param("deptId") Integer deptId,@Param("telephone") String telephone,@Param("userId")Integer userId);

    List<SysUser> getByIdList(@Param("userIdList") List<Integer> userIdList);

    List<SysUser> getAll();
}