package com.xmcc.controller;

import com.xmcc.beans.PageBean;
import com.xmcc.model.SysLogWithBLOBs;
import com.xmcc.param.SearchLogParam;
import com.xmcc.service.SysLogService;
import com.xmcc.utils.JsonData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;

@Controller
@RequestMapping("/sys/log")
public class SysLogController {

    @Resource
    private SysLogService sysLogService;

    @RequestMapping("/log.page")
    public ModelAndView page(){
        return new ModelAndView("log");
    }

    @RequestMapping("/page.json")
    @ResponseBody
    public JsonData searchPage(SearchLogParam param, PageBean<SysLogWithBLOBs> pageBean){
        PageBean<SysLogWithBLOBs> bean = sysLogService.searchPageList(param, pageBean);
        return JsonData.success(bean);
    }

    @RequestMapping("/recover.json")
    @ResponseBody
    public JsonData recover(@RequestParam("id") Integer logId){
        sysLogService.recover(logId);
        return JsonData.success();
    }

}
