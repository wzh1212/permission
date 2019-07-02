package com.xmcc.service;

import com.xmcc.dao.SysDeptMapper;
import com.xmcc.exception.ParamException;
import com.xmcc.model.SysDept;
import com.xmcc.param.DeptParam;
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
public class SysDeptService {

    @Resource
    private SysDeptMapper sysDeptMapper;

    @Resource
    private SysLogService sysLogService;

    /**
     * 添加部门
     * @param param
     */
    public void save(DeptParam param){
        // 验证参数是否正确
        BeanValidator.check(param);
        // 验证同一个部门层级下，是否有相同的部门
        if (checkExist(param.getParentId(),param.getName(),param.getId()) > 0){
            throw new ParamException("同一个部门层级下有相同部门");
        }
        SysDept sysDept = SysDept.builder().name(param.getName()).parentId(param.getParentId())
                .seq(param.getSeq()).remark(param.getRemark()).build();

        // 设置新建部门的 层级：计算层级 = 父级的层级 + 父级的 id
        sysDept.setLevel(LevelUtil.calculate(getLevel(param.getParentId()),param.getParentId())); // parentLevel.parentId

        sysDept.setOperator(RequestHolder.getUser().getUsername());
        sysDept.setOperateIp(IpUtil.getUserIP(RequestHolder.getRequest()));
        sysDept.setOperateTime(new Date());

        // 保存插入
        sysDeptMapper.insertSelective(sysDept);
        // 插入日志
        sysLogService.saveDeptLog(null,sysDept);
    }

    // 检查是否存在该部门
    public int checkExist(Integer parentId,String deptName,Integer deptId){
        return sysDeptMapper.countByParentIdAndDeptName(parentId, deptName, deptId);
    }

    // 根据 deptId 获取自己的 Level
    public String getLevel(Integer deptId){
        SysDept sysDept = sysDeptMapper.selectByPrimaryKey(deptId);
        if (sysDept == null){
            return null;
        }
        return sysDept.getLevel();
    }

    /**
     * 更新部门
     * @param param
     */
    public void update(DeptParam param){
        // 验证参数是否正确
        BeanValidator.check(param);
        // 根据 deptId 取出更新前的部门
        SysDept before = sysDeptMapper.selectByPrimaryKey(param.getId());
        if (before == null){
            throw new ParamException("待更新等待的部门不存在");
        }

        // 验证同一个部门层级下，是否有相同的部门
        if (checkExist(param.getParentId(),param.getName(),param.getId()) > 0){
            throw new ParamException("同一个部门层级下有相同部门");
        }

        SysDept after = SysDept.builder().id(param.getId()).name(param.getName()).parentId(param.getParentId())
                .seq(param.getSeq()).remark(param.getRemark()).build();

        // 设置新建部门的 层级：计算层级 = 父级的层级 + 父级的 id
        after.setLevel(LevelUtil.calculate(getLevel(param.getParentId()),param.getParentId())); // parentLevel.parentId

        after.setOperator(RequestHolder.getUser().getUsername());
        after.setOperateIp(IpUtil.getUserIP(RequestHolder.getRequest()));
        after.setOperateTime(new Date());

        // 更新
        updateWithChild(before,after);
        // 插入日志
        sysLogService.saveDeptLog(before,after);
    }

    // 更新子部门
    public void updateWithChild(SysDept before,SysDept after){
        // 用来存储获取的子部门
        List<SysDept> deptList = null;

        // 获取更新后的 level，判断是否相等
        String newLevel = after.getLevel();
        // 获取更新前的 level
        String oldLevel = before.getLevel();

        // 用于修改基础信息
        sysDeptMapper.updateByPrimaryKey(after);

        // 如果不相等，就需要更新子部门
        if (!newLevel.equals(oldLevel)){
            // 通过 Level 获取所有的子部门
            deptList = sysDeptMapper.getChildDeptListByLevel(oldLevel + "." +before.getId());
            if (deptList != null){
                // 循环遍历，给子部门设置新的 Level
                for (SysDept sysDept : deptList) {
                    SysDept sysDeptBefore = new SysDept();   // 0.1.2
                    // 拷贝
                    BeanUtils.copyProperties(sysDept,sysDeptBefore);

                    String level = sysDept.getLevel();  //  0.1.2
                    // 计算新的 level 值
                    level = newLevel + "." +after.getId();   // 0.12.2
                    sysDept.setLevel(level);

                    // 递归，更新子部门
                    updateWithChild(sysDeptBefore,sysDept);
                }
            }
            // 更新
            sysDeptMapper.updateByPrimaryKey(after);
        }
    }

    /**
     * 删除部门
     * @param deptId
     */
    public void delete(Integer deptId){

        // 用来存储获取的子部门
        List<SysDept> childDept = null;

        // 验证参数是否正确
        BeanValidator.check(deptId);
        // 根据 deptId 取出更新前的部门
        SysDept sysDept =  sysDeptMapper.selectByPrimaryKey(deptId);
        if (sysDept == null){
            throw new ParamException("待删除的部门不存在");
        }

        // 获取删除部门下的子部门
        childDept = sysDeptMapper.getChildDeptListByLevel(sysDept.getLevel() + "." + sysDept.getId());
        // 判断是否有子部门
        if (childDept.size() > 0){
            throw new ParamException("该部门下有子部门，不能删除");
        }else {
            sysDeptMapper.deleteByPrimaryKey(deptId);
            // 插入日志
            sysLogService.saveDeptLog(sysDept,null);
        }
    }

}
