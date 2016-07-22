package com.leimingtech.admin.module.mansongquota.controller;

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
import com.leimingtech.core.entity.base.ShopPMansongQuota;
import com.leimingtech.service.module.mansongquota.service.ShopPMansongQuotaService;

/**
 * 满就送套餐表Controller
 *
 * @author admin
 * @version 2015-11-19
 */
@Slf4j
@Controller
@RequestMapping("/shopPMansongQuota")
public class MansongQuotaAction extends BaseController {

	/** 满就送套餐表Service接口*/
	@Resource
	private ShopPMansongQuotaService shopPMansongQuotaService;
	
	/**
	 * 满就送套餐表列表
	 * 
	 * @param pageNo
	 * @return
	 */
	@RequestMapping("/list")
	public String list(Model model,
			@ModelAttribute ShopPMansongQuota shopPMansongQuota,
			@RequestParam(required = false, value = "pageNo", defaultValue = "1") int pageNo,
			@RequestParam(required = false, value = "storeName", defaultValue = "") String storeName) {

		Pager pager = new Pager();
		pager.setPageNo(pageNo);
		if(StringUtils.isNotBlank(storeName)){
			shopPMansongQuota.setStoreName(storeName);
		}
		pager.setCondition(shopPMansongQuota);
		
		pager.setResult(shopPMansongQuotaService.findShopPMansongQuotaPagerList(pager));
		model.addAttribute("storeName",storeName);
		model.addAttribute("pager", pager);
		return "/mansongquota/shopPMansongQuotaList";
	}

	/**
	 * 跳转至满就送套餐表新增或修改页面
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/forward")
	public String add(Model model ,@RequestParam(required = false, value = "id", defaultValue = "0") int id) {

		ShopPMansongQuota shopPMansongQuota = shopPMansongQuotaService.findShopPMansongQuotaById(id);

		if (shopPMansongQuota == null) {
			shopPMansongQuota = new ShopPMansongQuota();
		}

		model.addAttribute("shopPMansongQuota", shopPMansongQuota);
		return "/mansongquota/shopPMansongQuota";
	}

	/**
	 * 满就送套餐表修改或保存
	 * 
	 * @param goodsClass
	 * @param id
	 * @return
	 */
	@RequestMapping("/saveOrUpdate")
	public String saveOrUpdate(
			Model model, 
			@ModelAttribute ShopPMansongQuota shopPMansongQuota,
			@RequestParam(required = false, value = "id", defaultValue = "0") int id,
			HttpServletRequest request) {

		if (!beanValidatorForModel(model, shopPMansongQuota)) {
			String referer = request.getHeader("Referer");
			model.addAttribute("referer", referer);
			return Constants.MSG_URL;
		}
		
		if (id != 0) {
			shopPMansongQuotaService.updateShopPMansongQuota(shopPMansongQuota);
			model.addAttribute("msg", "修改满就送套餐成功");
		} else {
			shopPMansongQuotaService.saveShopPMansongQuota(shopPMansongQuota);
			model.addAttribute("msg", "保存满就送套餐成功");
		}

		model.addAttribute("referer", CommonConstants.ADMIN_SERVER + "/shopPMansongQuota/list");
		return Constants.MSG_URL;
	}

	/**
	 * 满就送套餐表删除
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/delete")
	public String delete(Model model,@RequestParam int[] ids,
			HttpServletRequest request) {

		String referer = request.getHeader("Referer");
		for (int id : ids) {
			shopPMansongQuotaService.deleteShopPMansongQuotaById(id);
		}

		model.addAttribute("referer", referer);
		return Constants.MSG_URL;
	}
	
	
}