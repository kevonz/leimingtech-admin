package com.leimingtech.admin.module.setting.controller;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.leimingtech.core.common.CommonConstants;
import com.leimingtech.core.common.Constants;
import com.leimingtech.service.module.setting.service.SettingService;

/**
 * 系统设置
 * @author zhaorh
 */
@Controller
@RequestMapping("/setting/site")
@Slf4j
public class SiteSettingAction {
	@Resource
	private SettingService settingService;
	
    /**
     * 跳转admin系统设置的站点设置
     * @param model
     * @return
     */
    @RequestMapping("/siteSetting")
    public ModelAndView siteSetting(){
    	ModelAndView model = new ModelAndView("setting/points/site");
		Map<String,String> map = settingService.findByNameResultMap("site");
		model.addObject("map", map);
		return model;
    }
    
    /**
     * admin系统设置的站点设置保存
     * @param model
     * @return
     */
    @RequestMapping("/siteSave")
    public String  siteSave(@RequestParam("logo") MultipartFile[] logo,
    						HttpServletRequest request,Model model){
    	Enumeration<String> names = request.getParameterNames();
		Map<String,String> siteMap = new HashMap<String, String>();
		try {
			Map<String,Object> map = com.leimingtech.core.common.FileUtils.fileUpload(logo,  CommonConstants.FILE_BASEPATH, Constants.SITE_LOGO_URL, request,"images",1);
			siteMap.put("logo", (String)map.get("result"));
			while(names.hasMoreElements()){
		    	String name= names.nextElement();
	    		String paramValue = request.getParameter(name);
	    		siteMap.put(name, paramValue);	
		    	
			}
			settingService.updateSetting("site", siteMap);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		String referer = request.getHeader("Referer");
		model.addAttribute("referer", referer);
		model.addAttribute("msg", "新增成功");
     	return Constants.MSG_URL;
	}
	
}
