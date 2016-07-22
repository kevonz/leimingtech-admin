package com.leimingtech.admin.module.mansongapply.controller;

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
import com.leimingtech.core.entity.base.ShopPMansongApply;
import com.leimingtech.service.module.mansongapply.service.ShopPMansongApplyService;

/**
 * 满就送套餐申请Controller
 *
 * @author admin
 * @version 2015-11-19
 */
@Slf4j
@Controller
@RequestMapping("/shopPMansongApply")
public class MansongApplyAction extends BaseController {

	/** 满就送套餐申请Service接口*/
	@Resource
	private ShopPMansongApplyService shopPMansongApplyService;
	
	/**
	 * 满就送套餐申请列表
	 * 
	 * @param pageNo
	 * @return
	 */
	@RequestMapping("/list")
	public String list(Model model,@RequestParam(required = false, value = "pageNo", defaultValue = "1") int pageNo) {

		Pager pager = new Pager();
		pager.setPageNo(pageNo);

		pager.setResult(shopPMansongApplyService.findShopPMansongApplyPagerList(pager));

		model.addAttribute("pager", pager);
		return "/mansongapply/shopPMansongApplyList";
	}

	/**
	 * 跳转至满就送套餐申请新增或修改页面
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/forward")
	public String add(Model model ,@RequestParam(required = false, value = "id", defaultValue = "0") int id) {

		ShopPMansongApply shopPMansongApply = shopPMansongApplyService.findShopPMansongApplyById(id);

		if (shopPMansongApply == null) {
			shopPMansongApply = new ShopPMansongApply();
		}

		model.addAttribute("shopPMansongApply", shopPMansongApply);
		return "/mansongapply/shopPMansongApply";
	}

	/**
	 * 满就送套餐申请修改或保存
	 * 
	 * @param goodsClass
	 * @param id
	 * @return
	 */
	@RequestMapping("/saveOrUpdate")
	public String saveOrUpdate(
			Model model, 
			@ModelAttribute ShopPMansongApply shopPMansongApply,
			@RequestParam(required = false, value = "id", defaultValue = "0") int id,
			HttpServletRequest request) {

		if (!beanValidatorForModel(model, shopPMansongApply)) {
			String referer = request.getHeader("Referer");
			model.addAttribute("referer", referer);
			return Constants.MSG_URL;
		}
		
		if (id != 0) {
			shopPMansongApplyService.updateShopPMansongApply(shopPMansongApply);
			model.addAttribute("msg", "修改满就送套餐申请成功");
		} else {
			shopPMansongApplyService.saveShopPMansongApply(shopPMansongApply);
			model.addAttribute("msg", "保存满就送套餐申请成功");
		}

		model.addAttribute("referer", CommonConstants.ADMIN_SERVER + "/shopPMansongApply/list");
		return Constants.MSG_URL;
	}

	/**
	 * 满就送套餐申请删除
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/delete")
	public String delete(Model model,@RequestParam int[] ids,
			HttpServletRequest request) {

		String referer = request.getHeader("Referer");
		for (int id : ids) {
			shopPMansongApplyService.deleteShopPMansongApplyById(id);
		}

		model.addAttribute("referer", referer);
		return Constants.MSG_URL;
	}
	
	
}