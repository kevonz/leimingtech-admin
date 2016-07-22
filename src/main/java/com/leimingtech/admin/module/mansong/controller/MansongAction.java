package com.leimingtech.admin.module.mansong.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.leimingtech.core.common.Constants;
import com.leimingtech.core.common.StringUtils;
import com.leimingtech.core.base.BaseController;
import com.leimingtech.core.common.CommonConstants;
import com.leimingtech.service.utils.page.Pager;
import com.leimingtech.core.entity.base.ShopPMansong;
import com.leimingtech.core.entity.base.ShopPMansongRule;
import com.leimingtech.service.module.mansong.service.ShopPMansongService;
import com.leimingtech.service.module.mansongrule.service.ShopPMansongRuleService;

/**
 * 满就送Controller
 *
 * @author linjm
 * @version 2015-11-19
 */
@Slf4j
@Controller
@RequestMapping("/shopPMansong")
public class MansongAction extends BaseController {

	/** 满就送Service接口*/
	@Resource
	private ShopPMansongService shopPMansongService;
	/*满就送规则Service接口*/
	@Resource
	private ShopPMansongRuleService shopPMansongRuleService;
	
	/**
	 * 满就送列表
	 * 
	 * @param pageNo
	 * @return
	 */
	@RequestMapping("/list")
	public String list(Model model,
			@ModelAttribute ShopPMansong shopPMansong,
			@RequestParam(required = false, value = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(required = false, value = "state", defaultValue = "") Integer state,
			@RequestParam(required = false, value = "storeName", defaultValue = "") String storeName,
			@RequestParam(required = false, value = "mansongName", defaultValue = "") String mansongName) {

		Pager pager = new Pager();
		pager.setPageNo(pageNo);
		if(null != state){
			shopPMansong.setState(state);
		}
		if(StringUtils.isNotBlank(mansongName)){
			shopPMansong.setMansongName(mansongName);
		}
		if(StringUtils.isNotBlank(storeName)){
			shopPMansong.setStoreName(storeName);
		}
		pager.setCondition(shopPMansong);

		pager.setResult(shopPMansongService.findShopPMansongPagerList(pager));

		model.addAttribute("pager", pager);
		model.addAttribute("shopPMansong", shopPMansong);
		model.addAttribute("mansongName", mansongName);
		model.addAttribute("state", state);
		model.addAttribute("storeName", storeName);
		return "/mansong/shopPMansongList";
	}

	/**
	 * 通过id获取活动的详情
	 */
	@RequestMapping("/findById")
	public String findMansongDetailById(Model model,@RequestParam(required = true, value = "id") int mansongId){
		ShopPMansong shopPMansong = shopPMansongService.findShopPMansongById(mansongId);
		List<ShopPMansongRule> shopPMansongRules = shopPMansongRuleService.findShopPMansongRuleByMansongid(mansongId);
		model.addAttribute("shopPMansong", shopPMansong);
		model.addAttribute("shopPMansongRuleList", shopPMansongRules);
		return "/mansong/shopPMansongDetail";
	}
	
	
	/**
	 * 跳转至满就送新增或修改页面
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/forward")
	public String add(Model model ,@RequestParam(required = false, value = "id", defaultValue = "0") int id) {

		ShopPMansong shopPMansong = shopPMansongService.findShopPMansongById(id);

		if (shopPMansong == null) {
			shopPMansong = new ShopPMansong();
		}
		List<ShopPMansongRule> msRuleList = shopPMansongRuleService.findShopPMansongRuleByMansongid(shopPMansong.getMansongId());
		model.addAttribute("msRuleList", msRuleList);
		model.addAttribute("shopPMansong", shopPMansong);
		return "/mansong/shopPMansong";
	}

	/**
	 * 满就送修改或保存
	 * 
	 * @param goodsClass
	 * @param id
	 * @return
	 */
	@RequestMapping("/saveOrUpdate")
	public String saveOrUpdate(
			Model model, 
			@ModelAttribute ShopPMansong shopPMansong,
			@RequestParam(required = false, value = "id", defaultValue = "0") int id,
			HttpServletRequest request) {

//		if (!beanValidatorForModel(model, shopPMansong)) {
//			String referer = request.getHeader("Referer");
//			model.addAttribute("referer", referer);
//			return Constants.MSG_URL;
//		}
		
		if (id != 0) {
			shopPMansong.setMansongId(id);
			shopPMansongService.updateShopPMansong(shopPMansong);
			model.addAttribute("msg", "修改满就送成功");
		} else {
			shopPMansongService.saveShopPMansong(shopPMansong);
			model.addAttribute("msg", "保存满就送成功");
		}

		model.addAttribute("referer", CommonConstants.ADMIN_SERVER + "/shopPMansong/list");
		return Constants.MSG_URL;
	}

	/**
	 * 满就送删除
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/delete")
	public String delete(Model model,@RequestParam int id,
			HttpServletRequest request) {

		String referer = request.getHeader("Referer");
		
		shopPMansongService.deleteShopPMansongById(id);
		//同时还要删除满就送的规则
		shopPMansongRuleService.deleteShopPMansongRuleByMansongid(id);

		model.addAttribute("referer", referer);
		return Constants.MSG_URL;
	}
	
	
}