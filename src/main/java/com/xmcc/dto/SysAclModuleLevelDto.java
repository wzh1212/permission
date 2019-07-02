package com.xmcc.dto;

import com.xmcc.model.SysAclModule;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class SysAclModuleLevelDto extends SysAclModule {
    // 用来存储下层数据（封装下层权限模块）
    List<SysAclModuleLevelDto> aclModuleList = new ArrayList<>();

    // 封装当前权限模块下的权限点
    List<SysAclDto> aclList = new ArrayList<>();

    // 将 SysAclModule 封装成 SysAclModuleNameDto
    public static SysAclModuleLevelDto adapter(SysAclModule sysAclModule){
        SysAclModuleLevelDto dto = new SysAclModuleLevelDto();
        // 拷贝字段
        BeanUtils.copyProperties(sysAclModule,dto);
        return dto;
    }
}
