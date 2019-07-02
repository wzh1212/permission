package com.xmcc.param;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class AclModuleParam {

    private Integer id;

    @NotBlank(message = "权限名不能为空")
    @Length(max = 15,min = 1,message = "权限名长度需要在2-15个字之间")
    private String name;

    private Integer parentId;

    @NotNull
    private Integer seq;

    @Length(max = 150,message = "部门备注不能超过150个字")
    private String remark;

    @NotNull(message = "状态  1：正常，0：冻结")
    private Integer status;
}
