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

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.leimingtech.core.common.Constants;
import com.leimingtech.service.module.setting.service.SettingService;

/**
 * <p>Title: CacheSetting.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2014-2018</p>
 * <p>Company: leimingtech.com</p>
 * @author linjm
 * @date 2015年9月28日
 * @version 1.0
 */
@Controller
@Slf4j
@RequestMapping("/dev/cache")
public class CacheSettingAction {
	
	@Resource
	private SettingService settingService;
	
	/**
	 * 初始化缓存设置,无页面
	 */
	//@RequiresPermissions("sys:cache:edit")
	@RequestMapping("/save")
    public void save(){
    	Map<String,String> map = new HashMap<String, String>();
    	map.put("adv_seconds", "50"); //广告缓存时间
    	map.put("adv_isShow", "0"); //广告缓存状态
    	map.put("category_seconds", "20"); //分类缓存时间
    	map.put("category_isShow", "1"); //分类缓存状态
    	map.put("dic_seconds", "20"); //数据字典缓存时间
    	map.put("dic_isShow", "0"); //数据字典缓存状态
    	map.put("area_seconds", "5"); //地区缓存时间
    	map.put("area_isShow", "1"); //地区缓存状态
    	map.put("other_seconds", "5"); //其他缓存时间
    	map.put("other_isShow", "1"); //其他缓存状态
    	Map<String,Map<String,String>> mapsMap = new HashMap<String, Map<String,String>>();
    	mapsMap.put("redisCache", map);
    	settingService.saveSetting(mapsMap);
    }
	
	//@RequiresPermissions("sys:cache:view")
	@RequestMapping("/setting")
	public ModelAndView setting(){
		ModelAndView model = new ModelAndView("/setting/points/cachesetting");
		Map<String,String> map = settingService.findByNameResultMap("redisCache");
		model.addObject("map", map);
		return model;
	}
	
	/**
	 * 修改缓存设置
	 * @param jsonData 修改缓存json
	 * @return
	 */
	//@RequiresPermissions("sys:cache:edit")
	@RequestMapping("/update")
	public String update(HttpServletRequest request,Model model){
		//取到数据库中原有的积分设置
		Map<String,String> oldMap = settingService.findByNameResultMap("redisCache");
		Set<String> oldSet = oldMap.keySet();
		for(String oldStr:oldSet){
			String redisCache = request.getParameter(oldStr);
			if(!(oldMap.get(oldStr).equals(redisCache))){
				oldMap.put(oldStr, redisCache); //将原有的键对应的值替换成新的
			}
		}
		settingService.updateSetting("redisCache", oldMap); //将替换完成的map存入数据库
        model.addAttribute("referer", "setting");
        model.addAttribute("msg", "修改成功");
        return Constants.MSG_URL;
	}

}
