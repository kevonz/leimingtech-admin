package com.leimingtech.admin.module.platformstore;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.leimingtech.core.common.CommonConstants;
import com.leimingtech.core.common.Constants;
import com.leimingtech.core.common.DateUtils;
import com.leimingtech.core.common.excel.ExportExcelUtils;
import com.leimingtech.core.entity.GoodsStatCount;
import com.leimingtech.core.entity.OrderCount;
import com.leimingtech.core.entity.OrderStaticExcel;
import com.leimingtech.core.entity.StoreGoodsSalCount;
import com.leimingtech.core.entity.vo.OrdermVo;
import com.leimingtech.core.platform.info.PlatformInfo;
import com.leimingtech.service.module.goods.service.ShopStatGoodsService;
import com.leimingtech.service.module.trade.common.OrderState;
import com.leimingtech.service.module.trade.service.OrderGoodsService;
import com.leimingtech.service.module.trade.service.OrderService;
import com.leimingtech.service.utils.page.Pager;

/**
 * action描述:平台关于报表跳转action
 * 创建人：cgl   
 * 创建时间：2015年08月03日16:04:35
 * 平台自营
 */
@Slf4j
@Controller
@RequestMapping("/platform/report")
public class PlatformReportAction {
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private ShopStatGoodsService shopStatGoodsService;
	@Autowired
	private OrderGoodsService orderGoodsService;
	
	String[] colorstr={"#CC0033","#cba952","#99CCCC","#99CC33","#003366","#FFFF00",
	          "#996600","#999966","#009966","#0099CC","#FFCC00","#CC3333",
	          "#FFFF00","#FF0033","#990066","#663399","#666699","#669933",
	          "#669933","#CCFFCC","#CC99CC","#CCCC99","#0099CC","#CCCC66",
	          "#666699","#CCCC66","##333300","#FF6666","#339999","#FFCC00"};
	/**
	 * 商品流量
	 */
	@RequiresPermissions("sys:platformflow:view")
	@RequestMapping("/goodsClick")
	public String goodsClick(){
		Integer storeId = PlatformInfo.PLATFORM_STORE_ID;
		return "forward:/report/goodsClickIndex?storeId="+storeId;
	}
	
	
	
	
	/**
	 * 销售情况
	 */
	@RequiresPermissions("sys:platformflow:view")
	@RequestMapping("/goodsSell")
	public String goodsSell(Model model){
		Integer storeId = PlatformInfo.PLATFORM_STORE_ID;
		return "forward:/report/orderIndex?storeId="+storeId;
	}
	
	/**
	 * 流量统计
	 * @return
	 */
	@RequiresPermissions("sys:platformflow:view")
	@RequestMapping("/clickIndex")
	public ModelAndView clickIndex(){
		ModelAndView model = new ModelAndView("/platform/report/click-index");
		return model;
	}
	
	/**
	 * 订单统计
	 * @return
	 */
	@RequiresPermissions("sys:platformflow:view")
	@RequestMapping("/orderIndex")
	public ModelAndView sellIndex(
			@RequestParam(value="toUrl",defaultValue="orderReport") String toUrl,
			@RequestParam(value = "condition", required=false, defaultValue="%Y-%m-%d") String condition){
		ModelAndView model = new ModelAndView("/platform/report/highchartorder-index");
		model.addObject("baseUrl", "orderIndex");
		model.addObject("toUrl", toUrl);
		model.addObject("condition", condition);
		return model;
	}
	
	
	/**
	 * 成交订单统计
	 * @return
	 */
	@RequiresPermissions("sys:platformflow:view")
	@RequestMapping("/knockdownIndex")
	public ModelAndView knockdownsellIndex(
			@RequestParam(value="toUrl",defaultValue="orderReport") String toUrl,
			@RequestParam(value = "condition", required=false, defaultValue="%Y-%m-%d") String condition,
			@RequestParam(value = "orderState", required=false) Integer orderState){
		ModelAndView model = new ModelAndView("/platform/report/knockdownorder-index");
		model.addObject("baseUrl", "orderIndex");
		model.addObject("toUrl", toUrl);
		model.addObject("condition",condition);
		model.addObject("orderState",orderState);
		return model;
	}
	
	/**
	 * @param storeId
	 * @param request
	 * @param response
	 * @author gyh
	 * ajax获取商品的浏览记录
	 */
	@RequestMapping("/ordercountHighChart")
	public @ResponseBody String ordercountHighChart(
			HttpServletRequest request, 
			HttpServletResponse response){
		try {
			Map<String,Object> ordercountmap=getmap(request);//获得相应条件map
			List<OrderCount> ordercountList=orderService.countorderbuy(ordercountmap);
			JSONArray jsonArr = new JSONArray();  
	        JSONObject item = null;  
	        if(ordercountList.size()!=0){
		        for (int i = 0; i<ordercountList.size(); i++) {  
		        	OrderCount ordercount=ordercountList.get(i);
		        	if(ordercount!=null){
			            item = new JSONObject();
			            item.put(ordercount.getDate(),Integer.valueOf(ordercount.getNum()));  
			            jsonArr.add(item); 
		          }
		        }
	        }
			log.debug("操作成功！");
			return jsonArr.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("导航失败!");
		}
	}
	
	
	/**
     * 导出订单信息
     */
	@RequiresPermissions("sys:platformflow:view")
	@RequestMapping("/loadordercount")
	public @ResponseBody Map<String, Object> loadordercount(HttpServletRequest request,HttpServletResponse response) throws Exception{
		    //定义文件的标头
		    Map<String, Object> map = new HashMap<String, Object>();
		    String orderstate=request.getParameter("orderState");//订单状态
		    Integer storeId = PlatformInfo.PLATFORM_STORE_ID;
		    String[] headers = { "订单号"," 买家名称","订单金额","支付金额","支付方式","订单状态","生成时间"};
		    map.put("storeId", storeId);
		    map.put("orderstate",orderstate);
		    List<OrderStaticExcel> orderlist=orderService.findorderexcel(map);
		    getlist(orderlist);//时间，支付方式，订单状态转换
			if(orderlist.size()!=0){
			    String excelurl= ExportExcelUtils.export(orderlist,CommonConstants.FILE_BASEPATH+Constants.STORE_ordercountexcel_URL,headers);
			    String filePath = CommonConstants.FILE_BASEPATH+Constants.STORE_ordercountexcel_URL+excelurl;
			    BufferedInputStream bis = null;
			    BufferedOutputStream bos = null;
			    try{
				     bis = new BufferedInputStream(new FileInputStream(filePath));
				     map.put("excelurl",Constants.STORE_ordercountexcel_URL+excelurl);
			         map.put("success", true);
					 map.put("message", "导出成功");
			    }catch(Exception e){
			    	 map.put("success",false);
					 map.put("message", "导出失败");
			    	 e.printStackTrace();
			    }finally{
			      if(bis != null){
			        bis.close();
			     }
			     if(bos != null){
			        bos.close();
			      }
			    }
         }
			return map;
	}
	
	/**
	 * 通过条件拼接map
	 */
	public Map<String,Object> getmap(HttpServletRequest request){
		Map<String,Object> ordercountmap=new HashMap<String,Object>();
		Integer storeId =  PlatformInfo.PLATFORM_STORE_ID;//获得店铺id
		String orderstate=request.getParameter("orderState");//订单状态
		String startime=request.getParameter("startime");//开始时间
		String endtime=request.getParameter("endtime");//结束时间
		String timesn=request.getParameter("condition");//时间标示
		ordercountmap.put("storeId",storeId);//店铺id
		if(StringUtils.isNotEmpty(timesn)){
			if(timesn.endsWith("%Y-%m-%d %h")){
				ordercountmap.put("timesn",timesn);
				ordercountmap.put("starttime",DateUtils.getStartTime());//当天开始时间
				ordercountmap.put("endtime",System.currentTimeMillis());//当天结束时间
			}
			else if(timesn.endsWith("%Y-%m-%d")){
				ordercountmap.put("timesn",timesn);
				ordercountmap.put("starttime",DateUtils.getweektime(1));//一周前
				ordercountmap.put("endtime",System.currentTimeMillis());//当天结束时间
			}
			else if(timesn.endsWith("%Y-%m %u")){
				ordercountmap.put("timesn","%Y-%m-%d");
				ordercountmap.put("starttime",DateUtils.getmonthtime(1));//一月前
				ordercountmap.put("endtime",System.currentTimeMillis());//当天结束时间
			}else if(timesn.endsWith("%Y-%m")){
				ordercountmap.put("timesn",timesn);
				ordercountmap.put("starttime",DateUtils.getyeartime(1));//一年前
				ordercountmap.put("endtime",System.currentTimeMillis());//当天结束时间
			}
		}
		if(orderstate!=null && !"".equals(orderstate)){
			ordercountmap.put("orderstate",orderstate);
			//按订单状态查询默认按当月的时间
			//ordercountmap.put("timesn","%Y-%m");
			//ordercountmap.put("starttime",DateUtils.getmonthtime(1));//一月前
			//ordercountmap.put("endtime",System.currentTimeMillis());//当天结束时间
		}
		if(StringUtils.isNotEmpty(startime) && StringUtils.isNotEmpty(endtime)){
			ordercountmap.put("timesn","%Y-%m-%d");
			ordercountmap.put("starttime",DateUtils.strToLong(startime));//开始时间
			ordercountmap.put("endtime",DateUtils.strToLong(endtime));//结束时间
		}
		return ordercountmap;
	}
	
	/**
	 * 
	 * @param orderselist
	 * @return
	 * 将时间，支付类型，订单状态转换
	 * 订单状态：Orderstate 0:已取消;10:待付款;20:待发货;30:待收货;40:交易完成;50:已提交;60:已确认;
	 * 支付方式名称代码 Paymentcode：
	 */
	public void getlist(List<OrderStaticExcel> orderselist){
		for(OrderStaticExcel orderstaticexcel:orderselist){
	    	if(orderstaticexcel!=null){
	    		if(StringUtils.isNotEmpty(orderstaticexcel.getOrderstate())){
	    			//订单状态装换
	    			orderstaticexcel.setOrderstate(OrderState.orderStatus(Integer.valueOf(orderstaticexcel.getOrderstate())));
	    		}
	    		if(StringUtils.isNotEmpty(orderstaticexcel.getPaymentcode())){
	    			if(orderstaticexcel.getPaymentcode().equals("2")){
	    				orderstaticexcel.setPaymentcode("货到付款");
	    			}else if(orderstaticexcel.getPaymentcode().equals("YL")||orderstaticexcel.getPaymentcode().equals("pc_unionpay")||orderstaticexcel.getPaymentcode().equals("mobile_unionpay")){
	    				orderstaticexcel.setPaymentcode("银联支付");
	    			}else if(orderstaticexcel.getPaymentcode().equals("ZFB")){
	    				orderstaticexcel.setPaymentcode("支付宝");
	    			}else if(orderstaticexcel.getPaymentcode().equals("weiscan")){
	    				orderstaticexcel.setPaymentcode("微信支付");
	    			}else if(orderstaticexcel.getPaymentcode().equals("open_weichatpay")||orderstaticexcel.getPaymentcode().equals("mp_weichatpay")){
	    				orderstaticexcel.setPaymentcode("微信支付");
	    			}else{
	    				orderstaticexcel.setPaymentcode("未付款");
	    			}
	    		}
	    		if(StringUtils.isNotEmpty(orderstaticexcel.getCreateTime())){
	    			orderstaticexcel.setCreateTime(DateUtils.getTimestampByLong(Long.valueOf(orderstaticexcel.getCreateTime()))+"");
	    		}
	    	}
	    }
	}
	
	
	 /**
     * @param @param  model
     * @param @param  div
     * @param @param  pageNoStr
     * @param @param  brandName
     * @param @return 设定文件
     * @return String 返回类型
     * @throws
     * @Title: list
     * @Description: 查询品牌列表
     */
	@RequiresPermissions("sys:platformflow:view")
    @RequestMapping("/highchartorderlist")
    public  ModelAndView list(
    		@ModelAttribute OrdermVo ordermVo,
            @RequestParam(required = false, value = "div", defaultValue = "") String div,
            @RequestParam(required = false, value = "pageNo") String pageNo
              ) {
        try {
        	ModelAndView model = new ModelAndView("/platform/report/highchartorderlist");
            Pager pager = new Pager();
            if (StringUtils.isNotBlank(pageNo)) {
                pager.setPageNo(Integer.parseInt(pageNo));
            }
            ordermVo.setStoreId(PlatformInfo.PLATFORM_STORE_ID);//店铺id
            pager.setCondition(ordermVo);
            // 页面查询条件订单统计列表
            List<OrdermVo> highcharorderList = orderService.findOrderinfo(pager);
            model.addObject("highcharorderList", highcharorderList);//
            model.addObject("pageNo", pager.getPageNo());// 当前页
            model.addObject("pageSize", pager.getPageSize());// 每页显示条数
            model.addObject("recordCount", pager.getTotalRows());// 总数
            model.addObject("pager", pager);
            model.addObject("div", div);// 显示的DIV数据区域
            model.addObject("toUrl", "/platform/report/highchartorderlist");// 跳转URL
            model.addObject("ordermVo", ordermVo);
            // 转发请求到FTL页面
            return model;
        } catch (Exception e) {
            log.error("导航失败!", e);
            throw new RuntimeException("导航失败!");
        }
    }
    
    /**
     * 订单销量统计表
     * @param ordermVo
     * @param div
     * @param pageNoStr
     * @param request
     * @return
     */
    @RequestMapping("/kdhighchartorderlist")
    public  ModelAndView kdlist(
    		@ModelAttribute OrdermVo ordermVo,
            @RequestParam(required = false, value = "div", defaultValue = "") String div,
            @RequestParam(required = false, value = "pageNo", defaultValue = "") String pageNoStr,
            HttpServletRequest request) {
        try {
        	ModelAndView model = new ModelAndView("/platform/report/highchartknoworderlist");
        	String orderstate=request.getParameter("orderstate");
            Pager pager = new Pager();
            ordermVo.setStoreId(PlatformInfo.PLATFORM_STORE_ID);//店铺id
            //订单状态
            if (StringUtils.isNotBlank(orderstate)) {
                ordermVo.setOrderState(Integer.valueOf(orderstate));
            }
            if (StringUtils.isNotBlank(pageNoStr)) {
                pager.setPageNo(Integer.parseInt(pageNoStr));
            }
            pager.setCondition(ordermVo);
            // 页面查询条件订单统计列表
            List<OrdermVo> highcharorderList = orderService.findOrderinfo(pager);
            model.addObject("highcharorderList", highcharorderList);//
            model.addObject("pageNo", pager.getPageNo());// 当前页
            model.addObject("pageSize", pager.getPageSize());// 每页显示条数
            model.addObject("recordCount", pager.getTotalRows());// 总数
        	model.addObject("pager", pager);
            model.addObject("div", div);// 显示的DIV数据区域
            model.addObject("toUrl", "/platform/report/kdhighchartorderlist");// 跳转URL
            model.addObject("ordermVo", ordermVo);
            // 转发请求到FTL页面
            return model;
        } catch (Exception e) {
            log.error("导航失败!", e);
            throw new RuntimeException("导航失败!");
        }
    }
    
    /**
	 * 
	 * @param request
	 * @param response
	 * @author gyh
	 * ajax获取商品的浏览记录
	 */
	@RequiresPermissions("sys:platformflow:view")
	@RequestMapping("/goodsstatcount")
	public @ResponseBody String goodsstatcount(
			HttpServletRequest request, 
			HttpServletResponse response){
		try {
			Map<String,Object> goodsstatcountmap=getmap(request);//获得相应条件map
			goodsstatcountmap.put("limitcount",30);//限制显示的条数
			List<GoodsStatCount> goodsStatcountList=shopStatGoodsService.findStatbytime(goodsstatcountmap);
			JSONArray jsonArr = new JSONArray();  
	        JSONObject item = null;  
	        if(goodsStatcountList.size()!=0){
	        	System.out.println("size:"+goodsStatcountList.size());
		        for (int i = 0;i<goodsStatcountList.size();i++) {
		        	GoodsStatCount goodsStatCount=goodsStatcountList.get(i);
		        	if(goodsStatCount!=null){
			            item = new JSONObject();
			            item.put("name",goodsStatCount.getGoodsName());//商品名称
			            item.put("y",goodsStatCount.getGoodsStatCount());//商品浏览量
			            item.put("color",colorstr[i]);
			            jsonArr.add(item); 
		          }
		        }
		        if(goodsStatcountList.size()<30){
		        	for(int j=0;j<(30-goodsStatcountList.size());j++){
		        		item = new JSONObject();
		        		item.put("name","");//商品名称
			            item.put("y",0);
			            item.put("color","#cba952");
			            jsonArr.add(item); 
		        	}
		        }
	        }
			log.debug("操作成功！");
			return jsonArr.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("导航失败!");
		}
	}
	
	
	/**
	 * 店铺商品成交量统计
	 * @return
	 */
	@RequiresPermissions("sys:platformflow:view")
	@RequestMapping("/storeGoodsSales")
	public ModelAndView storeGoodsSalesIndex(){
		ModelAndView model = new ModelAndView("/platform/report/storegoodssales");
		return model;
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @author gyh
	 * ajax获取商铺商品前30名成交量
	 */
	@RequiresPermissions("sys:platformflow:view")
	@RequestMapping("/goodssalcount")
	public @ResponseBody String goodssalcount(
			HttpServletRequest request, 
			HttpServletResponse response){
		try {
			Map<String,Object> storeDoodsSalCountmap=getmap(request);//获得相应条件map
			storeDoodsSalCountmap.put("limitcount",30);//限制显示的条数
			storeDoodsSalCountmap.put("orderstate",OrderState.ORDER_STATE_FINISH);//40:交易完成
			List<StoreGoodsSalCount> storeDoodsSalCountList=orderGoodsService.storeDoodsSalCount(storeDoodsSalCountmap);
			JSONArray jsonArr = new JSONArray();  
	        JSONObject item = null;  
	        if(storeDoodsSalCountList.size()!=0){
	        	System.out.println("size:"+storeDoodsSalCountList.size());
		        for (int i = 0;i<storeDoodsSalCountList.size();i++) {
		        	StoreGoodsSalCount storeGoodsSalCount=storeDoodsSalCountList.get(i);
		        	if(storeGoodsSalCount!=null){
			            item = new JSONObject();
			            item.put("name",storeGoodsSalCount.getGoodsName());//商品名称
			            item.put("y",storeGoodsSalCount.getGoodsSales());//商品成交量
			            item.put("color",colorstr[i]);
			            jsonArr.add(item); 
		          }
		        }
		        if(storeDoodsSalCountList.size()<30){
		        	for(int j=0;j<(30-storeDoodsSalCountList.size());j++){
		        		item = new JSONObject();
		        		item.put("name","");//商品名称
			            item.put("y",0);
			            item.put("color","#cba952");
			            jsonArr.add(item); 
		        	}
		        }
	        }
			log.debug("操作成功！");
			return jsonArr.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("导航失败!");
		}
	}
}