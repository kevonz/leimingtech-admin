/**
 * 
 */
package com.leimingtech.admin.module.goodsgroup.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.leimingtech.core.entity.base.Goods;
import com.leimingtech.core.entity.base.GoodsRecommend;
import com.leimingtech.core.entity.base.RelGoodsRecommend;
import com.leimingtech.core.state.goods.GoodsState;
import com.leimingtech.service.module.goods.service.GoodsRecommendService;
import com.leimingtech.service.module.goods.service.RelGoodsRecommendService;
import com.leimingtech.service.utils.page.Pager;

/**
 * <p>Title: GoodsGroupAction.java</p>
 * <p>Description: 首页组合商品 新品上市／推荐商品／狂购抢购／猜你喜欢</p>
 * <p>Copyright: Copyright (c) 2014-2018</p>
 * <p>Company: leimingtech.com</p>
 * @author linjm
 * @date 2015年8月5日
 * @version 1.0
 */
@Controller
@RequestMapping("/goodsGroup")
@Slf4j
public class GoodsGroupAction {
	
	
	@Resource
	private GoodsRecommendService goodsRecommendService;
	
	@Resource
	private RelGoodsRecommendService relGoodsRecommendService;
	
	@RequiresPermissions("sys:tag:view")
	@RequestMapping("/list")
	public String List(Model model,
			@RequestParam(required=false, value="pageNo",defaultValue="")String pageNo,
			@RequestParam(required=false, value="recommendInfo",defaultValue="")String recommendInfo
			){
		Pager pager = new Pager();
		/**查询条件，放入实体中，**/
		GoodsRecommend goodsRecommend = new GoodsRecommend();
		goodsRecommend.setRecommendInfo(recommendInfo);//栏目描述
		if(StringUtils.isNotBlank(pageNo)){
			pager.setPageNo(Integer.parseInt(pageNo));
		}
		pager.setCondition(goodsRecommend);//实体加载在pager中
		//int total = goodsRecommendService.findCount(pager);
		List<GoodsRecommend> results = goodsRecommendService.findPageList(pager);
		pager.setResult(results);
		//pager.setTotalRows(total);
		model.addAttribute("pager", pager);//总数
		model.addAttribute("recommendInfo", recommendInfo);
		return "/goodsgroup/list";
	}
		@RequiresPermissions("sys:tag:edit")
	    @RequestMapping(value="/save", method = RequestMethod.POST)
	    public String save(
	    	@ModelAttribute GoodsRecommend goodsRecommend,HttpServletRequest request){
		    goodsRecommendService.save(goodsRecommend);
	        //转发请求到FTL页面
	        return "redirect:/goodsGroup/list";
	    }
		
		@RequiresPermissions("sys:tag:edit")
	    @RequestMapping("/forward")
		public String add(Model model ,@RequestParam(required=false , value="reCommendid" ,defaultValue="")int reCommendid
				,@RequestParam(required=false , value="apId" ,defaultValue="0")int apId){
			if(reCommendid==0){
				return "/goodsgroup/add";
			}else{
				GoodsRecommend goodsRecommend=goodsRecommendService.findById(reCommendid);
				model.addAttribute("goodsRecommend", goodsRecommend);	
				return "/goodsgroup/edit";
			}
		}
	    
		@RequiresPermissions("sys:tag:edit")
	    @RequestMapping("/saveOrUpdate")
		public String saveOrUpdate(@ModelAttribute GoodsRecommend goodsRecommend,Model model,@RequestParam(required=false, value="startTime", defaultValue="") String startTime,
				@RequestParam(required=false, value="reCommendid", defaultValue="") Integer reCommendid,
				HttpServletRequest request, HttpServletResponse response){
			if(goodsRecommend!=null && reCommendid!=null){
				goodsRecommendService.update(goodsRecommend);
				model.addAttribute("msg", "修改成功");
			}else{
				goodsRecommendService.save(goodsRecommend);
				model.addAttribute("msg", "保存成功");
			}
			model.addAttribute("referer", CommonConstants.ADMIN_SERVER + "/goodsGroup/list");
			return Constants.MSG_URL;
		}
		
		@RequiresPermissions("sys:tag:edit")
	    @RequestMapping(value = "/deleteid", method = RequestMethod.POST)
	 	public @ResponseBody
	 	Map<String, String> deleteid(@RequestParam(value = "reCommendid") String reCommendid,
	 			Model model) {
	 		Map<String, String> map = Maps.newHashMap();
	 		if (StringUtils.isBlank(reCommendid)) {
	 			model.addAttribute("result", "ID为空");
	 			map.put("result", "ID为空");
	 			map.put("message", "true");
	 			return map;
	 		}
	 		goodsRecommendService.delete(Integer.valueOf(reCommendid));
	 		map.put("result", "删除成功");
	 		map.put("message", "true");
	 		return map;
	 	}
	    
	    /**
	     * 校验商品栏目下是否还有商品
	     * @return
	     */
	    @RequestMapping("/validatereCommendid")
	    public @ResponseBody Boolean validatereCommendid(@RequestParam Integer reCommendid){
	        //校验重复
	    	RelGoodsRecommend relGoodsRecommend=new RelGoodsRecommend();
	    	Goods goodt=new Goods();
			relGoodsRecommend.setReCommendId(reCommendid);
			goodt.setGoodsShow((GoodsState.GOODS_ON_SHOW));//商品上架状态
			goodt.setGoodsState(GoodsState.GOODS_OPEN_STATE);////商品状态审核通过
			relGoodsRecommend.setGoods(goodt);
	    	List<RelGoodsRecommend> goodslist=relGoodsRecommendService.findgoodsList(relGoodsRecommend);
	    	relGoodsRecommend=null;//释放资源
			goodt=null;
	        if(goodslist.size()> 0){
	            return false;
	        }else{
	            return true;
	        }
	    }
}
