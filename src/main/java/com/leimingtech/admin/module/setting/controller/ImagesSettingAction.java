package com.leimingtech.admin.module.setting.controller;




import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.leimingtech.core.common.Constants;
import com.leimingtech.service.module.setting.service.SettingService;


@Controller
@RequestMapping("/setting/images")
@Slf4j
public class ImagesSettingAction {
	@Resource
	private SettingService settingService;
	
    /**
     * 跳转admin系统设置的图片设置
     * @param model
     * @return
     */
    @RequestMapping("/imagesSetting")
    public ModelAndView siteSetting(){
    	ModelAndView model = new ModelAndView("setting/points/images");
		Map<String,String> map = settingService.findByNameResultMap("images");
		model.addObject("map", map);
		return model;
    }
    
    /**
     * admin系统设置的图片设置保存
     * @param model
     * @return
     */
    @RequestMapping("/imagesSave")
    public String  imagesSave(HttpServletRequest request,Model model){
    	Enumeration<String> names = request.getParameterNames();
		Map<String,String> imagesMap = new HashMap<String, String>();
		while(names.hasMoreElements()){
		    	String name= names.nextElement();
	    		String paramValue = request.getParameter(name);
	    		imagesMap.put(name, paramValue);	
		    	
		 }
		settingService.updateSetting("images", imagesMap);
		
		String referer = request.getHeader("Referer");
		model.addAttribute("referer", referer);
		model.addAttribute("msg", "新增成功");
     	return Constants.MSG_URL;
	}
	
}
