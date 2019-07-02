package com.xmcc.service;

import com.xmcc.dao.SysRoleMapper;
import com.xmcc.exception.ParamException;
import com.xmcc.model.SysRole;
import com.xmcc.param.RoleParam;
import com.xmcc.utils.BeanValidator;
import com.xmcc.utils.IpUtil;
import com.xmcc.utils.RequestHolder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class SysRoleService {

    @Resource
    private SysRoleMapper sysRoleMapper;

    @Resource
    private SysLogService sysLogService;

    /**
     * 查询所有角色
     * @return
     */
    public List<SysRole>  roleList(){
        List<SysRole> list = sysRoleMapper.findAll();
        return list;
    }

    /**
     * 新增角色
     * @param param
     */
    public void saveRole(RoleParam param){
        BeanValidator.check(param);
        // 判断角色是否存在
        if (checkName(param.getName()) > 0){
            throw new ParamException("已存在该角色");
        }

        SysRole sysRole = SysRole.builder()
                .id(param.getId())
                .name(param.getName())
                .remark(param.getRemark())
                .status(param.getStatus())
                .type(param.getType()).build();
        sysRole.setOperator(RequestHolder.getUser().getUsername());
        sysRole.setOperateIp(IpUtil.getUserIP(RequestHolder.getRequest()));
        sysRole.setOperateTime(new Date());

        sysRoleMapper.insertSelective(sysRole);
        // 日志记录
        sysLogService.saveRoleLog(null,sysRole);
    }
    // 根据 name 判断角色是否重复
    public int checkName(String roleName){
        return sysRoleMapper.countByRoleName(roleName);
    }

    /**
     * 更新角色
     * @param param
     */
    public void updateRole(RoleParam param){
        BeanValidator.check(param);
        // 判断更新角色是否存在
        SysRole before = sysRoleMapper.selectByPrimaryKey(param.getId());
        if (before == null){
            throw new ParamException("待更新的角色不存在");
        }

        SysRole sysRole = SysRole.builder()
                .id(param.getId())
                .name(param.getName())
                .remark(param.getRemark())
                .status(param.getStatus())
                .type(param.getType()).build();
        sysRole.setOperator(RequestHolder.getUser().getUsername());
        sysRole.setOperateIp(IpUtil.getUserIP(RequestHolder.getRequest()));
        sysRole.setOperateTime(new Date());

        sysRoleMapper.updateByPrimaryKeySelective(sysRole);
        // 日志记录
        sysLogService.saveRoleLog(before,sysRole);
    }

    /**
     * 删除角色
     * @param roleId
     */
//    public void deleteRole(Integer roleId){
//        BeanValidator.check(roleId);
//        SysRole sysRole = sysRoleMapper.selectByPrimaryKey(roleId);
//        if (sysRole == null){
//            throw new ParamException("待删除的角色不存在");
//        }
//        sysRoleMapper.deleteByPrimaryKey(roleId);
//        // 日志记录
//        sysLogService.saveRoleLog(sysRole,null);
//    }
}
