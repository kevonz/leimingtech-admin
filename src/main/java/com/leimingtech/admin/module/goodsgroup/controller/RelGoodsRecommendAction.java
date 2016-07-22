package com.leimingtech.admin.module.goodsgroup.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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
import com.leimingtech.core.common.Constants;
import com.leimingtech.core.common.DateUtils;
import com.leimingtech.core.entity.base.Goods;
import com.leimingtech.core.entity.base.RelGoodsRecommend;
import com.leimingtech.core.entity.base.Store;
import com.leimingtech.core.state.goods.GoodsState;
import com.leimingtech.service.module.goods.service.RelGoodsRecommendService;



/**
 * <p>Title: RelGoodsRecommend.java</p>
 * <p>Description: 首页组合商品 新品上市／推荐商品／狂购抢购／猜你喜欢/中间关联</p>
 * <p>Copyright: Copyright (c) 2014-2018</p>
 * <p>Company: leimingtech.com</p>
 * @author gyh
 * @date 2015年8月25日
 * @version 1.0
 */
@Controller
@RequestMapping("/RelGoodsRecommend")
public class RelGoodsRecommendAction {
	@Resource
	private RelGoodsRecommendService relGoodsRecommendService;
	
	    @RequestMapping(value="/save", method = RequestMethod.POST)
	    public  @ResponseBody Map<String, String> save(
	    	@RequestParam(required=false , value="reCommendid" ,defaultValue="0")Integer reCommendid,
	    	@RequestParam(required=false, value="goodsids",defaultValue="")String goodsids){
	        //转发请求到FTL页面
	    	Map<String, String> map = Maps.newHashMap();
			if (StringUtils.isBlank(goodsids)) {
				map.put("result", "ID为空");
				map.put("message", "true");
				return map;
			}
			if(goodsids!=null&&!"".equals(goodsids)){
				String[] idArray = StringUtils.split(goodsids, ",");
				for (String idStr : idArray) {
					if(!"".equals(idStr)){
						RelGoodsRecommend relGoodsRecommend=new RelGoodsRecommend();
						relGoodsRecommend.setReCommendId(reCommendid);
						relGoodsRecommend.setGoodsId(Integer.valueOf(idStr));
						relGoodsRecommendService.save(relGoodsRecommend);
						relGoodsRecommend=null;
					}
				}
			}
			map.put("result", "保存成功");
			map.put("message", "true");
			return map;
	 }
	    
	    /**
		 * 
		 * @Title: checkrecommendlist
		 * @Description: (加载数据页面)
		 * @return String 返回类型
		 * @throws
		 */
		@RequestMapping("/checkrecommendlist")
		public String recommendlist(Model model,
	            @RequestParam(required=false, value="reCommendId",defaultValue="")Integer reCommendId,
	            @RequestParam(required=false, value="goodsName",defaultValue="")String goodsName) {
				// 页面查询条件列表
				RelGoodsRecommend relGoodsRecommend=new RelGoodsRecommend();
				Goods goodt=new Goods();
				relGoodsRecommend.setReCommendId(reCommendId);
				goodt.setGoodsShow((GoodsState.GOODS_ON_SHOW));//商品上架状态
				goodt.setGoodsState(GoodsState.GOODS_OPEN_STATE);////商品状态审核通过
				goodt.setGoodsName(goodsName);//商品名称
				relGoodsRecommend.setGoods(goodt);
			    List<RelGoodsRecommend> checklist=relGoodsRecommendService.findgoodsList(relGoodsRecommend);
			    relGoodsRecommend=null;//释放资源
			    goodt=null;
			    model.addAttribute("goodsName",goodsName);
			    model.addAttribute("reCommendId",reCommendId);
	            model.addAttribute("checklist",checklist);
				// 转发请求到FTL页面
				return "/goodsgroup/checkrecommendlist";
		}
		  /**
		   * 
		   * @param id
		   * @return
		   */
		  @RequestMapping(value="/delete")
		    public  @ResponseBody Map<String, String> delete(
		    	@RequestParam(required=false , value="id" ,defaultValue="0")Integer id){
		        //转发请求到FTL页面
		    	Map<String, String> map = Maps.newHashMap();
		    	relGoodsRecommendService.delete(id);
				map.put("result", "删除成功");
				map.put("success", "true");
				return map;
		 }
		  
		  /**
		   * 修改排序
		   * @param relId
		   * @param sort
		   * @param model
		   * @param request
		   * @return
		   */
			@RequestMapping("/update")
			public String updateDetail(
			    @RequestParam(required=false, value="relId", defaultValue="") Integer[] relId,
			    @RequestParam(required=false, value="sort", defaultValue="") Integer[] sort,
			    Model model, HttpServletRequest request){
		        String referer = request.getHeader("Referer");
		        if(relId.length!=0){
		        	 for(int i=0;i<relId.length;i++){
		        		 RelGoodsRecommend relGoodsRecommend=new RelGoodsRecommend();
		        		 relGoodsRecommend.setRelId(relId[i]);
		        		 relGoodsRecommend.setSort(sort[i]);
		        		 relGoodsRecommendService.updaterel(relGoodsRecommend);
		        		 relGoodsRecommend=null;//释放资源
		        	 }
		        }
		        model.addAttribute("referer", referer);
		        model.addAttribute("msg", "编辑成功");
				return Constants.MSG_URL;
			}
		  
}
