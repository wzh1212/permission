package com.xmcc.param;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

@Setter
@Getter
public class RoleParam {

    private Integer id;

    @NotBlank(message = "角色名不能为空")
    @Length(max = 20,min = 2,message = "角色名需要在2-20个字之间")
    private String name;

    @Length(max = 150,message = "备注长度需要在150个字以内")
    private String remark;

    private Integer status;
    private Integer type;

}
