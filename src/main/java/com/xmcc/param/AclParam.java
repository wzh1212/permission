package com.xmcc.param;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class AclParam {
    private Integer id;

    @NotBlank(message = "权限点名不能为空")
    @Length(max = 20,min = 2,message = "权限点名需要在2-20个字之间")
    private String name;

    @NotNull
    private Integer aclModuleId;
    @NotNull
    private Integer seq;

    @Length(max = 150,message = "备注长度需要在150个字以内")
    private String remark;

    private Integer status;

    @Length(max = 100,min = 6,message = "长度需要在6-100个字符之间")
    private String url;

    private Integer type;
}
