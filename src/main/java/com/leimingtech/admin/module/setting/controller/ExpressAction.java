package com.leimingtech.admin.module.setting.controller;

import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.leimingtech.core.entity.base.Express;
import com.leimingtech.service.module.setting.service.ExpressService;
import com.leimingtech.service.utils.page.Pager;

/**
 * 快递公司设置
 * @author 
 */
@Controller
@RequestMapping("/setting/express")
@Slf4j
public class ExpressAction {

	String message = "success";

	@Autowired
	private ExpressService expressService;

	/** 导航至主操作页面 */
	@RequestMapping("/index")
	public String index() {
		try {
			log.debug("操作成功！");
			return "/setting/express/index";
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("导航失败!");
		}
	}

	/**
	 * 
	 * @Title: list
	 * @Description: TODO (查询方法)
	 * @param @param model
	 * @param @param div
	 * @param @param pageNoStr
	 * @param @param acctName
	 * @param @param certifyClass
	 * @param @return    设定文件
	 * @return String    返回类型
	 * @throws
	 */
	@RequiresPermissions("sys:express:view")
	@RequestMapping(value = "/list")
	public String list(
			Model model,
            @RequestParam(required=false, value="pageNo",defaultValue="")String pageNo,
			@RequestParam(required = false, value = "letter", defaultValue = "") String letter) {
		Pager pager = new Pager();
		Express express = new Express();
		if(StringUtils.isNotBlank(letter)){
			express.setELetter(letter);
		}
        if(!StringUtils.isEmpty(pageNo)){
            pager.setPageNo(Integer.parseInt(pageNo));
        }
		pager.setCondition(express);// 实体加载在pager中

		List<Express> results = expressService.findExpressList(pager);// 结果集
        pager.setResult(results);
		model.addAttribute("pager",pager);
		// 转发请求到FTL页面
		return "/setting/express/list";

	}



	
	/**
	 * 
	 * @Title: delExpress
	 * @Description: TODO (删除会员)
	 * @param @param ids
	 * @param @param model
	 * @param @return  设定文件
	 * @return Map<String,String>  返回类型
	 * @throws
	 */
	@RequiresPermissions("sys:express:edit")
	@RequestMapping(value = "/delExpress", method = RequestMethod.POST)
	public @ResponseBody
	Map<String, String> delExpress(@RequestParam(value = "ids") String ids,
			Model model) {

		Map<String, String> map = Maps.newHashMap();

		if (StringUtils.isBlank(ids)) {
			model.addAttribute("result", "ID为空");
			map.put("result", "ID为空");
			map.put(message, "true");
			return map;
		}
		String[] idArray = StringUtils.split(ids, ",");
		for (String idStr : idArray) {
			expressService.delete(Long.parseLong(idStr));
		}
		map.put("result", "删除成功");
		map.put(message, "true");
		return map;
	}
	
	/**
	 * 
	 * @Title: updateOrder
	 * @Description: TODO (更新快递公司是否常用)
	 * @param @param id
	 * @param @param order
	 * @param @return  设定文件
	 * @return Map<String,String>  返回类型
	 * @throws
	 */
	@RequiresPermissions("sys:express:edit")
	@RequestMapping(value = "/updateOrder")
	public @ResponseBody boolean updateOrder(@RequestParam(value = "id") String id,@RequestParam Integer value
			) {

		Map<String,String> map=Maps.newHashMap();
		Express express = new Express();
		if (StringUtils.isNotBlank(id)) {
			express.setEorder(value);
			express.setId(Integer.valueOf(id));
			expressService.updateOrder(express);
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 
	 * @Title: updateState
	 * @Description: TODO (更新快递公司是否常用)
	 * @param @param id
	 * @param @param state
	 * @param @return  设定文件
	 * @return Map<String,String>  返回类型
	 * @throws
	 */
	@RequiresPermissions("sys:express:edit")
	@RequestMapping(value = "/updateState")
	public @ResponseBody boolean updateState(@RequestParam(value = "id") String id,@RequestParam Integer value
			) {

		Map<String,String> map=Maps.newHashMap();
		Express express = new Express();
		if (StringUtils.isNotBlank(id)) {
			express.setEstate(value);
			express.setId(Integer.valueOf(id));
			expressService.updateState(express);
			return true;
		}else{
			return false;
		}
	}


}