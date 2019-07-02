package com.xmcc.controller;

import com.xmcc.beans.PageBean;
import com.xmcc.model.SysAcl;
import com.xmcc.param.AclParam;
import com.xmcc.service.SysAclService;
import com.xmcc.utils.JsonData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
@RequestMapping("/sys/acl")
public class SysAclController {

    @Resource
    private SysAclService sysAclService;

    /**
     * 根据 权限id 查找权限点并分页
     * @param aclModuleId
     * @param page
     * @return
     */
    @RequestMapping("/page.json")
    @ResponseBody
    public JsonData page(int aclModuleId, PageBean<SysAcl> page){
        PageBean<SysAcl> bean = sysAclService.getPageByAclModuleId(aclModuleId, page);
        return JsonData.success(bean);
    }

    /**
     * 新增权限点
     * @param param
     * @return
     */
    @RequestMapping("/save.json")
    @ResponseBody
    public JsonData saveAcl(AclParam param){
        sysAclService.saveAcl(param);
        return JsonData.success();
    }

    /**
     * 更新权限点
     * @param param
     * @return
     */
    @RequestMapping("/update.json")
    @ResponseBody
    public JsonData updateAcl(AclParam param){
        sysAclService.updateAcl(param);
        return JsonData.success();
    }

    /**
     * 删除权限点
     * @param aclId
     * @return
     */
    @RequestMapping("/delete.json")
    @ResponseBody
    public JsonData delete(Integer aclId){
        sysAclService.deleteAcl(aclId);
        return JsonData.success();
    }
}
