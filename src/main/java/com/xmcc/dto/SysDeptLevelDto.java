package com.xmcc.dto;

import com.xmcc.model.SysDept;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class SysDeptLevelDto extends SysDept {

    // 用来存储下层数据
    List<SysDeptLevelDto> deptList = new ArrayList<>();

    // 将 sysDept 封装成 SysDeptDto
    public static SysDeptLevelDto adapter(SysDept sysDept){

        SysDeptLevelDto dto = new SysDeptLevelDto();

        // 拷贝字段
        BeanUtils.copyProperties(sysDept,dto);
        return dto;
    }
}
