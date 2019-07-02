package com.xmcc.service;

import com.xmcc.beans.PageBean;
import com.xmcc.dao.SysUserMapper;
import com.xmcc.exception.ParamException;
import com.xmcc.model.SysUser;
import com.xmcc.param.UserParam;
import com.xmcc.utils.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class SysUserService {

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private SysLogService sysLogService;

    /**
     * 用户登录
     * @param param
     * @return
     */
    public SysUser login(UserParam param){
        // 验证参数是否正确
        BeanValidator.check(param);

        String username = param.getUsername();
        String password = param.getPassword();

        // 根据姓名查找
        SysUser sysUser = sysUserMapper.findByName(username);
        // 用户存储错误信息
        String errorMsg = "";

        // 判断是否存在该用户
        if (sysUser == null){
            errorMsg = "用户不存在";
        }else if (!sysUser.getPassword().equals(MD5Util.encrypt(password))){
            // 判断 密码 是否正确
            errorMsg = "密码错误";
        }else if (sysUser.getStatus() != 1){
            errorMsg = "用户状态异常";
        }else {
            return sysUser;
        }
        throw new ParamException(errorMsg);
    }

    /**
     * 根据部门查找用户并分页
     * @param deptId
     * @param page
     * @return
     */
    public PageBean<SysUser> getPageByDeptId(int deptId, PageBean<SysUser> page){
        // 校验参数
        BeanValidator.check(page);
        // 检查当前部门下是否存在用户
        int count = sysUserMapper.countByDeptId(deptId);
        if (count > 0){
            // 存在用户
            // 进行分页
            PageBean<SysUser> pageBean = new PageBean<>();
            List<SysUser> list = sysUserMapper.getUserPageByDeptId(deptId,page);
            // 进行封装
            pageBean.setData(list);
            pageBean.setTotal(count);
            return pageBean;
        }
        return new PageBean<>();
    }

    /**
     * 添加用户
     * @param param
     */
    public void saveUser(UserParam param){
        BeanValidator.check(param);

        // 验证同一部门下，是否有相同的用户
        if (checkUserByTelAndName(param.getDeptId(),param.getTelephone(),param.getId()) > 0){
            throw new ParamException("该部门下已有该用户");
        }

        SysUser sysUser = SysUser.builder()
                .username(param.getUsername())
                .telephone(param.getTelephone())
                .mail(param.getMail())
                .deptId(param.getDeptId())
                .status(param.getStatus())
                .remark(param.getRemark()).build();

        String psw = PasswordUtil.randomPassword();

        sysUser.setPassword(MD5Util.encrypt(psw));

        sysUser.setOperator(RequestHolder.getUser().getUsername());
        sysUser.setOperateIp(IpUtil.getUserIP(RequestHolder.getRequest()));
        sysUser.setOperateTime(new Date());

        int i = sysUserMapper.insertSelective(sysUser);
        sysLogService.saveUserLog(null,sysUser);
        if (i > 0){
            MailUtils.sendMail(param.getMail(),"你的初始密码是：" + psw,"注册成功");
        }else {
            throw new ParamException("参数错误");
        }

    }

    // 根据 电话号码，验证是否存在该用户
    public int checkUserByTelAndName(Integer deptId,String telephone,Integer userId){
        return sysUserMapper.countByUserIdAndUserNam(deptId,telephone,userId);
    }

    /**
     * 修改用户
     * @param param
     */
    public void updateUser(UserParam param){
        BeanValidator.check(param);
        // 根据 userID 取出更新前的用户
        SysUser beforeUser = sysUserMapper.selectByPrimaryKey(param.getId());
        if (beforeUser == null){
            throw new ParamException("待更新等待的用户不存在");
        }
        SysUser sysUser = SysUser.builder().id(param.getId())
                .username(param.getUsername())
                .telephone(param.getTelephone())
                .mail(param.getMail())
                .deptId(param.getDeptId())
                .status(param.getStatus())
                .remark(param.getRemark()).build();

        sysUser.setOperator(RequestHolder.getUser().getUsername());
        sysUser.setOperateIp(IpUtil.getUserIP(RequestHolder.getRequest()));
        sysUser.setOperateTime(new Date());

        sysUserMapper.updateByPrimaryKeySelective(sysUser);
        sysLogService.saveUserLog(beforeUser,sysUser);
    }

    // TODO:退出用户
}
