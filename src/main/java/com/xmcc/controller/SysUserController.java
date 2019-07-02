package com.xmcc.controller;

import com.xmcc.beans.PageBean;
import com.xmcc.model.SysUser;
import com.xmcc.param.UserParam;
import com.xmcc.service.SysUserService;
import com.xmcc.utils.JsonData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/sys/user")
public class SysUserController {

    @Resource
    private SysUserService sysUserService;

    /**
     * 登录
     * @param param
     * @param request
     * @return
     */
    @RequestMapping("/login.page")
    public ModelAndView login(UserParam param, HttpServletRequest request){
        SysUser sysUser = sysUserService.login(param);
        request.getSession().setAttribute("user",sysUser);
        return new ModelAndView("admin");
    }


    /**
     * 根据部门查找用户并分页
     * @param deptId
     * @param pageBean
     * @return
     */
    @RequestMapping("/page.json")
    @ResponseBody
    public JsonData page(int deptId, PageBean<SysUser> pageBean){
        PageBean<SysUser> pageByDeptId = sysUserService.getPageByDeptId(deptId, pageBean);
        return JsonData.success(pageByDeptId);
    }

    /**
     * 添加用户
     * @param param
     * @return
     */
    @RequestMapping("/save.json")
    @ResponseBody
    public JsonData saveUser(UserParam param){
        sysUserService.saveUser(param);
        return JsonData.success();
    }

    /**
     * 修改用户
     * @param param
     * @return
     */
    @RequestMapping("/update.json")
    @ResponseBody
    public JsonData updateUser(UserParam param){
        sysUserService.updateUser(param);
        return JsonData.success();
    }


    @RequestMapping("/noAuth.page")
    public ModelAndView noAuth(){
        return new ModelAndView("noAuth");
    }
}
