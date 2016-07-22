//package com.leimingtech.admin.module.log.controller;
//
//import java.util.List;
//
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletRequest;
//
//import lombok.extern.slf4j.Slf4j;
//
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import com.leimingtech.core.common.Constants;
//import com.leimingtech.service.utils.page.Pager;
//
///**
// *    
// * 项目名称：leimingtech-admin   
// * 类名称：AdminLogAction   
// * 类描述：   后台日志操作
// * 创建人：liuhao   
// * 创建时间：2014年11月5日 下午10:41:09   
// * 修改人：liuhao   
// * 修改时间：2014年11月5日 下午10:41:09   
// * 修改备注：   
// * @version    
// *
// */
//@Controller
//@RequestMapping("/adminlog")
//@Slf4j
//public class AdminLogAction {
//	
//	String message = "success";
//	
//
//	/**
//	 * 导航至主操作页面
//	 * @Title: index 
//	 * @param @return    设定文件
//	 * @return String    返回类型 
//	 * @throws
//	 */
//	@RequestMapping("/index")
//	public String index(){
//		try{
//			return "/log/index";
//		}catch (Exception e) {
//			log.error("导航失败！", e);
//			throw new RuntimeException("导航失败!");
//		}
//	}
//	
//	/**
//	 * 
//	 * @Title: list 
//	 * @param @param model
//	 * @param @param div
//	 * @param @param pageNo
//	 * @param @param adminName
//	 * @param @param starttime
//	 * @param @param endtime
//	 * @param @return    设定文件 
//	 * @return String    返回类型 
//	 * @throws
//	 */
//	@RequestMapping(value = "/list")
//	public String list(Model model,
//			@RequestParam(required=false, value="pageNo",defaultValue="")String pageNo,
//			@RequestParam(required=false, value="adminName",defaultValue="")String adminName,
//			@RequestParam(required=false, value="starttime",defaultValue="")String starttime,
//			@RequestParam(required=false, value="endtime",defaultValue="")String endtime){
//		Pager pager = new Pager();
//		AdminLog adminLog = new AdminLog();
//		/**查询条件，放入实体中，**/
//		if (StringUtils.isNotBlank(adminName))
//			adminLog.setAdminName(adminName);
//		
//		if(StringUtils.isNotBlank(starttime))
//			adminLog.setStarttime(starttime);
//		
//		if(StringUtils.isNotBlank(endtime))
//			adminLog.setEndtime(endtime);
//		
//		if(StringUtils.isNotBlank(pageNo)){
//			pager.setPageNo(Integer.parseInt(pageNo));
//		}
//		
//		pager.setCondition(adminLog);//实体加载在pager中
//		List<AdminLog> results = adminLogService.queryAdminLogList(pager);//结果集
//		pager.setResult(results);
//		model.addAttribute("pager", pager);//总数
//		model.addAttribute("adminName", adminName);
//		model.addAttribute("starttime", starttime);
//		model.addAttribute("endtime", endtime);
//		//转发请求到FTL页面
//		return "/log/list";
//		
//	}
//	
//	
//	
//	 /**
//	  * 
//	  * @Title: delDemo 
//	  * @Description: TODO(这里用一句话描述这个方法的作用) 
//	  * @param @param ids
//	  * @param @param model
//	  * @param @return    设定文件 
//	  * @return Map<String,String>    返回类型 
//	  * @throws
//	  */
//    @RequestMapping(value = "/delLog", method = RequestMethod.POST)
//    public String delLog(
//    		@RequestParam(value="id")long[] ids, Model model,HttpServletRequest request) {
//        for (long id : ids) {
//        	adminLogService.delete(id);
//        }
//		String referer = request.getHeader("Referer");
//		model.addAttribute("referer", referer);
//		model.addAttribute("msg", "删除成功");
//		return Constants.MSG_URL;
//    }
//
//}