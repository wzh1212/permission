package com.xmcc.controller;

import com.xmcc.dto.SysAclModuleLevelDto;
import com.xmcc.model.SysRole;
import com.xmcc.param.RoleParam;
import com.xmcc.service.*;
import com.xmcc.utils.JsonData;
import com.xmcc.utils.StringUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/sys/role")
public class SysRoleController {

    @Resource
    private SysRoleService sysRoleService;

    @Resource
    private SysTreeService sysTreeService;

    @Resource
    private SysRoleAclService sysRoleAclService;

    @Resource
    private SysRoleUserService sysRoleUserService;

    @RequestMapping("/role.page")
    public ModelAndView roleView(){
        return new ModelAndView("role");
    }

    @RequestMapping("/list.json")
    @ResponseBody
    public JsonData roleList(){
        List<SysRole> list = sysRoleService.roleList();
        return JsonData.success(list);
    }

    /**
     * 新增角色
     * @param param
     * @return
     */
    @RequestMapping("/save.json")
    @ResponseBody
    public JsonData save(RoleParam param){
        sysRoleService.saveRole(param);
        return JsonData.success();
    }

    /**
     * 更新角色
     * @param param
     * @return
     */
    @RequestMapping("/update.json")
    @ResponseBody
    public JsonData update(RoleParam param){
        sysRoleService.updateRole(param);
        return JsonData.success();
    }

    /**
     * 删除角色
     * @param param
     * @return
     */
//    @RequestMapping("/delete.json")
//    @ResponseBody
//    public JsonData delete(RoleParam param){
//        sysRoleService.deleteRole(param.getId());
//        return JsonData.success();
//    }

    /**
     * 角色树
     * @param roleId
     * @return
     */
    @RequestMapping("/roleTree.json")
    @ResponseBody
    public JsonData roelTree(@RequestParam("roleId") Integer roleId){
        List<SysAclModuleLevelDto> sysAclModuleLevelDtos = sysTreeService.roleTree(roleId);
        return JsonData.success(sysAclModuleLevelDtos);
    }

    /**
     * 更新权限
     * @param roleId
     * @param aclIds
     * @return
     */
    @RequestMapping("/changeAcls.json")
    @ResponseBody
    public JsonData changeAcls(@RequestParam("roleId") Integer roleId,String aclIds){
        List<Integer> aclIdList = StringUtil.strToList(aclIds);
        sysRoleAclService.changeAcl(roleId,aclIdList);
        return JsonData.success();
    }

    /**
     * 角色用户
     * @param roleId
     * @return
     */
    @RequestMapping("/users.json")
    @ResponseBody
    public JsonData users(@RequestParam("roleId") Integer roleId){
        Map map = sysRoleUserService.getUserMapByRoleId(roleId);
        return JsonData.success(map);
    }

    /**
     * 修改角色用户
     * @param roleId
     * @param userIds
     * @return
     */
    @RequestMapping("/changeUsers.json")
    @ResponseBody
    public JsonData updateUsers(@RequestParam("roleId") Integer roleId,String userIds){
        List<Integer> userIdList = StringUtil.strToList(userIds);
        sysRoleUserService.updateUsers(roleId,userIdList);
        return JsonData.success();
    }
}
