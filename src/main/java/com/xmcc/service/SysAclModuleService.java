package com.xmcc.service;

import com.xmcc.dao.SysAclModuleMapper;
import com.xmcc.exception.ParamException;
import com.xmcc.model.SysAclModule;
import com.xmcc.param.AclModuleParam;
import com.xmcc.utils.BeanValidator;
import com.xmcc.utils.IpUtil;
import com.xmcc.utils.LevelUtil;
import com.xmcc.utils.RequestHolder;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class SysAclModuleService {

    @Resource
    private SysAclModuleMapper sysAclModuleMapper;

    @Resource
    private SysLogService sysLogService;

    /**
     * 新增权限
     * @param param
     */
    public void saveAclModule(AclModuleParam param){
        // 验证参数是否正确
        BeanValidator.check(param);
        // 验证同一个权限层级下，是否有相同的权限名
        if (checkAclModuleName(param.getParentId(),param.getName(),param.getId()) > 0){
            throw new ParamException("同一个权限层级下有相同权限名");
        }

        SysAclModule sysAclModule = SysAclModule.builder()
                .name(param.getName())
                .parentId(param.getParentId())
                .seq(param.getSeq())
                .remark(param.getRemark())
                .status(param.getStatus()).build();

        // 计算新增权限的 Level
        sysAclModule.setLevel(LevelUtil.calculate(getLevel(param.getParentId()),param.getParentId()));

        sysAclModule.setOperator(RequestHolder.getUser().getUsername());
        sysAclModule.setOperateIp(IpUtil.getUserIP(RequestHolder.getRequest()));
        sysAclModule.setOperateTime(new Date());

        // 保存
        sysAclModuleMapper.insertSelective(sysAclModule);
        // 日志记录
        sysLogService.saveAclModuleLog(null,sysAclModule);
    }

    // 检查是否存在该权限名
    public int checkAclModuleName(Integer parentId,String aclModuleName,Integer aclModuleId){
        return sysAclModuleMapper.countByParentIdAndAclModuleName(parentId,aclModuleName,aclModuleId);
    }

    // 根据 id 获取自己 的 Level
    public String getLevel(Integer aclModuleId){
        SysAclModule sysAclModule = sysAclModuleMapper.selectByPrimaryKey(aclModuleId);
        if (sysAclModule == null){
            return null;
        }
        return sysAclModule.getLevel();
    }

    /**
     * 更新权限
     * @param param
     */
    public void updateAclModule(AclModuleParam param){
        BeanValidator.check(param);
        SysAclModule before = sysAclModuleMapper.selectByPrimaryKey(param.getId());
        if (before == null){
            throw new ParamException("待更新等待的权限不存在");
        }
        // 验证同一个权限层级下，是否有相同的权限名
        if (checkAclModuleName(param.getParentId(),param.getName(),param.getId()) > 0){
            throw new ParamException("同一个权限层级下有相同权限名");
        }
        SysAclModule after = SysAclModule.builder()
                .id(param.getId())
                .name(param.getName())
                .parentId(param.getParentId())
                .seq(param.getSeq())
                .remark(param.getRemark())
                .status(param.getStatus()).build();

        // 计算新增权限的 Level
        after.setLevel(LevelUtil.calculate(getLevel(param.getParentId()),param.getParentId()));

        after.setOperator(RequestHolder.getUser().getUsername());
        after.setOperateIp(IpUtil.getUserIP(RequestHolder.getRequest()));
        after.setOperateTime(new Date());
        // 更新
        updateWithChild(before,after);
        // 日志记录
        sysLogService.saveAclModuleLog(before,after);
    }

    public void updateWithChild(SysAclModule before,SysAclModule after){
        // 用来存储获取的子权限
        List<SysAclModule> aclModuleList = null;
        // 获取更新后的 level，判断是否相等
        String newLevel = after.getLevel();
        // 获取更新前的 level
        String oldLevel = before.getLevel();
        // 用于修改基础信息
        sysAclModuleMapper.updateByPrimaryKey(after);

        // 如果不相等，就需要更新子权限
        if (!newLevel.equals(oldLevel)){
            // 通过 Level 获取所有的子权限
            aclModuleList = sysAclModuleMapper.getChildAclModuleListByLevel(oldLevel + "." + before.getId());
            if (aclModuleList != null){
                // 循环遍历，给子权限设置新的 Level
                for (SysAclModule sysAclModule : aclModuleList) {
                    SysAclModule sysAclModule1Before = new SysAclModule();
                    // 拷贝
                    BeanUtils.copyProperties(sysAclModule,sysAclModule1Before);

                    String level = sysAclModule.getLevel();
                    // 计算新的 level 值
                    level = newLevel + "." + after.getId();
                    sysAclModule.setLevel(level);

                    // 递归，更新子权限
                    updateWithChild(sysAclModule1Before,sysAclModule);
                }
            }
            // 更新
            sysAclModuleMapper.updateByPrimaryKey(after);
        }
    }

    /**
     * 删除权限
     * @param aclModuleId
     */
    public void delete(Integer aclModuleId){
        // 用来存储获取的子权限
        List<SysAclModule> aclModuleList = null;
        BeanValidator.check(aclModuleId);
        SysAclModule sysAclModule = sysAclModuleMapper.selectByPrimaryKey(aclModuleId);
        if (sysAclModule == null){
            throw new ParamException("待删除的子权限不存在");
        }
        // 获取删除权限下的权限
        aclModuleList = sysAclModuleMapper.getChildAclModuleListByLevel(sysAclModule.getLevel() + "." + sysAclModule.getId());
        // 判断是否有子权限
        if (aclModuleList.size() > 0){
            throw new ParamException("该权限下有子权限，不能删除");
        }else {
            sysAclModuleMapper.deleteByPrimaryKey(aclModuleId);
            // 日志记录
            sysLogService.saveAclModuleLog(sysAclModule,null);
        }
    }

}
