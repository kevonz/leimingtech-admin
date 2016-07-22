package com.leimingtech.admin.module.coupon.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.leimingtech.core.common.CommonConstants;
import com.leimingtech.core.common.Constants;
import com.leimingtech.core.entity.base.CouponClass;
import com.leimingtech.service.module.coupon.service.CouponClassService;
import com.leimingtech.service.utils.page.Pager;

/**
 * 优惠券分类
 * @author kviuff
 * @date 2015-07-23 10:00:00
 */
@Controller
@RequestMapping("/couponclass")
@Slf4j
public class CouponClassAction {
	
	@Resource
	private CouponClassService couponClassService;
	
	/**
	 * 优惠券分类列表
	 * @return
	 */
	@RequiresPermissions("sys:couponclass:view")
	@RequestMapping("list")
	public String list(
			Model model,
			@RequestParam(required = false, value = "pageNo", defaultValue = "1") Integer pageNo
			){
		try {
			Pager pager = new Pager();
			pager.setPageNo(pageNo);
			pager.setPageSize(10);
			
			CouponClass couponClass = new CouponClass();
			
			int count = couponClassService.findCouponCount(couponClass);
			List<CouponClass> list = couponClassService.findCouponPageList(pager);
			
			model.addAttribute("datas", list);// 结果集
			model.addAttribute("pager", pager);// 分页对象
			model.addAttribute("pageNo", pager.getPageNo());// 当前页
			model.addAttribute("pageSize", pager.getPageSize());// 每页显示条数
			model.addAttribute("recordCount", count);// 总数
			
		} catch (Exception e) {
			log.error("优惠券分类列表出错", e);
		}
		
		return "/coupon/class/list";
	}
	
	/**
	 * 跳转到添加页面
	 * @return
	 */
	@RequiresPermissions("sys:couponclass:edit")
	@RequestMapping("add")
	public String add(){
		return "/coupon/class/add";
	}
	
	/**
	 * 跳转到编辑页面
	 * @param model
	 * @param id
	 * @return
	 */
	@RequiresPermissions("sys:couponclass:edit")
	@RequestMapping("edit")
	public String edit(
			Model model,
			@RequestParam(required = false, value = "id", defaultValue = "") int id
			){
		try {
			CouponClass couponclass = couponClassService.getCouponById(id);
			model.addAttribute("couponclass", couponclass);
		} catch (Exception e) {
			log.error("优惠券分类修改出错", e);
		}
		return "/coupon/class/edit";
	}
	
	/**
	 * 保存、新增优惠分类
	 * @param couponClass
	 * @param model
	 * @return
	 */
	@RequiresPermissions("sys:couponclass:edit")
	@RequestMapping("saveOrUpdate")
	public String saveOrUpdate(
			@ModelAttribute CouponClass couponClass,
			Model model
			){
		try {
			model.addAttribute("referer", CommonConstants.ADMIN_SERVER + "/couponclass/list");
			if(couponClass.getClassId() == null){
				couponClassService.saveCoupon(couponClass);
				model.addAttribute("msg", "新增成功");
			}else{
				couponClassService.updateCoupon(couponClass);
				model.addAttribute("msg", "编辑成功");
			}
		} catch (Exception e) {
			log.error("优惠券分类保存出错", e);
		}
		return Constants.MSG_URL;
	}
	
	/**
	 * 删除
	 * @param ids
	 * @param model
	 * @return
	 */
	@RequiresPermissions("sys:couponclass:edit")
	@RequestMapping("deleteCoupon")
	public String deleteCoupon(
			@RequestParam(value = "ids") String ids,
			Model model,
			HttpServletRequest request
			){
		
		String referer = request.getHeader("Referer");
		model.addAttribute("referer", referer);
		if (StringUtils.isBlank(ids)) {
			model.addAttribute("result", "ID为空");
			model.addAttribute("msg", "删除失败，ID为空");
		}else{
			String[] idArray = StringUtils.split(ids, ",");
			for (String idStr : idArray) {
				couponClassService.deleteCoupon(Integer.parseInt(idStr));
			}
			model.addAttribute("msg", "删除成功");
		}
		
		return Constants.MSG_URL;
	}
	
	
}
