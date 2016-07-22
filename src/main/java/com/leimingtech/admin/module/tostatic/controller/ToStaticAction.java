package com.leimingtech.admin.module.tostatic.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.leimingtech.service.module.tostatic.service.ToStaticService;
import com.leimingtech.service.utils.http.ToStaticSendToFront;

/**
 * 静态化页面action
 * @author cgl
 * 2015年08月10日11:26:42
 */
@Controller
@RequestMapping("/toStatic")
public class ToStaticAction {
	
	@Autowired
	ToStaticService toStaticService;
	
	/**
	 * 静态化页面的页面跳转
	 */
	@RequestMapping("/staticView")
	public String staticView(){
		return "/static/static-index";
	}
	
	/**
	 * 首页静态化
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/indexStatic.do")
	public String indexStatic(){
		try {
			ToStaticSendToFront.indexStatic();
			return "success";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "fail";
		}
	}
	
	/**
	 * 商品详细页静态化
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/goodsStatic.do")
	public String goodsStatic(){
		try {
			ToStaticSendToFront.goodsDetailBatchStatic();
			return "success";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "fail";
		}
	}
	
}
