package com.leimingtech.admin.module.platformstore;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.leimingtech.core.base.BaseController;
import com.leimingtech.core.common.CommonConstants;
import com.leimingtech.core.common.Constants;
import com.leimingtech.core.common.DateUtils;
import com.leimingtech.core.common.ParamsUtils;
import com.leimingtech.core.entity.base.ShopPMansong;
import com.leimingtech.core.entity.base.ShopPMansongRule;
import com.leimingtech.core.state.mansong.ManSongState;
import com.leimingtech.service.module.mansong.service.ShopPMansongService;
import com.leimingtech.service.module.mansongquota.service.ShopPMansongQuotaService;
import com.leimingtech.service.module.mansongrule.service.ShopPMansongRuleService;
import com.leimingtech.service.utils.page.Pager;

/**
 * 满就送controller，平台直营
 * @author linjm 2015年12月7日 下午3:27:46
 */
@Slf4j
@Controller
@RequestMapping("/platformmansong")
public class PlarformMansongAction extends BaseController {

	/** 满就送Service接口 */
	@Resource
	private ShopPMansongService shopPMansongService;
	// 满就送规则Service接口
	@Resource
	private ShopPMansongRuleService shopPMansongRuleService;
	// 满就送套餐service接口
	@Resource
	private ShopPMansongQuotaService shopPMansongQuotaService;

	/**
	 * 满就送列表
	 * @param pageNo
	 * @return
	 */
	@RequestMapping("/list")
	public String list(
			Model model,
			@ModelAttribute ShopPMansong shopPMansong,
			@RequestParam(required = false, value = "pageNo", defaultValue = "1") Integer pageNo
			) {

		Pager pager = new Pager();
		pager.setPageNo(pageNo);
		//直营店铺id 默认是0
		shopPMansong.setStoreId(Constants.PLATFORM_STORE_ID);
		pager.setCondition(shopPMansong);
		List<ShopPMansong> list = shopPMansongService
				.findShopPMansongPagerList(pager);
		pager.setResult(list);

		model.addAttribute("pager", pager);
		model.addAttribute("mansongName", shopPMansong.getMansongName());
		model.addAttribute("pageNo", pager.getPageNo());// 当前页
		model.addAttribute("pageSize", pager.getPageSize());// 每页显示条数
		model.addAttribute("recordCount", pager.getTotalRows());// 总数
		model.addAttribute("state", shopPMansong.getState());
		model.addAttribute("toUrl", "/platformmansong/list");
		return "/platform/mansong/mansongList";
	}

	/**
	 * 通过id获取活动的详情
	 */
	@RequestMapping("/findById")
	public String findMansongDetailById(Model model,
			@RequestParam(required = true, value = "id") int mansongId) {
		ShopPMansong shopPMansong = shopPMansongService
				.findShopPMansongById(mansongId);
		List<ShopPMansongRule> shopPMansongRules = shopPMansongRuleService
				.findShopPMansongRuleByMansongid(mansongId);
		model.addAttribute("shopPMansong", shopPMansong);
		model.addAttribute("shopPMansongRuleList", shopPMansongRules);
		return "/platform/mansong/mansongDetail";
	}

	/**
	 * 跳转至满就送新增或修改页面
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/forward")
	public String add(Model model) {
		return "/platform/mansong/mansongEdit";
	}

	/**
	 * 满就送保存
	 * 
	 * @param goodsClass
	 * @param id
	 * @return
	 */
	@RequestMapping("/save")
	public String save(Model model, HttpServletRequest request,
			@RequestParam(value = "mansongName", defaultValue = "") String mansongName,
			@RequestParam(value = "remark", defaultValue = "") String remark,
			@RequestParam(value = "startTime", defaultValue = "") String startTime,
			@RequestParam(value = "endTime", defaultValue = "") String endTime,
			@RequestParam(required=false, value = "mansong_rule[]") String[] mansong_rule) {
		String referer = CommonConstants.ADMIN_SERVER + "/platformmansong/list";//request.getHeader("Referer");
		ShopPMansong shopPMansong = new ShopPMansong();
		try{
			shopPMansong.setMansongName(mansongName);
			shopPMansong.setQuotaId(Constants.PLATFORM_STORE_ID);
			
			shopPMansong.setStoreId(Constants.PLATFORM_STORE_ID);
			shopPMansong.setStoreName("平台直营");
			shopPMansong.setMemberId(Constants.PLATFORM_STORE_ID);
			shopPMansong.setMemberName("pintaiziying");
			shopPMansong.setRemark(remark);
			shopPMansong.setStartTime(DateUtils.strToLong(startTime + ":00"));
			shopPMansong.setEndTime(DateUtils.strToLong(endTime + ":00"));
			shopPMansong.setState(ManSongState.MS_AGREE);//状态(1-新申请/2-审核通过/3-已取消/4-审核失败)
			shopPMansongService.saveShopPMansong(shopPMansong);
			Integer mansongId = shopPMansong.getMansongId();
			
			//遍历添加的规则
			int i = 1;
			for (String rules : mansong_rule) {
				ShopPMansongRule shopPMansongRule = new ShopPMansongRule();
				String[] rule = rules.split(";");
				shopPMansongRule.setPrice(BigDecimal.valueOf(ParamsUtils.getDouble(rule[0])));
				shopPMansongRule.setDiscount(BigDecimal.valueOf(ParamsUtils.getDouble(rule[1])));
				shopPMansongRule.setLevel(i);  //按添加的顺序设置的
				shopPMansongRule.setMansongId(mansongId);
				i++;
				shopPMansongRuleService.saveShopPMansongRule(shopPMansongRule);
			}
			model.addAttribute("referer", referer);
	        model.addAttribute("msg", "满就送保存成功");
			return Constants.MSG_URL;
		} catch (Exception e) {
			log.error("满就送保存出错", e);
			model.addAttribute("referer", referer);
			model.addAttribute("msg", "满就送保存出错");
			return Constants.MSG_URL;
		}
	}

	/**
	 * 满就送删除
	 * @param id
	 * @return
	 */
	@RequestMapping("delete")
	@ResponseBody
	public String delete(Model model,@RequestParam int id){
		String referer = CommonConstants.ADMIN_SERVER + "/platformmansong/list";//request.getHeader("Referer");
		try {
			shopPMansongService.deleteShopPMansongById(id);
			// 同时还要删除满就送的规则
			shopPMansongRuleService.deleteShopPMansongRuleByMansongid(id);
			model.addAttribute("msg", "删除成功");
		} catch (Exception e) {
			log.error("满就送删除出错", e);
			model.addAttribute("msg", "满就送保存出错");
		}
		model.addAttribute("referer", referer);
		return Constants.MSG_URL;
	}
}