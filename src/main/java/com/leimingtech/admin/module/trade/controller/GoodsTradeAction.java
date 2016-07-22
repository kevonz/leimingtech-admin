package com.leimingtech.admin.module.trade.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.leimingtech.core.entity.base.Store;
import com.leimingtech.core.entity.base.StoreGrade;
import com.leimingtech.core.entity.vo.GoodsTradeVo;
import com.leimingtech.service.module.goods.service.GoodsService;
import com.leimingtech.service.utils.page.Pager;

/**
 * 交易管理首页
 *      
 * 项目名称：leimingtech-seller   
 * 类名称：GoodsTradeAction   
 * 类描述：   
 * 修改备注：   
 * @version    
 *
 */
@Controller
@RequestMapping("/tradegoods")
@Slf4j
public class GoodsTradeAction {
	
	String message = "success";
	@Resource
	private GoodsService goodsService;
	
	/**
	 * 商品
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
			@RequestParam(required = false, value = "pageNo", defaultValue = "1") String pageNo,
			@ModelAttribute GoodsTradeVo goodsTradeVo){
		Pager pager = new Pager();
		if(StringUtils.isNotBlank(pageNo)){
			pager.setPageNo(Integer.parseInt(pageNo));
		}
		goodsTradeVo.setTradeGoodsCount("tradeGoodsCount");//按照交易商品数量排序
		goodsTradeVo.setOrderBy("desc");
		pager.setCondition(goodsTradeVo);//实体加载在pager中
		int count = goodsService.findTradeGoodcount(goodsTradeVo);
		List<GoodsTradeVo> results = goodsService.findTradeGoodPagerList(pager);//结果集
        pager.setResult(results);
        pager.setTotalRows(count);
        model.addAttribute("pager", pager);//总数
        model.addAttribute("goodsTradeVo",goodsTradeVo);
		return "/trade/goodstradelist";
	}
}