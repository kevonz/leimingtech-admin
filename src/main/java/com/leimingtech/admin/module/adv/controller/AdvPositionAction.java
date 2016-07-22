/**
 * 
 */
package com.leimingtech.admin.module.adv.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.leimingtech.core.common.CommonConstants;
import com.leimingtech.core.common.Constants;
import com.leimingtech.core.entity.base.AdvPosition;
import com.leimingtech.service.module.adv.service.AdvPositionService;
import com.leimingtech.service.utils.page.Pager;

/**
 * <p>Title: AdvPositionAction.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2014-2018</p>
 * <p>Company: leimingtech.com</p>
 * @author linjm
 * @date 2015年7月7日
 * @version 1.0
 */
@Controller
@Slf4j
@RequestMapping("/advPosition")
public class AdvPositionAction {
	
	@Resource
	private AdvPositionService advPositionService;
	
	@RequiresPermissions("sys:advposition:view")
	@RequestMapping("/list")
	public String list(Model model,
		@RequestParam(required=false, value="pageNo",defaultValue="")String pageNo,
		@RequestParam(required=false, value="isUse",defaultValue="-1")int isUse,
		@RequestParam(required=false, value="apDisplay",defaultValue="-1")int apDisplay,
		@RequestParam(required=false, value="apClass",defaultValue="-1")int apClass){
		Pager pager = new Pager();
		/**查询条件，放入实体中，**/
		AdvPosition advPosition = new AdvPosition();
		if(StringUtils.isNotBlank(pageNo)){
			pager.setPageNo(Integer.parseInt(pageNo));
		}
		if(isUse!=-1){
			advPosition.setIsUse(isUse);
		}
		if(apDisplay!=-1){
			advPosition.setApDisplay(apDisplay);
		}
		if(apClass!=-1){
			advPosition.setApClass(apClass);
		}
		pager.setCondition(advPosition);//实体加载在pager中
		List<AdvPosition> results = advPositionService.findAdvPositionPagerList(pager);
		pager.setResult(results);
		model.addAttribute("pager", pager);//总数
		//转发请求到FTL页面
		return "/advposition/list";
	}
	
	@RequiresPermissions("sys:advposition:edit")
	@RequestMapping("/forward")
	public String add(Model model,@RequestParam(required=false, value="apId",defaultValue="0") int apId){
		if(apId!=0){
			AdvPosition advPosition = advPositionService.findAdvPositionById(apId);
			model.addAttribute("advPosition",advPosition);
			return "/advposition/edit";
		}else{
			return "/advposition/add";
		}
	}
	
	@RequiresPermissions("sys:advposition:edit")
	@RequestMapping("/saveOrUpdate")
	public String saveOrUpdate(@ModelAttribute AdvPosition advPosition,
			HttpServletRequest request, HttpServletResponse response,
			Model model, @RequestParam(required = false, value = "div", defaultValue = "") String div){
		if(advPosition.getApId() != null){
			advPositionService.update(advPosition);
			model.addAttribute("msg", "修改成功");
		}else{
			advPositionService.save(advPosition);
			model.addAttribute("msg", "保存成功");
		}
		model.addAttribute("referer", CommonConstants.ADMIN_SERVER+"/advPosition/list");
		return Constants.MSG_URL;
	}
	
	@RequiresPermissions("sys:advposition:edit")
	@RequestMapping("/delete")
	public String delete(Model model,@RequestParam(required=false ,value="apid" ,defaultValue="0")int apid){
		model.addAttribute("msg", "删除失败");
		try {
			if(apid!=0){
				advPositionService.delete(apid);
				model.addAttribute("msg", "删除成功");
			}
			model.addAttribute("referer", CommonConstants.ADMIN_SERVER+"/advPosition/list");
		} catch (Exception e) {
			log.error("导航失败", e);
			throw new RuntimeException();
		}
		return Constants.MSG_URL;
	}

}
