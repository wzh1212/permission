package com.xmcc.controller;

import com.xmcc.dto.SysAclModuleLevelDto;
import com.xmcc.param.AclModuleParam;
import com.xmcc.service.SysAclModuleService;
import com.xmcc.service.SysTreeService;
import com.xmcc.utils.JsonData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping("/sys/aclModule")
public class SysAclModuleController {

    @Resource
    private SysAclModuleService sysAclModuleService;

    @Resource
    private SysTreeService sysTreeService;

    @RequestMapping("/acl.page")
    public ModelAndView aclModuleView(){
        return new ModelAndView("acl");
    }

    /**
     * 新增权限
     * @param param
     * @return
     */
    @RequestMapping("/save.json")
    @ResponseBody
    public JsonData save(AclModuleParam param){
        sysAclModuleService.saveAclModule(param);
        return JsonData.success();
    }

    /**
     * 权限树
     * @return
     */
    @RequestMapping("/tree.json")
    @ResponseBody
    public JsonData tree(){
        List<SysAclModuleLevelDto> sysAclModuleLevelDtos = sysTreeService.aclModlueTree();
        return JsonData.success(sysAclModuleLevelDtos);
    }

    /**
     * 更新权限
     * @param param
     * @return
     */
    @RequestMapping("/update.json")
    @ResponseBody
    public JsonData update(AclModuleParam param){
       sysAclModuleService.updateAclModule(param);
        return JsonData.success();
    }

    /**
     * 删除权限
     * @param param
     * @return
     */
    @RequestMapping("/delete.json")
    @ResponseBody
    public JsonData delete(AclModuleParam param){
        sysAclModuleService.delete(param.getId());
        return JsonData.success();
    }
}
