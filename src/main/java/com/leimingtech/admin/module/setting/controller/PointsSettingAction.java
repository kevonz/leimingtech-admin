package com.leimingtech.admin.module.setting.controller;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.leimingtech.core.common.Constants;
import com.leimingtech.core.jackson.JsonUtils;
import com.leimingtech.service.module.setting.service.SettingService;

@Controller
@RequestMapping("/setting/points")
@Slf4j
public class PointsSettingAction {
	
	@Resource
	private SettingService settingService;
	
	/**
	 * 初始化积分设置,无页面
	 */
	@RequiresPermissions("sys:setting:edit")
	@RequestMapping("/save")
    public void save(){
    	Map<String,String> map = new HashMap<String, String>();
    	map.put("register_rank", "50"); //成功注册等级积分
    	map.put("register_cons", "50"); //成功注册消费积分
    	map.put("email_rank", "20"); //完成邮箱验证等级积分
    	map.put("email_cons", "20"); //完成邮箱验证消费积分
    	map.put("persdata_rank", "20"); //完善个人资料等级积分
    	map.put("persdata_cons", "20"); //完善个人资料消费积分
    	map.put("login_rank", "5"); //登录等级积分
    	map.put("login_cons", "5"); //登录消费积分
    	map.put("comment_rank", "10"); //文字评论等级积分
    	map.put("comment_cons", "10"); //文字评论消费积分
    	map.put("uppiccom_rank", "30"); //上传图片评论等级积分
    	map.put("uppiccom_cons", "30"); //上传图片评论消费积分
    	map.put("goodsfirstcom_rank", "50"); //每个商品首次评论等级积分
    	map.put("goodsfirstcom_cons", "50"); //每个商品首次评论消费积分
    	map.put("buygoods_rank", "1"); //购买商品(一元等于多少积分)等级积分
    	map.put("buygoods_cons", "1"); //购买商品(一元等于多少积分)消费积分
    	map.put("onlinepay_rank", "10"); //选择网上支付等级积分
    	map.put("onlinepay_cons", "10"); //选择网上支付消费积分
    	map.put("recfriend_rank", "0"); //推荐好友等级积分
    	map.put("recfriend_cons", "50"); //推荐好友消费积分
    	map.put("sign_rank", "5"); //签到等级积分
    	map.put("sign_cons", "5"); //签到消费积分
    	map.put("recharge_rank", "0"); //充值(一元等于多少积分)等级积分
    	map.put("recharge_cons", "50"); //充值(一元等于多少积分)消费积分
    	Map<String,Map<String,String>> mapsMap = new HashMap<String, Map<String,String>>();
    	mapsMap.put("points", map);
    	settingService.saveSetting(mapsMap);
    }
	
	/**
	 * 积分设置首页
	 * @return
	 */
	@RequiresPermissions("sys:setting:view")
	@RequestMapping("/index")
	public ModelAndView index(){
		ModelAndView model = new ModelAndView("/setting/points/index");
		Map<String,String> map = settingService.findByNameResultMap("points");
		model.addObject("map", map);
		return model;
	}
	
	/**
	 * 修改积分设置
	 * @param jsonData 修改积分json
	 * @return
	 */
	@RequiresPermissions("sys:setting:edit")
	@RequestMapping("/update")
	public String update(HttpServletRequest request,Model model){
		//取到数据库中原有的积分设置
		Map<String,String> oldMap = settingService.findByNameResultMap("points");
		Set<String> oldSet = oldMap.keySet();
		for(String oldStr:oldSet){
			String points = request.getParameter(oldStr);
			if(!(oldMap.get(oldStr).equals(points))){
				oldMap.put(oldStr, points); //将原有的键对应的值替换成新的
			}
		}
		settingService.updateSetting("points", oldMap); //将替换完成的map存入数据库
        model.addAttribute("referer", "index");
        model.addAttribute("msg", "修改成功");
        return Constants.MSG_URL;
	}
}
