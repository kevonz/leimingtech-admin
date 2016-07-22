package com.leimingtech.admin.module.log.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.leimingtech.core.common.Constants;
import com.leimingtech.core.common.DateUtils;
import com.leimingtech.core.entity.base.SysLog;
import com.leimingtech.service.module.log.service.SysLogService;
import com.leimingtech.service.utils.page.Pager;

/**
 * 日志管理
 * @author 
 */
@Controller
@RequestMapping("/syslog")
@Slf4j
public class SysLogAction {
	
	String message = "success";
	
	@Autowired
	private SysLogService sysLogService;
	
	/**
	 * 导航至主操作页面
	 * @Title: index 
	 * @param @return    设定文件
	 * @return String    返回类型 
	 * @throws
	 */
	@RequestMapping("/index")
	public String index(){
		try{
			return "/log/index";
		}catch (Exception e) {
			log.error("导航失败！", e);
			throw new RuntimeException("导航失败!");
		}
	}
	
	/**
	 * 
	 * @Title: list 
	 * @param @param model
	 * @param @param div
	 * @param @param pageNo
	 * @param @param adminName
	 * @param @param starttime
	 * @param @param endtime
	 * @param @return    设定文件 
	 * @return String    返回类型 
	 * @throws
	 */
	@RequiresPermissions("sys:log:view")
	@RequestMapping(value = "/list")
	public String list(Model model,
			@RequestParam(required=false, value="pageNo",defaultValue="")String pageNo,
			@RequestParam(required=false, value="createBy",defaultValue="")String createBy,
			@RequestParam(required=false, value="starttime",defaultValue="")String starttime,
			@RequestParam(required=false, value="endtime",defaultValue="")String endtime){
		Pager pager = new Pager();
		SysLog sysLog = new SysLog();
		/**查询条件，放入实体中，**/
		if (StringUtils.isNotBlank(createBy))
			sysLog.setCreateBy(createBy);
		
		if(StringUtils.isNotBlank(starttime)){
			sysLog.setStartTime(DateUtils.strToLong(starttime, "yyy-MM-dd"));
		}
			
		if(StringUtils.isNotBlank(endtime)){
			sysLog.setEndTime(DateUtils.strToLong(endtime, "yyy-MM-dd"));
		}
			
		if(StringUtils.isNotBlank(pageNo)){
			pager.setPageNo(Integer.parseInt(pageNo));
		}
		
		pager.setCondition(sysLog);//实体加载在pager中
		List<SysLog> results = sysLogService.querySysLogList(pager);//结果集
		pager.setResult(results);
		model.addAttribute("pager", pager);//总数
		model.addAttribute("createBy", createBy);
		model.addAttribute("starttime", starttime);
		model.addAttribute("endtime", endtime);
		//转发请求到FTL页面
		return "/log/list";
	}
	
	
	 /**
	  * @Title: del
	  * @Description: TODO(这里用一句话描述这个方法的作用) 
	  * @param @param ids
	  * @param @param model
	  * @param @return    设定文件 
	  * @return Map<String,String>    返回类型 
	  * @throws
	  */
	@RequiresPermissions("sys:log:edit")
    @RequestMapping(value = "/delLog", method = RequestMethod.POST)
    public String delLog(
    		@RequestParam(value="id")long[] ids, Model model,HttpServletRequest request) {
        for (long id : ids) {
        	sysLogService.delete(id);
        }
		String referer = request.getHeader("Referer");
		model.addAttribute("referer", referer);
		model.addAttribute("msg", "删除成功");
		return Constants.MSG_URL;
    }

}