package com.leimingtech.admin.module.platformstore;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Maps;
import com.leimingtech.core.common.Constants;
import com.leimingtech.core.common.excel.ImportExcelsellerUtils;
import com.leimingtech.core.entity.EvaluateGoodsExcel;
import com.leimingtech.core.entity.base.EvaluateGoods;
import com.leimingtech.core.entity.base.Goods;
import com.leimingtech.core.entity.base.Store;
import com.leimingtech.core.jackson.JsonUtils;
import com.leimingtech.core.platform.info.PlatformInfo;
import com.leimingtech.service.module.goods.service.GoodsService;
import com.leimingtech.service.module.store.service.StoreService;
import com.leimingtech.service.module.trade.service.EvaluateGoodsService;
import com.leimingtech.service.utils.page.Pager;

/**
 * action描述:平台关于订单的跳转action 创建人：cgl 创建时间：2015年08月05日16:32:53 平台自营
 */
@Controller
@RequestMapping("/platform/trade")
public class PlatformTradeAction {

	@Resource
	private EvaluateGoodsService evaluateGoodsService;

	@Resource
	private GoodsService goodsService;
	@Resource
	private StoreService storeService;

	@RequiresPermissions("sys:platformevaluation:view")
	@RequestMapping("/reviewIndex")
	public String list(
			Model model,
			@ModelAttribute EvaluateGoods evaluateGoods,
			@RequestParam(required = false, value = "div", defaultValue = "") String div,
			@RequestParam(required = false, value = "pageNo", defaultValue = "") String pageNoStr) {

		evaluateGoods.setGevalStoreId(PlatformInfo.PLATFORM_STORE_ID);
		Pager pager = new Pager();
		pager.setCondition(evaluateGoods);
		if (!StringUtils.isEmpty(pageNoStr)) {
			pager.setPageNo(Integer.parseInt(pageNoStr));
		}
		pager.setCondition(evaluateGoods);
		List<EvaluateGoods> results = evaluateGoodsService.findPageList(pager);// 结果集
		pager.setResult(results);
		model.addAttribute("pager", pager);
		model.addAttribute("geval", evaluateGoods);
		return "platform/trade/list";
	}

	/**
	 * 删除
	 * 
	 * @param id
	 * @return
	 */
	@RequiresPermissions("sys:evaluation:edit")
	@RequestMapping("/delete")
	public String delete(@RequestParam int id, Model model,
			HttpServletRequest request) {

		String referer = request.getHeader("Referer");
		model.addAttribute("referer", referer);
		if (id == 0) {
			model.addAttribute("msg", "删除失败");
		} else {
			evaluateGoodsService.delete(id);
			model.addAttribute("msg", "删除成功");
		}
		return Constants.MSG_URL;
	}

	/**
	 * 商品评论信息上传
	 * 
	 * @return
	 */
	@RequestMapping(value = "/fileexcelUpload")
	public @ResponseBody Map<String, Object> fileexcelUpload(
			@RequestParam MultipartFile files, HttpServletResponse response)
			throws IOException {
		Map<String, Object> map = Maps.newHashMap();
		String message = "";
		EvaluateGoodsExcel excelgoods = new EvaluateGoodsExcel();
		try {
			@SuppressWarnings("unchecked")
			List<EvaluateGoodsExcel> evaluategoodslist = (List<EvaluateGoodsExcel>) ImportExcelsellerUtils
					.readExcelTitle(files.getInputStream(), excelgoods);
			if (evaluategoodslist.size() != 0) {
				// 保存评价
				saveevalueategoods(evaluategoodslist);
				map.put("success", true);
				message = "生成成功";
			}
		} catch (IOException e) {
			e.toString();
			e.printStackTrace();
			message = "生成失败";
		} catch (Exception e) {
			message = "生成失败";
			e.printStackTrace();
		}
		map.put("message", message);
		String json = JsonUtils.toJsonStr(map);
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		response.getWriter().write(json);
		return null;
	}

	/**
	 * 保存评论信息
	 * 
	 * @param list
	 */
	public void saveevalueategoods(List<EvaluateGoodsExcel> list) {
		// 遍历解析到的评价list信息
		for (EvaluateGoodsExcel evaluateGoodsExcel : list) {
			EvaluateGoods evaluateGoods = new EvaluateGoods();
			if (evaluateGoodsExcel != null) {
				if (evaluateGoodsExcel.getGevalGoodsId() != null) {
					// 根据商品id获得商品信息
					Goods goods = goodsService.findGoodById(evaluateGoodsExcel
							.getGevalGoodsId());
					// 根据店铺id获取店铺信息
					if (goods != null && goods.getStoreId() != null) {
						evaluateGoods.setGevalStoreId(PlatformInfo.PLATFORM_STORE_ID);// 保存店铺id
						evaluateGoods.setGevalStorename(PlatformInfo.PLATFORM_STORE_NAME);// 保存店铺名称
					}
				}
				// 保存商品id
				evaluateGoods.setGevalGoodsId(evaluateGoodsExcel
						.getGevalGoodsId());
				// 评价分数
				evaluateGoods.setGevalScore(evaluateGoodsExcel.getGevalScore());
				// 商品名称
				evaluateGoods.setGevalGoodsName(evaluateGoodsExcel
						.getGevalGoodsName());
				// 评价内容
				evaluateGoods.setGevalContent(evaluateGoodsExcel
						.getGevalContent());
				// 评价者名称
				evaluateGoods.setGevalFrommembername(evaluateGoodsExcel
						.getGevalFrommembername());
				// 评价者id
				evaluateGoods.setGevalFrommemberid(18);
				// 0表示不是 1表示是匿名评价
				evaluateGoods.setGevalIsAnonymous(evaluateGoodsExcel
						.getGevalIsAnonymous());
				// 评价信息的状态 0为正常 1为禁止显示
				evaluateGoods.setGevalState(evaluateGoodsExcel.getGevalState());
				// 订单表自增ID
				evaluateGoods.setGevalOrderId(49);
				// 订单编号
				evaluateGoods.setGevalOrderNo(System.currentTimeMillis());
				// 订单商品表编号
				evaluateGoods.setGevalOrderGoodsId(99);
				// 商品价格
				// evaluateGoods.setGevalGoodsPrice(evaluateGoodsExcel.getGevalGoodsPrice());
				// 规格描述
				evaluateGoods.setSpecInfo(evaluateGoodsExcel.getSpecInfo());
				// 评价时间
				evaluateGoods.setCreateTime(evaluateGoodsExcel.getCreateTime());
				// 保存评价信息
				evaluateGoodsService.saveEvaluate(evaluateGoods);
				// 释放资源
				evaluateGoods = null;
			}
		}
	}

}
