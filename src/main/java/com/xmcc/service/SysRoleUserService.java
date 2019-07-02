package com.xmcc.service;

import com.xmcc.beans.LogType;
import com.xmcc.dao.SysLogMapper;
import com.xmcc.dao.SysRoleUserMapper;
import com.xmcc.dao.SysUserMapper;
import com.xmcc.model.SysLogWithBLOBs;
import com.xmcc.model.SysRoleUser;
import com.xmcc.model.SysUser;
import com.xmcc.utils.IpUtil;
import com.xmcc.utils.JsonMapper;
import com.xmcc.utils.RequestHolder;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class SysRoleUserService {

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private SysRoleUserMapper sysRoleUserMapper;

    @Resource
    private SysLogMapper sysLogMapper;

    // 获取当前角色拥有的用户列表
    public List<SysUser> getListByRoleId(Integer roleId){
        // 通过 角色 id 获取所有的 用户id
        List<Integer> userIdList = sysRoleUserMapper.getUserIdListByRoleId(roleId);
        if (CollectionUtils.isEmpty(userIdList)){
            return new ArrayList<>();
        }
        return sysUserMapper.getByIdList(userIdList);
    }

    /**
     * 创建方法 将 角色已拥有和未拥有的列表封装到 Map 中
     * 展示角色对应的用户
     * @param roleId
     * @return
     */
    public Map getUserMapByRoleId(Integer roleId){
        // 获取 角色已拥有的列表
        List<SysUser> selectUserList = getListByRoleId(roleId);
        // 获取 角色未拥有的列表
        ArrayList<SysUser> unSelectUserList = new ArrayList<>();
        // 获取 角色全部的列表
        List<SysUser> allUserList = sysUserMapper.getAll();
        // 遍历
        for (SysUser sysUser : allUserList){
            // 判断用户的状态，并且 角色已拥有中 不包含 这些用户
            if (sysUser.getStatus() == 1 && !selectUserList.contains(sysUser)){
                unSelectUserList.add(sysUser);
            }
        }

        Map<String,List<SysUser>> map = new HashMap<>();
        map.put("unselected",unSelectUserList);
        map.put("selected",selectUserList);
        return map;
    }

    /**
     * 更新
     * @param roleId
     * @param userIds
     */
    public void updateUsers(Integer roleId,List<Integer> userIds){
        // 判断是否有修改
        // 获取角色原来的用户id
        List<Integer> oldUserIdList = sysRoleUserMapper.getUserIdListByRoleId(roleId);
        // 判断修改前和修改后的 userIds.size()
        if (oldUserIdList.size() == userIds.size()){
            if (userIds.containsAll(oldUserIdList)){
                return;
            }
        }
        deleteRoleAndUser(roleId);
        saveRoleAndUser(roleId,userIds);
        // 日志记录
        saveRoleUserLog(roleId,oldUserIdList,userIds);
    }

    /**
     * 根据 roleId 删除 所有对应的 用户id
     * @param roleId
     */
    private void deleteRoleAndUser(Integer roleId) {
        sysRoleUserMapper.deleteByRoleId(roleId);
    }

    /**
     * 批量新增
     * @param roleId
     * @param userIds
     */
    private void saveRoleAndUser(Integer roleId, List<Integer> userIds) {
        if (userIds.size() == 0){
            return;
        }
        // 批量新增
        ArrayList<SysRoleUser> list = new ArrayList<>();
        for (Integer userId : userIds) {
            SysRoleUser sysRoleUser = SysRoleUser.builder()
                    .roleId(roleId)
                    .userId(userId)
                    .operator(RequestHolder.getUser().getUsername())
                    .operateTime(new Date())
                    .operateIp(IpUtil.getUserIP(RequestHolder.getRequest())).build();

            list.add(sysRoleUser);
        }
        sysRoleUserMapper.bathInsert(list);
    }

    // 保存角色用户更新前后的信息
    public void saveRoleUserLog(int roleId, List<Integer> before,List<Integer> after){
        SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
        sysLog.setType(LogType.TYPE_ROLE_USER);
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
