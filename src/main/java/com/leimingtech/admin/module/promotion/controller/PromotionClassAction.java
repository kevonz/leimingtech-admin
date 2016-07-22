/**
 * 
 */
package com.leimingtech.admin.module.promotion.controller;

import java.util.List;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.leimingtech.core.common.CommonConstants;
import com.leimingtech.core.common.Constants;
import com.leimingtech.core.common.DateUtils;
import com.leimingtech.core.entity.base.PromotionClass;
import com.leimingtech.service.module.promotion.service.PromotionClassService;
import com.leimingtech.service.utils.page.Pager;

/**
 * <p>Title: PromotionAction.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2014-2018</p>
 * <p>Company: leimingtech.com</p>
 * @author linjm
 * @date 2015年7月21日
 * @version 1.0
 */
@Controller
@Slf4j
@RequestMapping("/promotionClass")
public class PromotionClassAction {
	
	@Resource
	private  PromotionClassService  promotionClassService;
	
	@RequiresPermissions("sys:promotionclass:view")
	@RequestMapping("/index")
	public ModelAndView index(@RequestParam(required=false ,value="pageNo" ,defaultValue="")String pageNo){
		ModelAndView mv = new ModelAndView("/promotion/classlist");
		
		Pager pager = new Pager();
		if(StringUtils.isNotEmpty(pageNo)){
			pager.setPageNo(Integer.valueOf(pageNo));
		}
//		int count = promotionClassService.findCount(pager);
		
		List<PromotionClass> list = promotionClassService.findList(pager);		
		
//		pager.setTotalRows(count);
		pager.setResult(list);
		mv.addObject("pager", pager);
		
		return mv;
	}
	
	@RequiresPermissions("sys:promotionclass:edit")
	@RequestMapping("/add")
	public String add(Model model,@RequestParam(required=false , value="pcId" ,defaultValue="")String pcId){
		int listSize = 0;
		List<PromotionClass> list = promotionClassService.findList(new Pager());
		if(list!=null&&list.size()>0){
			for(PromotionClass pc:list){
				if(0==pc.getPcStatus()){
					listSize++;
				}
			}
		}
		model.addAttribute("listSize", listSize);
		if(StringUtils.isNotEmpty(pcId)){
			PromotionClass promotionClass = promotionClassService.findById(Integer.valueOf(pcId));
			model.addAttribute("promotionClass", promotionClass);
			return "/promotion/classadd";
		}
		return "/promotion/classedit";
	}
	
	@RequiresPermissions("sys:promotionclass:edit")
	@RequestMapping("/saveOrUpdate")
	public String saveOrUpdate(Model model, @ModelAttribute PromotionClass promotionClass
			,@RequestParam(value="startStr")String startStr,@RequestParam(value="endStr")String endStr){
		if(promotionClass != null && promotionClass.getPcId()!= null){
			if(StringUtils.isNotEmpty(startStr))promotionClass.setStartTime(DateUtils.strToLong(startStr));
			
			if(StringUtils.isNotEmpty(endStr))promotionClass.setEndTime(DateUtils.strToLong(endStr));
			
			promotionClassService.update(promotionClass);
			model.addAttribute("msg", "修改成功");
		}else{
			if(StringUtils.isEmpty(startStr)){
				promotionClass.setStartTime(System.currentTimeMillis());
			}else{
				promotionClass.setStartTime(DateUtils.strToLong(startStr));
			}
			if(StringUtils.isEmpty(endStr)){
				promotionClass.setEndTime(System.currentTimeMillis()+(1000*60*60*24*30));
			}else{
				promotionClass.setEndTime(DateUtils.strToLong(endStr));
			}
			promotionClass.setCreateTime(System.currentTimeMillis());
			promotionClassService.save(promotionClass);
			model.addAttribute("msg", "保存成功");
		}
		
		model.addAttribute("referer", CommonConstants.ADMIN_SERVER + "/promotionClass/index");
		
		return Constants.MSG_URL;
	}
	
	@RequiresPermissions("sys:promotionclass:edit")
	@RequestMapping("/delete")
	public String delete(Model model,@RequestParam(required=false , value="pcId" ,defaultValue="")String pcId){
		if(StringUtils.isNotEmpty(pcId)){
			promotionClassService.delete(Integer.valueOf(pcId));
			model.addAttribute("msg", "删除成功");
		}
		model.addAttribute("referer", CommonConstants.ADMIN_SERVER + "/promotionClass/index");
		
		return Constants.MSG_URL;
	}

}
