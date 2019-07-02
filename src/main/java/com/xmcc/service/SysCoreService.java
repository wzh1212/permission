package com.xmcc.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.xmcc.beans.CacheKeyPrefix;
import com.xmcc.dao.SysAclMapper;
import com.xmcc.dao.SysRoleAclMapper;
import com.xmcc.dao.SysRoleUserMapper;
import com.xmcc.model.SysAcl;
import com.xmcc.model.SysRoleAcl;
import com.xmcc.model.SysUser;
import com.xmcc.utils.IpUtil;
import com.xmcc.utils.JsonMapper;
import com.xmcc.utils.RequestHolder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class SysCoreService {

    @Resource
    private SysAclMapper sysAclMapper;

    @Resource
    private SysRoleUserMapper sysRoleUserMapper;

    @Resource
    private SysRoleAclMapper sysRoleAclMapper;

    @Resource
    private SysCacheService sysCacheService;

    // 通过用户 id 拿到用户的相关角色，在通过角色查询出角色对应的权限点
    public List<SysAcl> getUserAclList(){
        // 获取 用户的 id
        Integer userId = RequestHolder.getUser().getId();
        // 判断是否是超级管理员
        if (isSupperAdmin()){
            // 返回 所有的 权限点
            return sysAclMapper.getAll();
        }

        // 通过 用户 id 获取用户拥有的角色 id
        List<Integer> userRoleIdList = sysRoleUserMapper.getRoleIdListByUserId(userId);
        if (userRoleIdList == null || userRoleIdList.size() == 0){
            return new ArrayList<>();
        }

        // 通过 角色 id 查询 对应的 权限点 id
        List<Integer> userAclIdList = sysRoleAclMapper.getAclIdListByRoleId(userRoleIdList);

        // 通过 权限点 id 查询 权限对象
        List<SysAcl> userAclList = sysAclMapper.getByIdList(userAclIdList);

        return userAclList;
    }

    //  根据 roleId 获取当前角色拥有的 权限
    public List<SysAcl> getRoleAclList(Integer roleId){
        // 将 roleId 存入集合，
        ArrayList<Integer> roleIdList = new ArrayList<>();
        roleIdList.add(roleId);
        // 通过 角色 id 查询 对应的 权限点 id
        List<Integer> aclIdList = sysRoleAclMapper.getAclIdListByRoleId(roleIdList);
        if (aclIdList == null || aclIdList.size() == 0){
            return new ArrayList<>();
        }
        return sysAclMapper.getByIdList(aclIdList);
    }

    // 判断是否是超级管理员
    private boolean isSupperAdmin(){
        // 自己定义了一个假的超级管理员规则，实际中要根据项目进行修改
        // 可以配置文件获取，可以指定某个用户，也可以指定某个角色
        SysUser user = RequestHolder.getUser();
        if (user.getUsername().contains("Admin")){
            return true;
        }
        return false;
    }


    /**
     * 判断当前用户是否拥有有权限
     * @param url 请求路径
     * @return
     */
    public boolean hasAcl(String url){
        // 判断是否是超级管理员
        if (isSupperAdmin()){
            return true;
        }

        // 通过 URL 查询对应的权限点
        // 返回的是一个对象
        SysAcl sysAcl = sysAclMapper.getByUrl(url);
        // 如果 sysAcl 是空，说明该路径 不需要权限也可以访问
        if (sysAcl == null){
            return true;
        }
        // 如果不是空，获取当前用户拥有的所有权限点
       // List<SysAcl> userAclList = getUserAclList();
        List<SysAcl> userAclList = getCurrentUserFromCache();
        System.out.println("缓存"+userAclList);
        // 判断当前用户的权限点集合，看是否包含 请求的权限点，如果包含：说明有权限，如果不包含：说明没有权限
        if (userAclList.contains(sysAcl)){
            return true;
        }
        return false;
    }

    // 在缓存中获取用户的权限
    public List<SysAcl> getCurrentUserFromCache(){
        List<SysAcl> userList = null;
        // 从缓存中获取权限
        String cache = sysCacheService.getInfoFromCache(CacheKeyPrefix.USER_ACLS, String.valueOf(RequestHolder.getUser().getId()));
        // 判断 cache 是否为空
        if (cache == null){
            // 缓存中没有
            // 从 数据库中读取
            userList = getUserAclList();
            // 存入 缓存
            cache = JsonMapper.obj2String(userList);
            sysCacheService.saveCache(cache,60*60*24,String.valueOf(RequestHolder.getUser().getId()),CacheKeyPrefix.USER_ACLS);
            System.out.println("没有缓存：" + userList);
        }else {
            userList = JsonMapper.string2Obj(cache, new TypeReference<List<SysAcl>>() {});
            System.out.println("已有缓存：" + userList);
        }
        return userList;
    }


}
