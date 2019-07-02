package com.xmcc.dao;

import com.xmcc.beans.PageBean;
import com.xmcc.dto.SearchLogDto;
import com.xmcc.model.SysLog;
import com.xmcc.model.SysLogWithBLOBs;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysLogMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysLogWithBLOBs record);

    int insertSelective(SysLogWithBLOBs record);

    SysLogWithBLOBs selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysLogWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(SysLogWithBLOBs record);

    int updateByPrimaryKey(SysLog record);

    // 判断要查询的记录是否存在
    int countBySearchDto(@Param("dto") SearchLogDto dto);

    // 获取所有的记录
    List<SysLogWithBLOBs> getPageListBySearchDto(@Param("dto") SearchLogDto dto,@Param("page") PageBean<SysLogWithBLOBs> page);
}