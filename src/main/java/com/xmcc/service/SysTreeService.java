package com.xmcc.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.xmcc.dao.SysAclMapper;
import com.xmcc.dao.SysAclModuleMapper;
import com.xmcc.dao.SysDeptMapper;
import com.xmcc.dto.SysAclDto;
import com.xmcc.dto.SysAclModuleLevelDto;
import com.xmcc.dto.SysDeptLevelDto;
import com.xmcc.model.SysAcl;
import com.xmcc.model.SysAclModule;
import com.xmcc.model.SysDept;
import com.xmcc.utils.LevelUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * 用来生成树的
 */

@Service
public class SysTreeService {

    @Resource
    private SysDeptMapper sysDeptMapper;

    @Resource
    private SysAclModuleMapper sysAclModuleMapper;


    @Resource
    private SysAclMapper sysAclMapper;

    @Resource
    private SysCoreService sysCoreService;
    // 顺序 ：Controller ---  service -- dao


    /**
     * 角色树
     * @param roleId
     * @return
     */
    public List<SysAclModuleLevelDto> roleTree(Integer roleId){
        // 获取当前用户拥有的权限
        List<SysAcl> userAclList = sysCoreService.getUserAclList();
        // 获取角色对应的权限
        List<SysAcl> roleAclList = sysCoreService.getRoleAclList(roleId);
        // 获取 所有权限点
        List<SysAcl> all = sysAclMapper.getAll();

        //  存储 SysAclDto
        ArrayList<SysAclDto> aclDtoList = new ArrayList<>();

        // 遍历所有权限点，匹配用户拥有的权限和角色拥有的权限，修改相关属性（checked  和 hasAcl 操作）
        for (SysAcl sysAcl : all) {
            // 进行转换，转换之后才有：checked  和 hasAcl 属性
            SysAclDto aclDto = SysAclDto.adapter(sysAcl);
            // 如果当前用户有操作权限
            if (userAclList.contains(sysAcl)){
                aclDto.setHasAcl(true);
            }
            // 如果角色拥有权限
            if (roleAclList.contains(sysAcl)){
                aclDto.setChecked(true);
            }
            aclDtoList.add(aclDto);
        }
        // 封装树
        return aclListToTree(aclDtoList);
    }

    public List<SysAclModuleLevelDto> aclListToTree( ArrayList<SysAclDto> aclDtoList){
        if (aclDtoList == null){
            return new ArrayList<>();
        }
        // 将权限点封装到对应的权限模块中
        // 获取权限模块树
        List<SysAclModuleLevelDto> aclModuleLevelDtos = aclModlueTree();
        // 按权限封装数据（前面放的是自己，后面放的是对应的权限点）
        Multimap<Integer,SysAclDto> aclModuleAclMap = ArrayListMultimap.create();
        // 遍历，封装数据
        for (SysAclDto sysAclDto : aclDtoList) {
            // 判断权限的状态
            if (sysAclDto.getStatus() == 1){
                aclModuleAclMap.put(sysAclDto.getAclModuleId(),sysAclDto);
            }
        }

        // 递归绑定权限点和权限模块
        bindAcls(aclModuleLevelDtos,aclModuleAclMap);
        return aclModuleLevelDtos;
    }

    // 递归绑定权限点和权限模块
    public void bindAcls( List<SysAclModuleLevelDto> aclModuleLevelDtos,Multimap<Integer,SysAclDto> map){
        if (aclModuleLevelDtos == null){
            return;
        }
        // 遍历权限模块树，封装数据
        for (SysAclModuleLevelDto dto : aclModuleLevelDtos) {
            // 通过权限模块 id 从 map 中 取出 权限点集合
            List<SysAclDto> aclDtos = (List<SysAclDto>) map.get(dto.getId());
            // 根据 seq 排序
            Collections.sort(aclDtos,new MyComparatorAclTree());
            // 将取出的集合设置到权限模块树种
            dto.setAclList(aclDtos);
            // 递归
            bindAcls(dto.getAclModuleList(),map);
        }
    }
    // 根据 seq 排序
    public class MyComparatorAclTree implements Comparator<SysAclDto>{
        @Override
        public int compare(SysAclDto o1, SysAclDto o2) {
            return o1.getSeq() - o2.getSeq();
        }
    }


    /**
     * 生成 部门树
     * @return
     */
    public List<SysDeptLevelDto> deptTree(){
        // 获取当前所有的部门
        List<SysDept> deptList = sysDeptMapper.findAll();
        // 用来 存储所有部门的 dto
        List<SysDeptLevelDto> deptDtoList = new ArrayList<>();
        // 将 dept 转换成 deptDto
        for (SysDept sysDept : deptList) {
            // 将 sysDept 中的字段 拷贝进 dto
            SysDeptLevelDto dto = SysDeptLevelDto.adapter(sysDept);
            // 在将 dto 中的字段 添加到 dto的 List 集合中
            deptDtoList.add(dto);
        }
        return deptDtoListToTree(deptDtoList);
    }

    // 封装 dto
    public List<SysDeptLevelDto> deptDtoListToTree( List<SysDeptLevelDto> deptDtoList){
        if (deptDtoList == null){
            return new ArrayList<SysDeptLevelDto>();
        }
        // 按部门封装数据
        /**
         * Multimap：如果 key 相同，那么 value 会被封装成一个集合
         */
        Multimap<String,SysDeptLevelDto> map = ArrayListMultimap.create();
        // 创建集合存储顶层部门
        List<SysDeptLevelDto> rootList = new ArrayList<>();
        // 遍历集合封装顶层集合
        for (SysDeptLevelDto dto : deptDtoList) {
            // 判断是不是顶层
            if (LevelUtil.ROOT.equals(dto.getLevel())){
                // 是顶层，就将 dto 放到 存储顶层的 rootList 集合中
                rootList.add(dto);
            }
            // 如果不是顶层，就放到 map 中
            map.put(dto.getLevel(),dto);
        }
        // 部门根据 seq 排序
        Collections.sort(rootList,new MyComparator());
        // 递归生成树
        recursionDeptTree(rootList,map);
        return rootList;
    }

    // 递归生成树
    public void recursionDeptTree(List<SysDeptLevelDto> rootList,Multimap<String,SysDeptLevelDto> map){
        // 遍历该层级的每一个元素
        for (int i = 0; i < rootList.size(); i++) {
            SysDeptLevelDto deptLevelDto = rootList.get(i);
            // 处理当前层级的数据

            // 获取下一层的 Level，获取的是自己的 id 和 Level
            String nextLevel = LevelUtil.calculate(deptLevelDto.getLevel(),deptLevelDto.getId());
            // 处理下一层id（顶层的下一层数据）
            List<SysDeptLevelDto> sysDeptLevelDtos = (List<SysDeptLevelDto>) map.get(nextLevel);
            if (sysDeptLevelDtos != null){
                // 部门根据 seq 排序
                Collections.sort(sysDeptLevelDtos,new MyComparator());
                // 设置下一层部门
                deptLevelDto.setDeptList(sysDeptLevelDtos);
                // 递归
                recursionDeptTree(sysDeptLevelDtos,map);
            }
        }
    }

    // 部门 根据 seq 排序
    public class MyComparator implements Comparator<SysDeptLevelDto>{
        @Override
        public int compare(SysDeptLevelDto o1, SysDeptLevelDto o2) {
            return o1.getSeq() - o2.getSeq();
        }
    }


    /**
     * 生成权限树
     * @return
     */
    public List<SysAclModuleLevelDto> aclModlueTree(){
        // 获取当前所有的权限
        List<SysAclModule> aclModuleList = sysAclModuleMapper.findAllAclModlue();
        // 用来 存储所有权限的 dto
        List<SysAclModuleLevelDto> aclModuleDtoList = new ArrayList<>();
        // 将 sysAclModule 转换成 sysAclModuleDto
        for (SysAclModule sysAclModule : aclModuleList) {
            // 将 sysAclModule 中的字段 拷贝进 dto
            SysAclModuleLevelDto dto = SysAclModuleLevelDto.adapter(sysAclModule);
            // 在将 dto 中的字段 添加到 dto的 List 集合中
            aclModuleDtoList.add(dto);
        }
        return aclModuleDtoToTree(aclModuleDtoList);
    }

    // 封装 dto
    public List<SysAclModuleLevelDto> aclModuleDtoToTree(List<SysAclModuleLevelDto> aclModuleDtoList){
        if (aclModuleDtoList == null){
            return new ArrayList<SysAclModuleLevelDto>();
        }
        // 按权限封装数据
        Multimap<String,SysAclModuleLevelDto> map = ArrayListMultimap.create();
        // 创建集合存储顶层权限
        List<SysAclModuleLevelDto> rootList = new ArrayList<>();
        // 遍历集合封装顶层集合
        for (SysAclModuleLevelDto dto : aclModuleDtoList) {
            // 判断是不是顶层权限
            if (LevelUtil.ROOT.equals(dto.getLevel())){
                // 是顶层，就将 dto 放到 存储顶层的 rootList 集合中
                rootList.add(dto);
            }
            // 如果不是顶层，就放到 map 中
            map.put(dto.getLevel(),dto);
        }
        // 权限 按 seq 排序
        Collections.sort(rootList,new MyComparatorAclModlue());
        // 生成递归树
        recursionAclModlueTree(rootList,map);
        return rootList;
    }

    // 递归生成树
    public void recursionAclModlueTree(List<SysAclModuleLevelDto> rootList,Multimap<String,SysAclModuleLevelDto> map){
        // 遍历该层级的每一个元素
        for (int i = 0; i < rootList.size(); i++) {
            SysAclModuleLevelDto aclModuleLevelDto = rootList.get(i);
            // 处理当前层级的数据
            // 获取下一层的 Level，获取的是自己的 id 和 Level
            String nextLevel = LevelUtil.calculate(aclModuleLevelDto.getLevel(),aclModuleLevelDto.getId());
            // 处理下一层id（顶层的下一层数据）
            List<SysAclModuleLevelDto> sysAclModuleLevelDtos = (List<SysAclModuleLevelDto>) map.get(nextLevel);
            if (sysAclModuleLevelDtos != null){
                // 权限 按 seq 排序
                Collections.sort(rootList,new MyComparatorAclModlue());
                // 设置下一层权限
                aclModuleLevelDto.setAclModuleList(sysAclModuleLevelDtos);
                // 递归
                recursionAclModlueTree(sysAclModuleLevelDtos,map);
            }
        }
    }

    // 权限 按 seq 排序
    public class MyComparatorAclModlue implements Comparator<SysAclModuleLevelDto>{
        @Override
        public int compare(SysAclModuleLevelDto o1, SysAclModuleLevelDto o2) {
            return o1.getSeq() - o2.getSeq();
        }
    }


}
