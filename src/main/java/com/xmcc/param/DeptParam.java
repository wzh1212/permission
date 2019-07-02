package com.xmcc.param;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class DeptParam {

    private Integer id;

    @NotBlank(message = "部门名不能为空")
    @Length(max = 15,min = 2,message = "部门名称长度需要在2-15之间")
    private String name;

    // 赋初值，默认为顶层部门
    private Integer parentId = 0;

    @NotNull
    private Integer seq;

    @Length(max = 150,message = "部门备注不能超过150个字")
    private String remark;

}
