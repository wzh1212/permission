package com.xmcc.service;

import com.xmcc.beans.PageBean;
import com.xmcc.dao.SysAclMapper;
import com.xmcc.exception.ParamException;
import com.xmcc.model.SysAcl;
import com.xmcc.param.AclParam;
import com.xmcc.utils.BeanValidator;
import com.xmcc.utils.IpUtil;
import com.xmcc.utils.RequestHolder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
public class SysAclService {

    @Resource
    private SysAclMapper sysAclMapper;

    @Resource
    private SysLogService sysLogService;

    /**
     * 根据 权限id 查找权限点并分页
     * @param aclModuleId
     * @param page
     * @return
     */
    public PageBean<SysAcl> getPageByAclModuleId(int aclModuleId,PageBean<SysAcl> page){
        BeanValidator.check(page);
        // 检查当前权限下是否存在权限点
        int count = sysAclMapper.countByAclModuleId(aclModuleId);
        if (count > 0){
            PageBean<SysAcl> pageBean = new PageBean<>();
            // 根据 aclModuleId 查询权限下的权限点，并放到集合中
            List<SysAcl> list = sysAclMapper.getAclPageByAclModuleId(aclModuleId,page);
            // 封装
            pageBean.setData(list);
            pageBean.setTotal(count);
            return pageBean;
        }
        return new PageBean<>();
    }

    /**
     * 新增权限点
     * @param param
     */
    public void saveAcl(AclParam param){
        BeanValidator.check(param);
        // 验证同一权限下是否存在该权限点
        if (checkAclByName(param.getAclModuleId(),param.getName(),param.getId()) > 0){
            throw new ParamException("该权限下已有该权限点");
        }

        SysAcl sysAcl = SysAcl.builder()
                .id(param.getId())
                .name(param.getName())
                .aclModuleId(param.getAclModuleId())
                .seq(param.getSeq())
                .remark(param.getRemark())
                .status(param.getStatus())
                .url(param.getUrl())
                .type(param.getType()).build();

        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        String format = simpleDateFormat.format(date);

        Random random = new Random();
        int num = random.nextInt(100);
        sysAcl.setCode(format + "_" + num);

        sysAcl.setOperator(RequestHolder.getUser().getUsername());
        sysAcl.setOperateIp(IpUtil.getUserIP(RequestHolder.getRequest()));
        sysAcl.setOperateTime(new Date());

        int i = sysAclMapper.insertSelective(sysAcl);
        // 日志记录
        sysLogService.saveAclLog(null,sysAcl);
        if (i == 0){
            throw new ParamException("权限点状态有误");
        }
    }
    // 检查是否存在该权限点
    public int checkAclByName(Integer aclModuleId,String aclName,Integer aclId){
        return sysAclMapper.countByAclIdAndAclName(aclModuleId,aclName,aclId);
    }

    /**
     * 更新权限点
     * @param param
     */
    public void updateAcl(AclParam param){
        BeanValidator.check(param);
        SysAcl acl = sysAclMapper.selectByPrimaryKey(param.getId());
        if (acl == null){
            throw new ParamException("待更新的权限点不存在");
        }
        SysAcl sysAcl = SysAcl.builder()
                .id(param.getId())
                .name(param.getName())
                .aclModuleId(param.getAclModuleId())
                .seq(param.getSeq())
                .remark(param.getRemark())
                .status(param.getStatus())
                .url(param.getUrl())
                .type(param.getType()).build();
        sysAcl.setOperator(RequestHolder.getUser().getUsername());
        sysAcl.setOperateIp(IpUtil.getUserIP(RequestHolder.getRequest()));
        sysAcl.setOperateTime(new Date());

        sysAclMapper.updateByPrimaryKeySelective(sysAcl);
        // 日志记录
        sysLogService.saveAclLog(acl,sysAcl);
    }

    /**
     * 删除权限点
     * @param aclId
     */
    public void deleteAcl(Integer aclId){
        BeanValidator.check(aclId);
        SysAcl sysAcl = sysAclMapper.selectByPrimaryKey(aclId);
        if (sysAcl == null){
            throw new ParamException("待删除的权限点不存在");
        }else {
            sysAclMapper.deleteByPrimaryKey(aclId);
            // 日志记录
            sysLogService.saveAclLog(sysAcl,null);
        }
    }

}
