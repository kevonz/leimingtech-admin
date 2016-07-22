package com.leimingtech.admin.module.platformstore;

import java.util.List;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.leimingtech.core.entity.Area;
import com.leimingtech.core.entity.Transport;
import com.leimingtech.core.platform.info.PlatformInfo;
import com.leimingtech.service.module.area.service.AreaService;
import com.leimingtech.service.module.trade.service.TransportService;
import com.leimingtech.service.utils.page.Pager;

/**
 * action描述:平台关于运费模板跳转action
 * @创建人：cgl
 * @创建时间：2015年08月04日12:10:28
 */
@Controller
@RequestMapping("/platform/transport")
@Slf4j
public class PlatformTransportAction {
	
	@Resource
	private TransportService transportService;
	
	@Resource
	private AreaService areaService;
	

	/**
	 * 
	 * @Title: index
	 * @Description: 跳转到主页面
	 * @param @param model
	 * @param @return 设定文件
	 * @return String 返回类型
	 * @throws
	 */
	@RequiresPermissions("sys:platformtransport:view")
	@RequestMapping("/index")
	public String index(
			Model model,
			@RequestParam(required = false, value = "pageNo", defaultValue = "") String pageNoStr) {
		try {
			Integer storeId = PlatformInfo.PLATFORM_STORE_ID;

			Transport transport = new Transport();
			transport.setStoreId(storeId);

			Pager pager = new Pager();
			pager.setCondition(transport);

			List<Transport> transportList = transportService.queryList(pager);

			model.addAttribute("transportList", transportList);// 品牌列表
			model.addAttribute("pageNo", pager.getPageNo());// 当前页
			model.addAttribute("pageSize", pager.getPageSize());// 每页显示条数

			return "/platform/transport/transport_index";
		} catch (Exception e) {
			log.error("导航失败!", e);
			throw new RuntimeException("导航失败!");
		}
	}
	
	
	
	/**
	 * 
	 * @Title: add
	 * @Description: 跳转到新增运费模板页面
	 * @param @param model
	 * @param @return 设定文件
	 * @return String 返回类型
	 * @throws
	 */
	@RequiresPermissions("sys:platformtransport:view")
	@RequestMapping("/toAdd")
	public String toAdd(Model model) {
		try {
			List<Area> areas = areaService.getProvinceCityArea();
			model.addAttribute("areas", areas);
			return "/platform/transport/transport_add";
		} catch (Exception e) {
			log.error("导航失败!", e);
			throw new RuntimeException("导航失败!");
		}
	}
	
	/**
	 * 
	 * @Title: add
	 * @Description: 跳转到新增运费模板页面
	 * @param @param model
	 * @param @return 设定文件
	 * @return String 返回类型
	 * @throws
	 */
	@RequiresPermissions("sys:platformtransport:view")
	@RequestMapping("/toEdit")
	public String toEdit(Model model, Integer id) {
		try {
			Transport transport = transportService.findById(id);
			model.addAttribute("transport", transport);
			List<Area> areas = areaService.getProvinceCityArea();
			model.addAttribute("areas", areas);
			return "/platform/transport/transport_edit";
		} catch (Exception e) {	
			log.error("导航失败!", e);
			throw new RuntimeException("导航失败!");
		}
	}
	
	/**
	 * 
	 * @Title: add
	 * 添加
	 */
	@RequiresPermissions("sys:platformtransport:edit")
	@ResponseBody
	@RequestMapping("/add")
	public String add(Model model, String title, String tranStr) {
		try {
			Integer storeId = PlatformInfo.PLATFORM_STORE_ID;
			if(StringUtils.isNotEmpty(title) && StringUtils.isNotEmpty(tranStr)){
				transportService.save(storeId, title, tranStr);
			}
			return "success";
		} catch (Exception e) {
			log.error("导航失败!", e);
			return "error";
		}
	}
	
	/**
	 * 
	 * @Title: add
	 * 修改
	 */
	@RequiresPermissions("sys:platformtransport:edit")
	@ResponseBody
	@RequestMapping("/edit")
	public String edit(Model model,Integer transportId, String title, String tranStr) {
		try {
			Integer storeId = PlatformInfo.PLATFORM_STORE_ID;
			if(StringUtils.isNotEmpty(title) && StringUtils.isNotEmpty(tranStr)){
				transportService.update(transportId, storeId, title, tranStr);
			}
			return "success";
		} catch (Exception e) {
			log.error("导航失败!", e);
			return "error";
		}
	}
	
	/**
	 * 
	 * @Title: delete
	 * 删除
	 */
	@RequiresPermissions("sys:platformtransport:edit")
	@ResponseBody
	@RequestMapping("/delete")
	public String delete(Integer transportId) {
		try {
			//Integer storeId = CacheUtils.getCacheUser().getStore().getStoreId();
			transportService.delete(transportId);
			return "success";
		} catch (Exception e) {
			log.error("导航失败!", e);
			return "error";
		}
	}
	
	/**
	 * 
	 * @Title: setDefaultTransport
	 * 修改默认的运费模板
	 */
	@RequiresPermissions("sys:platformtransport:edit")
	@ResponseBody
	@RequestMapping("/setDefaultTransport")
	public String updateDefaultTransport(Integer transportId) {
		try {
			Integer storeId = PlatformInfo.PLATFORM_STORE_ID;
			transportService.updateDefaultTransport(storeId, transportId);
			return "success";
		} catch (Exception e) {
			log.error("导航失败!", e);
			return "error";
		}
	}
}
