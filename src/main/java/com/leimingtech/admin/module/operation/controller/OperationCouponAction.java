package com.leimingtech.admin.module.operation.controller;

import java.util.HashMap;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.leimingtech.core.common.DateUtils;
import com.leimingtech.core.entity.GoodsClass;
import com.leimingtech.core.entity.base.Coupon;
import com.leimingtech.core.entity.base.CouponClass;
import com.leimingtech.core.entity.base.Store;
import com.leimingtech.service.module.coupon.service.CouponClassService;
import com.leimingtech.service.module.coupon.service.CouponService;
import com.leimingtech.service.module.goods.service.GoodsClassService;
import com.leimingtech.service.module.store.service.StoreService;
import com.leimingtech.service.utils.page.Pager;

/**
 * 运营下的优惠券，供全平台使用
 * 
 * @author yangxp 2015年12月5日 下午2:28:55
 */

@Controller
@RequestMapping("/operation/coupon")
@Slf4j
public class OperationCouponAction {

	@Resource
	private CouponService couponService;

	@Resource
	private CouponClassService couponClassService;

	@Resource
	private StoreService storeService;

	@Resource
	private GoodsClassService goodsClassService;

	/**
	 * 优惠券列表
	 * 
	 * @param model
	 * @param pageNo
	 * @param couponTitle
	 * @param couponStartDate
	 * @param couponEndDate
	 * @return
	 */
	@RequestMapping("list")
	public String list(
			Model model,
			@RequestParam(required = false, value = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(required = false, value = "couponTitle") String couponTitle,
			@RequestParam(required = false, value = "couponStartDate") String couponStartDate,
			@RequestParam(required = false, value = "couponEndDate") String couponEndDate) {
		try {
			Pager pager = new Pager();
			pager.setPageNo(pageNo);
			pager.setPageSize(10);
			Coupon coupon = new Coupon();
			// coupon.setStoreId(0);
			if (StringUtils.isNotEmpty(couponTitle)) {
				coupon.setCouponTitle(couponTitle);
			}

			if (StringUtils.isNotEmpty(couponStartDate)
					&& StringUtils.isNotEmpty(couponEndDate)) {
				// 优惠券开始时间
				coupon.setStartTime(DateUtils.strToLong(couponStartDate
						+ " 00:00:00"));
				// 优惠券截止时间
				coupon.setEndTime(DateUtils.strToLong(couponEndDate
						+ " 23:59:59"));
			}

			pager.setCondition(coupon);

			int count = couponService.findCouponCount(coupon);
			List<Coupon> list = couponService.findCouponPagerList(pager);

			model.addAttribute("datas", list);// 结果集
			model.addAttribute("pager", pager);// 分页对象
			model.addAttribute("couponTitle", couponTitle); // 搜索回显
			model.addAttribute("couponStartDate", couponStartDate);
			model.addAttribute("couponEndDate", couponEndDate);
			model.addAttribute("pageNo", pager.getPageNo());// 当前页
			model.addAttribute("pageSize", pager.getPageSize());// 每页显示条数
			model.addAttribute("recordCount", count);// 总数
			model.addAttribute("toUrl", "/operation/coupon/list");

		} catch (Exception e) {
			log.error("优惠券列表出错", e);
		}

		return "/operation/coupon/list";
	}

	/**
	 * 跳转到新增页面
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("add")
	public String add(Model model) {
		try {
			List<CouponClass> list = couponClassService.findAllCouponList();
			List<GoodsClass> goodsClassList = goodsClassService.findAll();
			model.addAttribute("goodsClassList", goodsClassList);
			model.addAttribute("classlist", list);
		} catch (Exception e) {
			log.error("优惠券新增出错", e);
		}
		return "/operation/coupon/add";
	}

	/**
	 * 保存
	 * 
	 * @param couponClass
	 * @param model
	 * @return
	 */
	@RequestMapping("saveOrUpdate")
	@ResponseBody
	public Map<String, Object> saveOrUpdate(@ModelAttribute Coupon coupon,
			Model model, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {

			String couponStartDate = request.getParameter("couponStartDate");
			String couponEndDate = request.getParameter("couponEndDate");
			if (StringUtils.isNotEmpty(couponStartDate)
					&& StringUtils.isNotEmpty(couponEndDate)) {
				// 优惠券开始时间
				coupon.setStartTime(DateUtils.strToLong(couponStartDate
						+ " 00:00:00"));
				if (couponStartDate.equals(couponEndDate)) {
					// 优惠券截止时间
					coupon.setEndTime(DateUtils.strToLong(couponEndDate
							+ " 23:59:59"));
				} else {
					// 优惠券戒指时间
					coupon.setEndTime(DateUtils.strToLong(couponEndDate
							+ " 00:00:00"));
				}
			}

			if (coupon.getCouponId() == null) {
				coupon.setCouponClassId(0);//全站优惠券，设置优惠券分类id为0
				coupon.setCouponState(0);
				coupon.setCouponusage(0);
				coupon.setCouponIock(0);
				coupon.setCoupClick(1);
				coupon.setCouponRecommend(0);
				coupon.setCouponAllowState(0);
				couponService.saveCoupon(coupon);
				map.put("msg", "保存成功");
			} else {
				couponService.updateCoupon(coupon);
				map.put("msg", "修改成功");
			}

			map.put("success", true);

		} catch (Exception e) {
			log.error("优惠券保存出错", e);
			map.put("success", false);
			map.put("msg", "保存失败");
		}
		return map;
	}

	/**
	 * 跳转到新增页面
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("edit")
	public String edit(Model model,
			@RequestParam(required = false, value = "id") Integer id) {
		try {
			Coupon coupon = couponService.getCouponById(id);
			List<CouponClass> list = couponClassService.findAllCouponList();
			List<GoodsClass> goodsClassList = goodsClassService.findAll();
			model.addAttribute("goodsClassList", goodsClassList);
			model.addAttribute("classlist", list);
			model.addAttribute("coupon", coupon);
		} catch (Exception e) {
			log.error("优惠券新增出错", e);
		}
		return "/operation/coupon/add";
	}

	/**
	 * 优惠券
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("delete")
	@ResponseBody
	public Map<String, Object> delete(Model model,
			@RequestParam(required = false, value = "id") Integer id) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			couponService.deleteCoupon(id);
			map.put("success", true);
			map.put("msg", "删除成功");
		} catch (Exception e) {
			log.error("优惠券新增出错", e);
			map.put("success", false);
			map.put("msg", "删除失败");
		}
		return map;
	}
}
