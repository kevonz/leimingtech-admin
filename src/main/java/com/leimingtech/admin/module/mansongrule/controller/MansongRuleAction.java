package com.leimingtech.admin.module.mansongrule.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.leimingtech.core.common.Constants;
import com.leimingtech.core.base.BaseController;
import com.leimingtech.core.common.CommonConstants;
import com.leimingtech.service.utils.page.Pager;
import com.leimingtech.core.entity.base.ShopPMansongRule;
import com.leimingtech.service.module.mansongrule.service.ShopPMansongRuleService;

/**
 * 满就送活动规则Controller
 *
 * @author linmj
 * @version 2015-11-19
 */
@Slf4j
@Controller
@RequestMapping("/shopPMansongRule")
public class MansongRuleAction extends BaseController {

	/** 满就送活动规则Service接口*/
	@Resource
	private ShopPMansongRuleService shopPMansongRuleService;
	
	/**
	 * 满就送活动规则列表
	 * 
	 * @param pageNo
	 * @return
	 */
	@RequestMapping("/list")
	public String list(Model model,@RequestParam(required = false, value = "pageNo", defaultValue = "1") int pageNo) {

		Pager pager = new Pager();
		pager.setPageNo(pageNo);

		pager.setResult(shopPMansongRuleService.findShopPMansongRulePagerList(pager));

		model.addAttribute("pager", pager);
		return "/mansongrule/shopPMansongRuleList";
	}

	/**
	 * 跳转至满就送活动规则新增或修改页面
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/forward")
	public String add(Model model ,@RequestParam(required = false, value = "id", defaultValue = "0") int id) {

		ShopPMansongRule shopPMansongRule = shopPMansongRuleService.findShopPMansongRuleById(id);

		if (shopPMansongRule == null) {
			shopPMansongRule = new ShopPMansongRule();
		}

		model.addAttribute("shopPMansongRule", shopPMansongRule);
		return "/mansongrule/shopPMansongRule";
	}

	/**
	 * 满就送活动规则修改或保存
	 * 
	 * @param goodsClass
	 * @param id
	 * @return
	 */
	@RequestMapping("/saveOrUpdate")
	public String saveOrUpdate(
			Model model, 
			@ModelAttribute ShopPMansongRule shopPMansongRule,
			@RequestParam(required = false, value = "id", defaultValue = "0") int id,
			HttpServletRequest request) {

		if (!beanValidatorForModel(model, shopPMansongRule)) {
			String referer = request.getHeader("Referer");
			model.addAttribute("referer", referer);
			return Constants.MSG_URL;
		}
		
		if (id != 0) {
			shopPMansongRuleService.updateShopPMansongRule(shopPMansongRule);
			model.addAttribute("msg", "修改满就送活动规则成功");
		} else {
			shopPMansongRuleService.saveShopPMansongRule(shopPMansongRule);
			model.addAttribute("msg", "保存满就送活动规则成功");
		}

		model.addAttribute("referer", CommonConstants.ADMIN_SERVER + "/shopPMansongRule/list");
		return Constants.MSG_URL;
	}

	/**
	 * 满就送活动规则删除
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/delete")
	public String delete(Model model,@RequestParam int[] ids,
			HttpServletRequest request) {

		String referer = request.getHeader("Referer");
		for (int id : ids) {
			shopPMansongRuleService.deleteShopPMansongRuleById(id);
		}

		model.addAttribute("referer", referer);
		return Constants.MSG_URL;
	}
	
	
}