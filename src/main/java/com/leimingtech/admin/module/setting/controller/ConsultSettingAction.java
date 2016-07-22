/**
 * 
 */
package com.leimingtech.admin.module.setting.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.leimingtech.core.common.Constants;
import com.leimingtech.service.module.setting.service.SettingService;

/**
 * @author zhaorh
 * @date 2015年11月1日
 * @version 1.0
 */
@Controller
@Slf4j
@RequestMapping("/setting/consult")
public class ConsultSettingAction {
	
	@Resource
	private SettingService settingService;
	
	/**
	 * 初始化咨询设置,无页面
	 */
	@RequestMapping("/save")
    public void save(){
    	Map<String,String> map = new HashMap<String, String>();
    	map.put("consult_isShow", "1"); //设置咨询开启
    	Map<String,Map<String,String>> mapsMap = new HashMap<String, Map<String,String>>();
    	mapsMap.put("consult", map);
    	settingService.saveSetting(mapsMap);
    }
	
	@RequestMapping("/setting")
	public ModelAndView setting(){
		ModelAndView model = new ModelAndView("/setting/points/consultsetting");
		Map<String,String> map = settingService.findByNameResultMap("consult");
		model.addObject("map", map);
		return model;
	}
	
	/**
	 * 修改咨询设置
	 * @param jsonData 修改缓存json
	 * @return
	 */
	@RequestMapping("/update")
	public String update(HttpServletRequest request,Model model){
		//取到数据库中原有的咨询设置
		Map<String,String> oldMap = settingService.findByNameResultMap("consult");
		Set<String> oldSet = oldMap.keySet();
		for(String oldStr:oldSet){
			String consult = request.getParameter(oldStr);
			if(!(oldMap.get(oldStr).equals(consult))){
				oldMap.put(oldStr, consult); //将原有的键对应的值替换成新的
			}
		}
		settingService.updateSetting("consult", oldMap); //将替换完成的map存入数据库
        model.addAttribute("referer", "setting");
        model.addAttribute("msg", "修改成功");
        return Constants.MSG_URL;
	}

}
