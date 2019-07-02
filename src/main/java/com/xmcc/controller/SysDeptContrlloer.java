package com.xmcc.controller;

import com.xmcc.dto.SysDeptLevelDto;
import com.xmcc.param.DeptParam;
import com.xmcc.service.SysDeptService;
import com.xmcc.service.SysTreeService;
import com.xmcc.utils.JsonData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping("/sys/dept")
public class SysDeptContrlloer {

    @Resource
    private SysDeptService sysDeptService;

    @Resource
    private SysTreeService sysTreeService;

    // 登录成功之后，进入
    @RequestMapping("/dept.page")
    public ModelAndView deptView(){
        return new ModelAndView("dept");
    }

    /**
     * 添加部门
     * @param param
     * @return
     */
    @RequestMapping("/save.json")
    @ResponseBody
    public JsonData save(DeptParam param){
        sysDeptService.save(param);
        return JsonData.success();
    }

    /**
     * 部门树
     * @return
     */
    @RequestMapping("/tree.json")
    @ResponseBody
    public JsonData tree(){
        List<SysDeptLevelDto> sysDeptLevelDtos = sysTreeService.deptTree();
        return JsonData.success(sysDeptLevelDtos);
    }

    /**
     * 更新部门
     * @param param
     * @return
     */
    @RequestMapping("/update.json")
    @ResponseBody
    public JsonData update(DeptParam param){
        sysDeptService.update(param);
        return JsonData.success();
    }

    /**
     * 删除部门
     * @param param
     * @return
     */
    @RequestMapping("/delete.json")
    @ResponseBody
    public JsonData delete(DeptParam param){
        sysDeptService.delete(param.getId());
        return JsonData.success();
    }
}
