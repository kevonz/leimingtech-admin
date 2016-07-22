package com.leimingtech.admin.module.goods.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.leimingtech.core.common.Collections3;
import com.leimingtech.core.entity.GoodsClass;
import com.leimingtech.core.entity.base.Goods;
import com.leimingtech.service.module.goods.service.GoodsClassService;
import com.leimingtech.service.module.goods.service.GoodsService;
import com.leimingtech.service.module.tostatic.service.ToStaticService;
import com.leimingtech.service.utils.page.Pager;

/**
 * 
 * 
 * @项目名称：leimingtech-admin
 * @类名称：GoodsAction
 * @类描述： 后台商品-->商品管理
 * @创建人：shining
 * @创建时间：2014年11月10日 上午12:33:44
 * @修改人：shining
 * @修改时间：2014年11月10日 上午12:33:44
 * @修改备注：
 * @version
 * 
 */
@Controller
@RequestMapping("/goods/goods")
@Slf4j
public class GoodsAction {

	String message = "success";

	@Resource
	private GoodsService goodsService;
	@Resource
    private GoodsClassService goodsClassService;
	@Autowired
	private ToStaticService toStaticService;
	/**
	 * 导航至主操作页面
	 * 
	 * @Title: index
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @return 设定文件
	 * @return String 返回类型
	 * @throws
	 */
	@RequestMapping("/index")
	public String index(Model model) {
		try {
			return "/goods/goods/index";
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("导航失败!");
		}
	}

	/**
	 * 
	 * @Title: list
	 * @Description: (加载数据页面)
	 * @param @param model
	 * @param @param div
	 * @param @param pageNoStr
	 * @param @param adminName
	 * @param @param starttime
	 * @param @param endtime
	 * @param @return 设定文件
	 * @return String 返回类型
	 * @throws
	 */
	@RequiresPermissions("sys:goods:view")
	@RequestMapping("/list")
	public String list(
			Model model,
			@RequestParam(required = false, value = "div", defaultValue = "") String div,
			@RequestParam(required = false, value = "pageNo", defaultValue = "") String pageNoStr,
			@RequestParam(required = false, value = "goodsShow", defaultValue = "") String goodsShow,
			@RequestParam(required = false, value = "goodsState", defaultValue = "") String goodsState,
            @RequestParam Integer gcId,@RequestParam String goodsName
			) {
		try {
			Pager pager = new Pager();
            pager.setPageSize(6);
			Goods goods = new Goods();
			if(gcId!=0){
				goods.setGcId(gcId);
			}
            goods.setGoodsName(goodsName);
            if (StringUtils.isNotBlank(pageNoStr)) {
                pager.setPageNo(Integer.parseInt(pageNoStr));
            }
            if (StringUtils.isNumeric(goodsShow)) {
            	goods.setGoodsShow(Integer.valueOf(goodsShow));
            	model.addAttribute("goodsShow", goodsShow);// 弹出框分页附加条件
            }
            if (StringUtils.isNumeric(goodsState)) {
            	goods.setGoodsState(Integer.valueOf(goodsState));
            	model.addAttribute("goodsState", goodsState);// 弹出框分页附加条件
            }
            pager.setCondition(goods);// 实体加载在pager中
			List<Goods> results = goodsService.findGoodPagerList(pager);// 结果集
			model.addAttribute("datas", results);// 结果集
			model.addAttribute("pageNo", pager.getPageNo());// 当前页
            model.addAttribute("pageSize",pager.getPageSize());
			model.addAttribute("recordCount", pager.getTotalRows());// 总数
			model.addAttribute("toUrl", "/goods/goods/list");// 跳转URL
			model.addAttribute("div", div);// 显示的DIV数据区域
			model.addAttribute("gcId", gcId);// 弹出框分页附加条件
			model.addAttribute("goodsName", goodsName);// 弹出框分页附加条件

			// 转发请求到FTL页面
			return "/goods/goods/list";
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("导航失败!");
		}
	}

	/**
	 * 
	 * @Title: delGoods
	 * @Description: 删除商品
	 * @param @param ids
	 * @param @param model
	 * @param @return 设定文件
	 * @return Map<String,String> 返回类型
	 * @throws
	 */
	@RequiresPermissions("sys:goods:edit")
	@RequestMapping(value = "/delGoods", method = RequestMethod.POST)
	public @ResponseBody
	Map<String, String> delGoods(@RequestParam(value = "ids") String ids,
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
			goodsService.deleteGoods(Integer.parseInt(idStr));
			toStaticService.deleteGoodsDetailStaticPage(Integer.parseInt(idStr));
		}
		map.put("result", "删除成功");
		map.put(message, "true");
		return map;
	}

    /**
     * 根据父节点返回对应子节点字符串
     * @param gcId
     * @return
     */
    private List<String> createGcIds(Integer gcId){
        //如果父节点为null，子节点不选
        if(gcId == null || gcId == 0){
            return null;
        }else{
            List<String> gcIds = Lists.newArrayList();
            List<GoodsClass> list = goodsClassService.findList(gcId);
            gcIds.add(gcId+"");
            gcIds = Collections3.union(gcIds, Collections3.extractToList(list, "gcId"));
            for(GoodsClass vo : list){
                List<GoodsClass> childList = goodsClassService.findList(vo.getGcId());
                if(Collections3.isNotEmpty(childList)){
                    gcIds = Collections3.union(gcIds,Collections3.extractToList(childList,"gcId"));
                }
            }
            return gcIds;
        }
    }
    /**
     * 校验分类下是否还有商品
     * @param model
     * @param gcId 分类id
     * @return
     */
    @RequestMapping("/validateGoodsByGcId")
	public  @ResponseBody Boolean validateGoodsByGcId(@RequestParam Integer id) {
			List<Goods> results = goodsService.findGoodListByGcId(id);
			if(results.size()> 0){
	            return false;
	        }else{
	            return true;
	        }
	}
}
