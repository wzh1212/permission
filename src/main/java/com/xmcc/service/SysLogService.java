package com.xmcc.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.xmcc.beans.LogType;
import com.xmcc.beans.PageBean;
import com.xmcc.dao.*;
import com.xmcc.dto.SearchLogDto;
import com.xmcc.exception.ParamException;
import com.xmcc.model.*;
import com.xmcc.param.SearchLogParam;
import com.xmcc.utils.BeanValidator;
import com.xmcc.utils.IpUtil;
import com.xmcc.utils.JsonMapper;
import com.xmcc.utils.RequestHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class SysLogService {


    @Resource
    private SysLogMapper sysLogMapper;

    @Resource
    private SysDeptMapper sysDeptMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private SysAclModuleMapper sysAclModuleMapper;

    @Resource SysAclMapper sysAclMapper;

    @Resource
    private SysRoleMapper sysRoleMapper;
    /**
     * 注意：如果两个类相互注入，会出现死循环，所以这个地方需要稍微的修改一下代码的逻辑，
     * 我们将 SysLogService 中的 saveRoleAclLog 的方法直接放入 SysRoleAclService 中，作为成员方法
     */
    @Resource
    private SysRoleAclService sysRoleAclService;

    @Resource
    private SysRoleUserService sysRoleUserService;



    public void recover(Integer id){
        // 根据 id 查询出这个记录
        SysLogWithBLOBs log = sysLogMapper.selectByPrimaryKey(id);
        // 判断这条记录是否还存在
        if (log == null){
            throw new ParamException("待还原的记录不存在");
        }
        // 判断对那个模块进行操作
        switch (log.getType()){
            case LogType.TYPE_DEPT:
                // 根据 target_id 取出当前数据库中部门的信息
                SysDept beforeDept = sysDeptMapper.selectByPrimaryKey(log.getTargetId());
                // 判断是否为空
                if (beforeDept == null){
                    throw new ParamException("待还原的部门已经不存在了");
                }
                // 新增和删除不做还原操作
                if (StringUtils.isBlank(log.getOldValue()) || StringUtils.isBlank(log.getNewValue())){
                    throw new ParamException("新增和删除不做还原处理");
                }
                // 获取记录中对应的 old_value 的信息
                SysDept afterDept = JsonMapper.string2Obj(log.getOldValue(),new TypeReference<SysDept>() {});
                afterDept.setOperator(RequestHolder.getUser().getUsername());
                afterDept.setOperateIp(IpUtil.getUserIP(RequestHolder.getRequest()));
                afterDept.setOperateTime(new Date());

                sysDeptMapper.updateByPrimaryKey(afterDept);
                // 记录日志
                saveDeptLog(beforeDept,afterDept);
                break;
            case LogType.TYPE_USER:
                // 根据 target_id 取出之前的数据
                SysUser beforeUser = sysUserMapper.selectByPrimaryKey(log.getTargetId());
                // 判断是否为空
                if (beforeUser == null){
                    throw new ParamException("待还原的用户已经不存在了");
                }
                // 新增和删除不做还原操作
                if (StringUtils.isBlank(log.getOldValue()) || StringUtils.isBlank(log.getNewValue())){
                    throw new ParamException("新增和删除不做还原处理");
                }
                SysUser afterUser = JsonMapper.string2Obj(log.getOldValue(),new TypeReference<SysUser>() {});
                afterUser.setOperator(RequestHolder.getUser().getUsername());
                afterUser.setOperateIp(IpUtil.getUserIP(RequestHolder.getRequest()));
                afterUser.setOperateTime(new Date());

                sysUserMapper.updateByPrimaryKey(afterUser);
                // 记录日志
                saveUserLog(beforeUser,afterUser);
                break;
            case LogType.TYPE_ACL_MODULE:
                // 根据 target_id 取出之前的数据
                SysAclModule beforeAclModule = sysAclModuleMapper.selectByPrimaryKey(log.getTargetId());
                // 判断是否为空
                if (beforeAclModule == null){
                    throw new ParamException("待还原的权限已经不存在了");
                }
                // 新增和删除不做还原操作
                if (StringUtils.isBlank(log.getOldValue()) || StringUtils.isBlank(log.getNewValue())){
                    throw new ParamException("新增和删除不做还原处理");
                }
                SysAclModule afterAclModule = JsonMapper.string2Obj(log.getOldValue(),new TypeReference<SysAclModule>() {});
                afterAclModule.setOperator(RequestHolder.getUser().getUsername());
                afterAclModule.setOperateIp(IpUtil.getUserIP(RequestHolder.getRequest()));
                afterAclModule.setOperateTime(new Date());

                sysAclModuleMapper.updateByPrimaryKey(afterAclModule);
                // 记录日志
                saveAclModuleLog(beforeAclModule,afterAclModule);
                break;
            case LogType.TYPE_ACL:
                // 根据 target_id 取出之前的数据
                SysAcl beforeAcl = sysAclMapper.selectByPrimaryKey(log.getTargetId());
                // 判断是否为空
                if (beforeAcl == null){
                    throw new ParamException("待还原的权限点已经不存在了");
                }
                // 新增和删除不做还原操作
                if (StringUtils.isBlank(log.getOldValue()) || StringUtils.isBlank(log.getNewValue())){
                    throw new ParamException("新增和删除不做还原处理");
                }
                SysAcl afterAcl = JsonMapper.string2Obj(log.getOldValue(),new TypeReference<SysAcl>() {});
                afterAcl.setOperator(RequestHolder.getUser().getUsername());
                afterAcl.setOperateIp(IpUtil.getUserIP(RequestHolder.getRequest()));
                afterAcl.setOperateTime(new Date());

                sysAclMapper.updateByPrimaryKey(afterAcl);
                // 记录日志
                saveAclLog(beforeAcl,afterAcl);
                break;
            case LogType.TYPE_ROLE:
                // 根据 target_id 取出之前的数据
                SysRole beforeRole = sysRoleMapper.selectByPrimaryKey(log.getTargetId());
                // 判断是否为空
                if (beforeRole == null){
                    throw new ParamException("待还原的角色已经不存在了");
                }
                // 新增和删除不做还原操作
                if (StringUtils.isBlank(log.getOldValue()) || StringUtils.isBlank(log.getNewValue())){
                    throw new ParamException("新增和删除不做还原处理");
                }
                SysRole afterRole = JsonMapper.string2Obj(log.getOldValue(),new TypeReference<SysRole>() {});
                afterRole.setOperator(RequestHolder.getUser().getUsername());
                afterRole.setOperateIp(IpUtil.getUserIP(RequestHolder.getRequest()));
                afterRole.setOperateTime(new Date());

                sysRoleMapper.updateByPrimaryKey(afterRole);
                // 记录日志
                saveRoleLog(beforeRole,afterRole);
                break;
            case LogType.TYPE_ROLE_ACL:
                SysRole sysRole = sysRoleMapper.selectByPrimaryKey(log.getTargetId());
                // 判断是否为空
                if (sysRole == null){
                    throw new ParamException("角色权限已经不存在了");
                }
                // 进行还原操作
                sysRoleAclService.changeAcl(log.getTargetId(),
                        JsonMapper.string2Obj(log.getOldValue(), new TypeReference<List<Integer>>() {}));
                break;
            case LogType.TYPE_ROLE_USER:
                SysRole sysRole2 = sysRoleMapper.selectByPrimaryKey(log.getTargetId());
                // 判断是否为空
                if (sysRole2 == null){
                    throw new ParamException("角色用户已经不存在了");
                }
                sysRoleUserService.updateUsers(log.getTargetId(),
                        JsonMapper.string2Obj(log.getOldValue(),new TypeReference<List<Integer>>() {}));
                break;
        }

    }


    /**
     * 分页查询日志
     * @param param
     * @param page
     * @return
     */
    public PageBean<SysLogWithBLOBs> searchPageList(SearchLogParam param,PageBean<SysLogWithBLOBs> page){
        // 校验参数
        BeanValidator.check(param);
        // 将参数封装层 Dto
        SearchLogDto dto = new SearchLogDto();
        // 设置类型
        dto.setType(param.getType());
        // 设置查询条件
        if (StringUtils.isNoneBlank(param.getBeforeSeg())){
            dto.setBeforeSeg("%" + param.getBeforeSeg() + "%");
        }
        if (StringUtils.isNoneBlank(param.getAfterSeg())){
            dto.setAfterSeg("%" + param.getAfterSeg() + "%");
        }
        if (StringUtils.isNoneBlank(param.getOperator())){
            dto.setOperator("%" + param.getOperator() + "%");
        }
        // 处理日期格式
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            if (StringUtils.isNoneBlank(param.getFromTime())){
                dto.setFromTime(format.parse(param.getFromTime()));
            }
            if (StringUtils.isNoneBlank(param.getToTime())){
                dto.setToTime(format.parse(param.getToTime()));
            }
        }catch (Exception e){
           throw new ParamException("传入的日期格式有误");
        }
        // 判断要查询的记录是否存在
        int count = sysLogMapper.countBySearchDto(dto);
        if (count > 0){
            // 获取所有的记录
            List<SysLogWithBLOBs> logList = sysLogMapper.getPageListBySearchDto(dto,page);
            // 封装 PageBean
            page.setTotal(count);
            page.setData(logList);
            return page;
        }
        return new PageBean<>();
    }


    // 保存部门更新前后的信息
    public void saveDeptLog(SysDept before,SysDept after){
        SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
        sysLog.setType(LogType.TYPE_DEPT);
        // 插入时：before 是空的；
        // 删除时：after 是空的
        // 只有修改时，before 和 after 才不为空
        sysLog.setTargetId(after == null ? before.getId():after.getId());
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

    // 保存用户更新前后的信息
    public void saveUserLog(SysUser before, SysUser after){
        SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
        sysLog.setType(LogType.TYPE_USER);
        // 插入时：before 是空的；
        // 删除时：after 是空的
        // 只有修改时，before 和 after 才不为空
        sysLog.setTargetId(after == null ? before.getId():after.getId());
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

    // 保存权限模块更新前后的信息
    public void saveAclModuleLog(SysAclModule before, SysAclModule after){
        SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
        sysLog.setType(LogType.TYPE_ACL_MODULE);
        // 插入时：before 是空的；
        // 删除时：after 是空的
        // 只有修改时，before 和 after 才不为空
        sysLog.setTargetId(after == null ? before.getId():after.getId());
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

    // 保存权限点更新前后的信息
    public void saveAclLog(SysAcl before, SysAcl after){
        SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
        sysLog.setType(LogType.TYPE_ACL);
        // 插入时：before 是空的；
        // 删除时：after 是空的
        // 只有修改时，before 和 after 才不为空
        sysLog.setTargetId(after == null ? before.getId():after.getId());
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

    // 保存角色更新前后的信息
    public void saveRoleLog(SysRole before, SysRole after){
        SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
        sysLog.setType(LogType.TYPE_ROLE);
        // 插入时：before 是空的；
        // 删除时：after 是空的
        // 只有修改时，before 和 after 才不为空
        sysLog.setTargetId(after == null ? before.getId():after.getId());
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
