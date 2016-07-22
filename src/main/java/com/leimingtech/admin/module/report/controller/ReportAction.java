package com.leimingtech.admin.module.report.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.leimingtech.core.common.CommonConstants;
import com.leimingtech.core.common.Constants;
import com.leimingtech.core.common.DateUtils;
import com.leimingtech.core.entity.base.GoodsClick;
import com.leimingtech.core.entity.base.MemberRegister;
import com.leimingtech.core.jasperreport.JasperExport;
import com.leimingtech.service.module.goods.service.GoodsService;
import com.leimingtech.service.module.report.service.BalanceReportService;
import com.leimingtech.service.module.report.service.MemberReportService;
import com.leimingtech.service.module.report.service.OrderReportService;
import com.leimingtech.service.module.report.service.StoreReportService;

/**
 * 
 * 创建人：cgl   
 * 创建时间：2015年08月03日16:04:35
 */
@Controller
@Slf4j
@RequestMapping("/report")
public class ReportAction {
	
	@Autowired
	private OrderReportService orderReportService;
	
	@Autowired
	private StoreReportService storeReportService;
	
	@Autowired
	private MemberReportService memberReportService;
	
	@Autowired
	private BalanceReportService balanceReportService;
	@Autowired
	private TransactionAwareDataSourceProxy dataSourceProxy;
	@Autowired
	private GoodsService goodsService;
	
	
	
    /**
	 * 查看店铺的销售信息
	 * @return
	 */
	@RequestMapping("/orderIndex")
	public String sellIndex(
			Model model,
			Integer storeId,
			@RequestParam(value="toUrl",defaultValue="orderReport") String toUrl,
			@RequestParam(value = "startTime", required=false) String startTimeStr,
			@RequestParam(value = "endTime", required=false) String endTimeStr,
			@RequestParam(value = "condition", required=false, defaultValue="%Y-%m-%d") String condition){
		model.addAttribute("baseUrl", "orderIndex");
		/*//开始时间
		if(!StringUtils.isNotEmpty(startTimeStr)){
			startTimeStr = new Timestamp(System.currentTimeMillis()).toString();
		}
		model.addAttribute("startTime", startTimeStr);
		//结束时间
		if(!StringUtils.isNotEmpty(endTimeStr)){
			endTimeStr = new Timestamp(System.currentTimeMillis()).toString();
		}
		model.addAttribute("endTime", endTimeStr);*/
		model.addAttribute("toUrl", toUrl);
		model.addAttribute("condition", condition);
		model.addAttribute("storeId", storeId);
		return "/report/store/order-index";
	}
	

	/**
	 * 查看店铺的销售信息
	 * @return
	 */
	@RequestMapping("/orderReport")
	public void storeSellCount(
			Integer storeId,
			@RequestParam(value = "startTime", required=false) String startTimeStr,
			@RequestParam(value = "endTime", required=false) String endTimeStr,
			@RequestParam(value = "condition", required=false) String condition,
			@RequestParam(value = "orderState", required=false) String orderState,
			String exportType,
			HttpServletRequest request, 
			HttpServletResponse response){
		try {
			String reportPath = request.getRealPath("/")+"jasperreportTemplate/" + Constants.REPORT_ORDER + "/" + "orderTemplate" + ".jrxml";
			JasperDesign design = JRXmlLoader.load(reportPath);
			JRDesignQuery query =(JRDesignQuery) design.getQuery();
			//动态改变sql
			if(StringUtils.isNotBlank(startTimeStr)||StringUtils.isNotBlank(endTimeStr)||StringUtils.isNotBlank(orderState)){
				query.setText(getSql(startTimeStr, endTimeStr,orderState));
			}
			System.out.println("sql------------------------"+query.getText());
			JasperReport jasperReport = JasperCompileManager.compileReport(design);
			HashMap<String, Object> param = new HashMap<String, Object>();
			param.put("storeId",storeId);
			param.put("dateFormat",condition);
			Connection connection = getConnection();
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,param,connection);
			//关闭连接
			CloseConnection(connection);
				JasperExport jasperExport = new JasperExport();
				//生成文件
				jasperExport.export(request, response,jasperPrint,exportType);
		} catch (Exception e) {
			log.error("",e);
		}finally{
			try {
				response.flushBuffer();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private String getSql(String startTimeStr,String endTimeStr,String orderState){
		StringBuffer sb=new StringBuffer();
		sb.append("(select FROM_UNIXTIME(create_time/1000,$P{dateFormat}) date,count(1) num from shop_order  where store_id=$P{storeId} "+getWhere(startTimeStr, endTimeStr,orderState)+" group by  FROM_UNIXTIME(create_time/1000,$P{dateFormat}) limit 10)");
		sb.append("union");
		sb.append("(select '总订单数' date,count(1) num from shop_order  where store_id=$P{storeId} "+getWhere(startTimeStr, endTimeStr,orderState)+"  )");
		return sb.toString();
	}
	private String getWhere(String startTimeStr,String endTimeStr,String orderState){
		StringBuffer sb=new StringBuffer();
		if(StringUtils.isNotBlank(startTimeStr)){
			sb.append(" and create_time>="+DateUtils.strToLong(startTimeStr,"yyyy-MM-dd"));
		}
		if(StringUtils.isNotBlank(endTimeStr)){
			sb.append(" and create_time<="+DateUtils.strToLong(endTimeStr,"yyyy-MM-dd"));
		}
		if(StringUtils.isNotBlank(orderState)){
			sb.append(" and order_state="+orderState);
		}
		
		return sb.toString();
	}
	private String getBalanceSql(String startTimeStr,String endTimeStr,String balanceState){
		StringBuffer sb=new StringBuffer();
		sb.append("(select FROM_UNIXTIME(finnshed_time/1000,$P{dateFormat}) date,sum(goods_amount) as '商品总价格', sum(discount) as '折扣价格' ,sum(order_amount) as '支付金额' , sum(goods_amount+shipping_fee)as '订单总金额',sum(shipping_fee) as '运费价格' ,sum(voucher_price) as '代金券金额' from shop_order s_order where  payment_state=1 " );
		sb.append(" and order_state=40 AND STORE_ID=$P{storeId} "+getBalanceWhere(startTimeStr, endTimeStr, balanceState)+" GROUP BY FROM_UNIXTIME(finnshed_time/1000,$P{dateFormat}) limit 10) " );
		sb.append("union  " );
		sb.append("(  " );
		sb.append("select '总计' as date,sum(goods_amount) as '商品总价格', sum(discount) as '折扣价格' ,sum(order_amount) as '支付金额' , sum(goods_amount+shipping_fee)as '订单总金额',sum(shipping_fee) as '运费价格' ,sum(voucher_price) as '代金券金额' from shop_order s_order where  payment_state=1 and order_state=40 AND STORE_ID=$P{storeId}  "+getBalanceWhere(startTimeStr, endTimeStr, balanceState) );
		sb.append(")  " );
		return sb.toString();
		
	}
	private String getBalanceWhere(String startTimeStr,String endTimeStr,String balanceState){
		StringBuffer sb=new StringBuffer();
		if(StringUtils.isNotBlank(startTimeStr)){
			sb.append(" and finnshed_time>="+DateUtils.strToLong(startTimeStr,"yyyy-MM-dd"));
		}
		if(StringUtils.isNotBlank(endTimeStr)){
			sb.append(" and finnshed_time<="+DateUtils.strToLong(endTimeStr,"yyyy-MM-dd"));
		}
		if(StringUtils.isNotBlank(balanceState)){
			sb.append(" and balance_state="+balanceState);
		}
		
		return sb.toString();
	}
	/**
	 * 流量统计
	 * @return
	 */
	@RequestMapping("/balanceIndex")
	public ModelAndView balanceIndex(
			Integer storeId,
			@RequestParam(value="toUrl",defaultValue="balance") String toUrl,
			@RequestParam(value = "condition", required=false, defaultValue="%Y-%m-%d") String condition){
		ModelAndView model = new ModelAndView("/report/balance/balance-index");
		model.addObject("baseUrl", "balanceIndex");
		//开始时间
		model.addObject("toUrl", toUrl);
		model.addObject("storeId", storeId);
		model.addObject("condition", condition);
		return model;
	}
	
	/**
	 * 商品价格趋势
	 * @return
	 */
	@RequestMapping("/balance")
	public void balance(
			Integer storeId,
			@RequestParam(value = "startTime", required=false) String startTimeStr,
			@RequestParam(value = "endTime", required=false) String endTimeStr,
			@RequestParam(value = "condition", required=false) String condition,
			@RequestParam(value = "balanceState", required=false) String balanceState,
			String exportType,
			HttpServletRequest request, 
			HttpServletResponse response){
		try {
//			BalanceReport balanceReport = new BalanceReport();
			//设置店铺id
			String reportPath = request.getRealPath("/")+"jasperreportTemplate/" + Constants.REPORT_BALANCE + "/" + "BalanceReport" + ".jrxml";
			JasperDesign design = JRXmlLoader.load(reportPath);
			JRDesignQuery query =(JRDesignQuery) design.getQuery();
			//动态改变sql
			if(StringUtils.isNotBlank(startTimeStr)||StringUtils.isNotBlank(endTimeStr)||StringUtils.isNotBlank(balanceState)){
				query.setText(getBalanceSql(startTimeStr, endTimeStr,balanceState));
			}
			System.out.println("sql------------------------"+query.getText());
			JasperReport jasperReport = JasperCompileManager.compileReport(design);
			HashMap<String, Object> param = new HashMap<String, Object>();
			param.put("storeId",storeId);
			param.put("dateFormat",condition);
			Connection connection = getConnection();
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,param,connection);
			//关闭连接
			CloseConnection(connection);
				JasperExport jasperExport = new JasperExport();
				//生成文件
			jasperExport.export(request, response,jasperPrint,exportType);
			
		} catch (Exception e) {
			log.error("",e);
		}finally{
			try {
				response.flushBuffer();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/**
	 * 店铺商品流量
	 * @return
	 */
	@RequestMapping("/goodsClickIndex")
	public String goodsClickIndex(
			Model model,
			Integer storeId,
			@RequestParam(value="toUrl",defaultValue="goodsClickReport") String toUrl){
		model.addAttribute("baseUrl", "goodsClickIndex");
		model.addAttribute("toUrl", toUrl);
		model.addAttribute("storeId", storeId);
		return "/report/goods/goods-click-index";
	}
	
	/**
	 * @param storeId
	 * @param startTimeStr
	 * @param endTimeStr
	 * @param condition 查询类型 week 
	 * @param exportType 导出类型  html、 pdf 、excel
	 * @param request
	 * @param response
	 * @author kwg
	 */
	@RequestMapping("/goodsClickReport")
	public void goodsClickReport(
			Integer storeId,
			String exportType,
			HttpServletRequest request, 
			HttpServletResponse response){
		try {
			String reportPath = request.getRealPath("/")+"jasperreportTemplate/" + Constants.REPORT_GOODS + "/" + "goodsClick_" + ".jrxml";
			JasperDesign design = JRXmlLoader.load(reportPath);
			JRDesignQuery query =(JRDesignQuery) design.getQuery();
			//动态改变sql
			JasperReport jasperReport = JasperCompileManager.compileReport(design);
			HashMap<String, Object> param = new HashMap<String, Object>();
			param.put("storeId", storeId);
			Connection connection = getConnection();
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,param,connection);
			//关闭连接
			CloseConnection(connection);
				JasperExport jasperExport = new JasperExport();
				//生成文件
				jasperExport.export(request, response,jasperPrint,exportType);
			
		} catch (Exception e) {
			log.error("",e);
		}finally{
			try {
				response.flushBuffer();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	
	/**
	 * 所有店铺的商品流量前十
	 * @return
	 */
	@RequestMapping("/allGoodsClickIndex")
	public String allGoodsClickIndex(
			Model model,
			@RequestParam(value="toUrl",defaultValue="allGoodsClickReport") String toUrl,
			@RequestParam(value = "startTime", required=false) String startTimeStr,
			@RequestParam(value = "endTime", required=false) String endTimeStr,
			@RequestParam(value = "condition", required=false, defaultValue="today") String condition){
		model.addAttribute("baseUrl", "allGoodsClickIndex");
		//开始时间
		if(!StringUtils.isNotEmpty(startTimeStr)){
			startTimeStr = new Timestamp(System.currentTimeMillis()).toString();
		}
		model.addAttribute("startTime", startTimeStr);
		//结束时间
		if(!StringUtils.isNotEmpty(endTimeStr)){
			endTimeStr = new Timestamp(System.currentTimeMillis()).toString();
		}
		model.addAttribute("endTime", endTimeStr);
		model.addAttribute("toUrl", toUrl);
		model.addAttribute("condition", condition);
		return "/report/goods/all-goods-click-index";
	}
	
	
	/**
	 * 所有店铺的商品流量前十
	 * @return
	 */
	@RequestMapping("/allGoodsClickReport")
	public void allGoodsClickReport(
			Integer storeId,
			@RequestParam(value = "startTime", required=false) String startTimeStr,
			@RequestParam(value = "endTime", required=false) String endTimeStr,
			@RequestParam(value = "condition", required=false) String condition,
			String exportType,
			HttpServletRequest request, 
			HttpServletResponse response){
		try {
			GoodsClick goodsClick = new GoodsClick();
			//开始时间
			goodsClick.setStartTime(DateUtils.strToLong(startTimeStr));
			//结束时间
			goodsClick.setEndTime(DateUtils.strToLong(endTimeStr));
			goodsClick.setCondition(condition);
			String reportPath = CommonConstants.REPORT_BASEPATH + Constants.REPORT_GOODS + "/" + "GoodsClick" + ".jasper";
			List<GoodsClick> data = storeReportService.getAllGoodsClick(goodsClick);
			if(data.isEmpty()){
			}else{
				JasperExport jasperExport = new JasperExport();
				if(StringUtils.isNotEmpty(exportType)){
					if(exportType.equals("pdf")){
						jasperExport.exportPdf(request, response, reportPath, getConnection());
					}else if(exportType.equals("excel")){
						jasperExport.exportExcel(request, response, reportPath, getConnection());
					}
				}else{
					jasperExport.exportHtml(request, response, reportPath, getConnection());
				}
			}
			
		} catch (Exception e) {
			log.error("",e);
		}finally{
			try {
				response.flushBuffer();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 新注册用户
	 * @return
	 */
	@RequestMapping("/memberReportIndex")
	public String memberReportIndex(
			Model model,
			@RequestParam(value="toUrl",defaultValue="memberReport") String toUrl,
			@RequestParam(value = "startTime", required=false) String startTimeStr,
			@RequestParam(value = "endTime", required=false) String endTimeStr,
			@RequestParam(value = "condition", required=false, defaultValue="today") String condition){
		model.addAttribute("baseUrl", "memberReportIndex");
		//开始时间
		if(!StringUtils.isNotEmpty(startTimeStr)){
			startTimeStr = new Timestamp(System.currentTimeMillis()).toString();
		}
		model.addAttribute("startTime", startTimeStr);
		//结束时间
		if(!StringUtils.isNotEmpty(endTimeStr)){
			endTimeStr = new Timestamp(System.currentTimeMillis()).toString();
		}
		model.addAttribute("endTime", endTimeStr);
		model.addAttribute("toUrl", toUrl);
		model.addAttribute("condition", condition);
		return "/report/member/member-index";
	}
	
	
	/**
	 * 新注册用户
	 * @return
	 */
	@RequestMapping("/memberReport")
	public void memberReport(
			Integer storeId,
			@RequestParam(value = "startTime", required=false) String startTimeStr,
			@RequestParam(value = "endTime", required=false) String endTimeStr,
			@RequestParam(value = "condition", required=false) String condition,
			String exportType,
			HttpServletRequest request, 
			HttpServletResponse response){
		try {
			MemberRegister memberRegister = new MemberRegister();
			//开始时间
			memberRegister.setStartTime(DateUtils.strToLong(startTimeStr));
			//结束时间
			memberRegister.setEndTime(DateUtils.strToLong(endTimeStr));
			memberRegister.setCondition(condition);
			String reportPath = CommonConstants.REPORT_BASEPATH + Constants.REPORT_ADMIN + "/" + "MemberRegister" + ".jasper";
			List<MemberRegister> data = memberReportService.getMemberRegister(memberRegister);
			if(data.isEmpty()){
			}else{
				JasperExport jasperExport = new JasperExport();
				if(StringUtils.isNotEmpty(exportType)){
					if(exportType.equals("pdf")){
						jasperExport.exportPdf(request, response, reportPath, getConnection());
					}else if(exportType.equals("excel")){
						jasperExport.exportExcel(request, response, reportPath, getConnection());
					}
				}else{
					jasperExport.exportHtml(request, response, reportPath, getConnection());
				}
			}
			
		} catch (Exception e) {
			log.error("",e);
		}finally{
			try {
				response.flushBuffer();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/** 获取数据库连接
	 * @return
	 * @author kwg
	 */
	private Connection getConnection(){
		try {
			return dataSourceProxy.getConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 关闭数据库连接
	 * @param conn
	 * @author kwg
	 */
	private void CloseConnection(Connection conn){
		if(conn!=null){
			try {
				if(!conn.isClosed()){
					conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * @param storeId
	 * @param request
	 * @param response
	 * @author wh
	 * ajax获取商品点击数据
	 */
	@RequestMapping("/goodsClickHighChart")
	public @ResponseBody String goodsClickHighChart(
			Integer storeId,
			HttpServletRequest request, 
			HttpServletResponse response){
		try {
			List<Map<String,Object>> goodsList=goodsService.countGoodsClick(storeId);
			JSONArray jsonArr = new JSONArray();  
	        JSONObject item = null;  
	        for (int i = 0; i<goodsList.size(); i++) {  
	        	Map<String,Object> map = goodsList.get(i);
	            item = new JSONObject();  
	            item.put(map.get("goods_name"),map.get("goods_click"));  
	            jsonArr.add(item);  
	        }
			log.debug("操作成功！");
			return jsonArr.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("导航失败!");
		}
	}
	
}
