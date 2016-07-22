package com.leimingtech.admin.module.operation.controller;

import java.util.List;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.collect.Lists;
import com.leimingtech.core.entity.base.OrderStatis;
import com.leimingtech.service.module.operation.service.OrderStatisService;
import com.leimingtech.service.utils.page.Pager;

/**
 * 
 * 结算管理   
 * 项目名称：leimingtech-admin   
 * 类名称：OrderStatisAction   
 * 类描述：   
 * 创建人：liuhao   
 * 创建时间：2014年11月14日 上午12:03:49   
 * 修改人：liuhao   
 * 修改时间：2014年11月14日 上午12:03:49   
 * 修改备注：   
 * @version    
 *
 */
@Controller
@RequestMapping("/orderstatis")
@Slf4j
public class OrderStatisAction {
	
	@Resource
	private OrderStatisService orderStatisService;

	/**
	 * 
	 * @Title: list 
	 * @Description: TODO(加载数据页面) 
	 * @param @param model
	 * @param @param div
	 * @param @param pageNoStr
	 * @param @param adminName
	 * @param @param starttime
	 * @param @param endtime
	 * @param @return    设定文件 
	 * @return String    返回类型 
	 * @throws
	 */
	@RequestMapping(value = "/list")
	public String list(Model model,
			@RequestParam(required=false, value="pageNo",defaultValue="")String pageNoStr,
			@RequestParam(required=false, value="osYear",defaultValue="")String osYear){

        List<Integer> years = Lists.newArrayList();
        for(int i = -5; i < 3 ; i++){
            years.add(new DateTime().plusYears(i).getYear());
        }
        Pager pager = new Pager();
		OrderStatis orderStatis = new OrderStatis();
		/**查询条件，放入实体中，**/
		if (StringUtils.isNotBlank(osYear))
			orderStatis.setOsYear(Short.valueOf(osYear));
		
		if(StringUtils.isNotBlank(pageNoStr)){
			pager.setPageNo(Integer.parseInt(pageNoStr));
		}
		
		pager.setCondition(orderStatis);//实体加载在pager中
		int total  = orderStatisService.countOrderStatis(pager);//获取总条数
		log.info("获取总条数【total】"+total);
		List<OrderStatis> results = orderStatisService.queryOrderStatisList(pager);//结果集
		
		pager.setTotalRows(total);
        pager.setResult(results);
        model.addAttribute("pager",pager);
        model.addAttribute("years",years);
        model.addAttribute("osYear",osYear);
		//转发请求到FTL页面
		return "/operation/list";
		
	}
	
	
	
	
}