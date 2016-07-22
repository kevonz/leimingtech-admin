package com.leimingtech.admin.module.coupon.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.leimingtech.core.common.CommonConstants;
import com.leimingtech.core.common.Constants;
import com.leimingtech.core.entity.base.Coupon;
import com.leimingtech.service.module.coupon.service.CouponService;
import com.leimingtech.service.utils.page.Pager;


/**
 * 优惠券
 * @author kviuff
 * @date 2015-07-23 10:00:00
 */
@Controller
@RequestMapping("/coupon")
@Slf4j
public class CouponAction {
	
	
	@Resource
	private CouponService couponService;
	
	/**
	 * 优惠券列表
	 * @return
	 */
	@RequiresPermissions("sys:coupon:view")
	@RequestMapping("list")
	public String list(
			Model model,
			@RequestParam(required = false, value = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(required = false, value = "couponAllowState") String couponAllowState
			
			){
		try {
			int status = 0;
			Pager pager = new Pager();
			pager.setPageNo(pageNo);
			
			Coupon coupn = new Coupon();
			if(StringUtils.isNotEmpty(couponAllowState)){
				coupn.setCouponAllowState(Integer.parseInt(couponAllowState));
				status = 1;
			}
			pager.setCondition(coupn);
			
			List<Coupon> list = couponService.findCouponPagerList(pager);
			pager.setResult(list);
			model.addAttribute("datas", list);// 结果集
			model.addAttribute("pager", pager);
			model.addAttribute("status", status);
			
		} catch (Exception e) {
			log.error("优惠券列表出错", e);
		}
		
		return "/coupon/coupon/list";
	}
	
	
	/**
	 * 优惠券列表
	 * @return
	 */
	@RequiresPermissions("sys:coupon:edit")
	@RequestMapping("delete")
	public String delete(
			Model model,
			@RequestParam(required = false, value = "ids") String ids,
			HttpServletRequest request
			){
		try {
			String referer = request.getHeader("Referer");
			model.addAttribute("referer", referer);
			if (StringUtils.isBlank(ids)) {
				model.addAttribute("result", "ID为空");
				model.addAttribute("msg", "删除失败，ID为空");
			}else{
				String[] idArray = StringUtils.split(ids, ",");
				for (String idStr : idArray) {
					couponService.deleteCoupon(Integer.parseInt(idStr));
				}
				model.addAttribute("msg", "删除成功");
			}
		} catch (Exception e) {
			log.error("优惠券列表出错", e);
		}
		
		return Constants.MSG_URL;
	}
	
	/**
	 * 跳转到编辑页面
	 * @param model
	 * @param id
	 * @param request
	 * @return
	 */
	@RequiresPermissions("sys:coupon:edit")
	@RequestMapping("edit")
	public String edit(
			Model model,
			@RequestParam(required = false, value = "id") Integer id,
			HttpServletRequest request
			){
		try {
			Coupon coupon = couponService.getCouponById(id);
			model.addAttribute("coupon", coupon);
		} catch (Exception e) {
			log.error("优惠券编辑出错", e);
		}
		return "/coupon/coupon/edit";
	}
	
	/**
	 * 保存
	 * @param couponClass
	 * @param model
	 * @return
	 */
	@RequiresPermissions("sys:coupon:edit")
	@RequestMapping("update")
	public String update(
			@ModelAttribute Coupon coupon,
			Model model, 
			HttpServletRequest request
			){
		try {
			model.addAttribute("referer", CommonConstants.ADMIN_SERVER + "/coupon/list");
			coupon.setCreateTime(System.currentTimeMillis());
			couponService.updateCoupon(coupon);
			model.addAttribute("msg", "编辑成功");
		} catch (Exception e) {
			log.error("优惠券保存出错", e);
			model.addAttribute("msg", "编辑失败");
		}
		return Constants.MSG_URL;
	}
	
	/**
	 * 通过couponClassId获取优惠券
	 * @return
	 */
	@RequiresPermissions("sys:coupon:view")
	@ResponseBody
	@RequestMapping(value = "findCouponByClassId", method = RequestMethod.POST)
	public Map<String,Object> findCouponByClassId(
			@RequestParam(value = "couponClassId") Integer couponClassId
			){
		Map<String,Object> map = Maps.newHashMap();
		try {
			
			List<Coupon> list = couponService.findCouponByClassId(couponClassId);
			int size = 0;
			if(list!=null){
				size = list.size();
			}
			map.put("size",size);
			map.put("couponClassId", couponClassId);
			return map;
		} catch (Exception e) {
			log.error("优惠券列表出错", e);
		}
		return map;
	}
	
}

