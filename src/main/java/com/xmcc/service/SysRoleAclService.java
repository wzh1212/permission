package com.xmcc.service;

import com.xmcc.beans.LogType;
import com.xmcc.dao.SysAclMapper;
import com.xmcc.dao.SysLogMapper;
import com.xmcc.dao.SysRoleAclMapper;
import com.xmcc.model.SysLogWithBLOBs;
import com.xmcc.model.SysRoleAcl;
import com.xmcc.utils.IpUtil;
import com.xmcc.utils.JsonMapper;
import com.xmcc.utils.RequestHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class SysRoleAclService {

    @Resource
    private SysRoleAclMapper sysRoleAclMapper;

    @Resource
    private SysLogMapper sysLogMapper;


    // 通过 roleId 更改 权限
    public void changeAcl(Integer roleId, List<Integer> aclIds){
        // 1、判断是否有修改
        // 获取角色原来的权限点 id
        ArrayList<Integer> roleIdList = new ArrayList<>();
        roleIdList.add(roleId);
        List<Integer> oldAclIdListByRoleId = sysRoleAclMapper.getAclIdListByRoleId(roleIdList);
        // 判断修改前和修改后的 aclIds
        if (oldAclIdListByRoleId.size() == aclIds.size()){
            // 移除
            // boolean b ： 移除是否成功
            boolean b = aclIds.removeAll(oldAclIdListByRoleId);
            // 全部移除成功，返回 true；没有全部移除，返回 false，执行后面的方法
            if (b){
                return;
            }
        }
        // 更新操作
        updateRoleAcls(roleId,aclIds);
        // 日志记录
        saveRoleAclLog(roleId,oldAclIdListByRoleId,aclIds);
    }

    // 更新操作
    // 为什么加事务：因为；里面含有两步操作：一：删除，二：新增
    @Transactional
    public void updateRoleAcls(Integer roleId, List<Integer> aclIds) {
        // 删除角色原来拥有的全部权限点
        sysRoleAclMapper.deleteByRoleId(roleId);

        if (aclIds.size() == 0){
            return;
        }

        // 批量新增
        ArrayList<SysRoleAcl> sysRoleAcls = new ArrayList<>();
        for (Integer aclId : aclIds) {
            SysRoleAcl sysRoleAcl = SysRoleAcl.builder().roleId(roleId)
                    .aclId(aclId)
                    .operator(RequestHolder.getUser().getUsername())
                    .operateIp(IpUtil.getUserIP(RequestHolder.getRequest()))
                    .operateTime(new Date()).build();
            sysRoleAcls.add(sysRoleAcl);
        }
        sysRoleAclMapper.bathInsert(sysRoleAcls);
    }

    // 保存角色权限更新前后的信息
    public void saveRoleAclLog(int roleId, List<Integer> before,List<Integer> after){
        SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
        sysLog.setType(LogType.TYPE_ROLE_ACL);
        // 插入时：before 是空的；
        // 删除时：after 是空的
        // 只有修改时，before 和 after 才不为空
        sysLog.setTargetId(roleId);
        // 保存 操作之前的数据
        sysLog.setOldValue(before == null ? "" : JsonMapper.obj2String(before));
        // 保存 操作之后的数据
        sysLog.setNewValue(after == null ? "" : JsonMapper.obj2String(after));
        sysLog.setOperator(RequestHolder.getUser().getUsername());
        sysLog.setOperateIp(IpUtil.getUserIP(RequestHolder.getRequest()));
        sysLog.setOperateTime(new Date());
        sysLog.setStatus(0);
        sysLogMapper.insert(sysLog);
    }
}
