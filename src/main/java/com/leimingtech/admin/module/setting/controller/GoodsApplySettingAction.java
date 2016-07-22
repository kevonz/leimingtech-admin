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
@RequestMapping("/setting/goodsApply")
public class GoodsApplySettingAction {
	
	@Resource
	private SettingService settingService;
	
	/**
	 * 初始化缓存设置,无页面
	 */
	@RequestMapping("/save")
    public void save(){
    	Map<String,String> map = new HashMap<String, String>();
    	map.put("goods_isApply", "0"); //其他缓存状态
    	Map<String,Map<String,String>> mapsMap = new HashMap<String, Map<String,String>>();
    	mapsMap.put("goods_isApply", map);
    	settingService.saveSetting(mapsMap);
    }
	
	@RequestMapping("/setting")
	public ModelAndView setting(){
		ModelAndView model = new ModelAndView("/setting/points/goodsApplySetting");
		Map<String,String> map = settingService.findByNameResultMap("goods_isApply");
		model.addObject("map", map);
		return model;
	}
	
	/**
	 * 修改缓存设置
	 * @param jsonData 修改缓存json
	 * @return
	 */
	@RequestMapping("/update")
	public String update(HttpServletRequest request,Model model){
		//取到数据库中原有的积分设置
		Map<String,String> oldMap = settingService.findByNameResultMap("goods_isApply");
		Set<String> oldSet = oldMap.keySet();
		for(String oldStr:oldSet){
			String goods_isApply = request.getParameter(oldStr);
			if(!(oldMap.get(oldStr).equals(goods_isApply))){
				oldMap.put(oldStr, goods_isApply); //将原有的键对应的值替换成新的
			}
		}
		settingService.updateSetting("goods_isApply", oldMap); //将替换完成的map存入数据库
        model.addAttribute("referer", "setting");
        model.addAttribute("msg", "修改成功");
        return Constants.MSG_URL;
	}

}
