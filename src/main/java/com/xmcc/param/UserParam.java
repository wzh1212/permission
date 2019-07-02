package com.xmcc.param;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class UserParam {

    private Integer id;

    @NotBlank(message = "用户名不能为空")
    @Length(max = 20,min = 1,message = "用户名长度需要在20个字以内")
    private String username;

    @Length(max = 20,min = 1,message = "密码长度在13个字以内")
    private String password;

    @Length(max = 13,min = 1,message = "电话长度需要在13个字以内")
    private String telephone;

    @Length(max = 50,min = 1,message = "邮箱长度需要在50个字符以内")
    private String mail;

    private Integer deptId;
    private Integer status;

    @Length(max = 200,message = "备注长度需要在200个字以内")
    private String remark;
}
