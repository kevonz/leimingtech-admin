package com.leimingtech.admin.module.setting.controller;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.leimingtech.core.common.Constants;
import com.leimingtech.service.module.setting.service.SettingService;

/**
 * 结算时间设置
 * @author liukai
 */
@Controller
@RequestMapping("/setting/bill")
public class BillSettingAction {
	
	@Resource
	private SettingService settingService;
	
	/**
	 * 结算时间设置首页
	 * @return
	 */
	@RequiresPermissions("sys:billSetting:view")
	@RequestMapping("/index")
	public String index(Model model){
		Map<String,String> map = settingService.findByNameResultMap("bill");
		model.addAttribute("map", map);
		return "/setting/bill/index";
	}
	
	/**
	 * 修改结算时间设置
	 * @param jsonData 修改积分json
	 * @return
	 */
	@RequiresPermissions("sys:billSetting:edit")
	@RequestMapping("/update")
	public String update(HttpServletRequest request,Model model){
		//取到数据库中原有的积分设置
		Map<String,String> oldMap = settingService.findByNameResultMap("bill");
		//判断是否存在设置
		if(oldMap!=null){ // 
			Set<String> oldSet = oldMap.keySet();
			for(String oldStr:oldSet){
				String value = request.getParameter(oldStr);
				if(!(oldMap.get(oldStr).equals(value))){
					oldMap.put(oldStr, value); //将原有的键对应的值替换成新的
				}
			}
			settingService.updateSetting("bill", oldMap); //将替换完成的map存入数据库
		}else{
			Map<String,String> map = new HashMap<String, String>();
			Enumeration<String> names = request.getParameterNames();
			while(names.hasMoreElements()){
		    	String name= names.nextElement();
	    		String paramValue = request.getParameter(name);
	    		map.put(name, paramValue);
			}
			Map<String,Map<String,String>> mapsMap = new HashMap<String, Map<String,String>>();
			mapsMap.put("bill", map);
			settingService.saveSetting(mapsMap); //将替换完成的map存入数据库
		}
		
        model.addAttribute("referer", "index");
        model.addAttribute("msg", "设置成功");
        return Constants.MSG_URL;
	}
}
