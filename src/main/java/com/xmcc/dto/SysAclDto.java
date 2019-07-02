package com.xmcc.dto;

import com.xmcc.model.SysAcl;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

@Setter
@Getter
public class SysAclDto extends SysAcl {

    // 检查当前角色是否拥有这个权限
    private boolean checked = false;

    // 检查当前用户是否有操作这个权限能力
    private boolean hasAcl = false;

    // 将 SysAcl 封装成 SysAclDto（转换）
    public static SysAclDto adapter(SysAcl sysAcl){
        SysAclDto dto = new SysAclDto();
        // 拷贝字段
        BeanUtils.copyProperties(sysAcl,dto);
        return dto;
    }
}
