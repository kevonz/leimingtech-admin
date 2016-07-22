package com.leimingtech.admin.module.trade.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.leimingtech.core.base.BaseController;
import com.leimingtech.core.common.CommonConstants;
import com.leimingtech.core.common.Constants;
import com.leimingtech.core.common.DateUtils;
import com.leimingtech.core.common.NumberUtils;
import com.leimingtech.core.common.excel.ExportExcelUtils;
import com.leimingtech.core.entity.base.OrderBill;
import com.leimingtech.core.entity.vo.BillExcelVo;
import com.leimingtech.core.entity.vo.BillVo;
import com.leimingtech.core.entity.vo.OrderBillExcelVo;
import com.leimingtech.service.module.trade.service.OrderBillService;
import com.leimingtech.service.utils.page.Pager;

/**
 * 结算相关
 * 
 * @author liukai
 */
@Controller
@RequestMapping("/bill")
public class BillAction extends BaseController {

	@Resource
	private OrderBillService orderBillService;
	
	/**
	 * 订单结算管理
	 * 
	 * @return
	 */
	@RequiresPermissions("sys:orderbill:view")
	@RequestMapping(value = "/list")
	public String list(
			Model model,
			@RequestParam(required = false, value = "pageNo", defaultValue = "") String pageNoStr,
			@RequestParam(required = false, value = "storeName", defaultValue = "") String storeName,
			@RequestParam(required = false, value = "obStartTime", defaultValue = "") String obStartTime,
			@RequestParam(required = false, value = "obEndTime", defaultValue = "") String obEndTime,
			@RequestParam(required = false, value = "isCurrent", defaultValue = "1") Integer isCurrent
			){
		try {
			Pager pager = new Pager();
			BillVo billVo = new BillVo();
			
			// 店铺名称
			if (StringUtils.isNotBlank(storeName)) {
				billVo.setStoreName(storeName.trim());
			}
			
			//结算周期开始时间
			if(StringUtils.isNoneBlank(obStartTime)){
				billVo.setObStartTime(DateUtils.strToLong(obStartTime+" 00:00:00"));
			}
			
			//结算周期结束时间
			if(StringUtils.isNoneBlank(obEndTime)){
				billVo.setObEndTime(DateUtils.strToLong(obEndTime+" 23:59:59"));
			}
			
			int count = orderBillService.findBillVoCount(billVo);
			
			if (StringUtils.isNotBlank(pageNoStr)) {
				pager.setPageNo(Integer.parseInt(pageNoStr));
			}
			pager.setCondition(billVo);// 实体加载在pager中
			pager.setPageSize(20);// 每页默认显示20条

			List<BillVo> results = orderBillService.findBillVoPagerList(pager);// 结果集

			pager.setResult(results);
			model.addAttribute("pager", pager);
			model.addAttribute("billVo", billVo);
			model.addAttribute("count", count);
			model.addAttribute("obStartTime", obStartTime);
			model.addAttribute("obEndTime",obEndTime);
			return "/trade/bill/list";
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("导航失败!");
		}
	}
	
	/**
	 * 账单明细列表
	 * @param model
	 * @param obId
	 *            结算id
	 * @return
	 */
	@RequiresPermissions("sys:orderbill:view")
	@RequestMapping("detailList")
	public String detailList(Model model,
			@RequestParam(required = false, value = "pageNo", defaultValue = "") String pageNoStr,
			@RequestParam(value = "obStartTime") Long obStartTime,
			@RequestParam(value = "obEndTime") Long obEndTime,
			@RequestParam(value = "storeId") Integer storeId) {
		try {
			Pager pager = new Pager();
			OrderBill orderBill = new OrderBill();
			//店铺id
			orderBill.setObStoreId(storeId);
			
			//开始时间
			orderBill.setObStartTime(obStartTime);
			//结束时间 
			orderBill.setObEndTime(obEndTime);
			
			if (StringUtils.isNotBlank(pageNoStr)) {
				pager.setPageNo(Integer.parseInt(pageNoStr));
			}
			pager.setCondition(orderBill);// 实体加载在pager中
			pager.setPageSize(10);// 每页默认显示20条

			List<OrderBill> results = orderBillService
					.findOrderBillPagerList(pager);// 结果集

			pager.setResult(results);
			model.addAttribute("pager", pager);
			model.addAttribute("storeId", storeId);
			model.addAttribute("obStartTime", obStartTime);
			model.addAttribute("obEndTime", obEndTime);
			return "/trade/bill/detailList";
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("加载失败!");
		}
	}
	
	/**
	 * 账单明细列表
	 * @return
	 */
	@RequiresPermissions("sys:orderbill:view")
	@RequestMapping(value = "/orderBillList")
	public String orderBillList(
			Model model,
			@RequestParam(required = false, value = "pageNo", defaultValue = "") String pageNoStr,
			@RequestParam(required = false, value = "storeName", defaultValue = "") String storeName,
			@RequestParam(required = false, value = "storeId", defaultValue = "") String storeId,
			@RequestParam(required = false, value = "obState", defaultValue = "") String obState,
			@RequestParam(required = false, value = "osMonth",defaultValue="") String osMonth,
			@RequestParam(required = false, value = "osYear",defaultValue="") String osYear,
			@RequestParam(required = false, value = "obNo",defaultValue="") String obNo,
			@RequestParam(required = false, value = "obStartTime", defaultValue = "") String obStartTime,
			@RequestParam(required = false, value = "obEndTime", defaultValue = "") String obEndTime){
		try {
			Pager pager = new Pager();
			OrderBill orderBill = new OrderBill();
			//店铺id
			if(StringUtils.isNotBlank(storeId)){
				orderBill.setObStoreId(Integer.valueOf(storeId));
			}
			
			// 结算状态
			if (StringUtils.isNotBlank(obState) && !"99".equals(obState)) {
				orderBill.setObState(Integer.valueOf(obState));
			}

			// 店铺名称
			if (StringUtils.isNotBlank(storeName)) {
				orderBill.setObStoreName(storeName.trim());
			}
			
			//账单所在年份
		    if(StringUtils.isNotBlank(osYear)){
		    	orderBill.setOsYear(Integer.valueOf(osYear));
		    }
		    
		    //账单所在年月份
		    if(StringUtils.isNotBlank(osMonth)){
		    	orderBill.setOsMonth(Integer.valueOf(osMonth));
		    }
		    
		    //账单编号
		    if(StringUtils.isNotBlank(obNo)){
		    	orderBill.setObNo(obNo);
		    }
		    
		    //结算周期开始时间
			if(StringUtils.isNoneBlank(obStartTime)){
				orderBill.setObStartTime(DateUtils.strToLong(obStartTime+" 00:00:00"));
			}
			
			//结算周期结束时间
			if(StringUtils.isNoneBlank(obEndTime)){
				orderBill.setObEndTime(DateUtils.strToLong(obEndTime+" 23:59:59"));
			}

			if (StringUtils.isNotBlank(pageNoStr)) {
				pager.setPageNo(Integer.parseInt(pageNoStr));
			}
			pager.setCondition(orderBill);// 实体加载在pager中
			pager.setPageSize(20);// 每页默认显示20条

			List<OrderBill> results = orderBillService
					.findOrderBillPagerList(pager);// 结果集

			pager.setResult(results);
			model.addAttribute("pager", pager);
			model.addAttribute("orderBill", orderBill);
			model.addAttribute("obStartTime", obStartTime);
			model.addAttribute("obEndTime",obEndTime);
			return "/trade/bill/billList";
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("导航失败!");
		}
	}

	/**
	 * 账单明细详情
	 * @param model
	 * @param obId
	 *            结算id
	 * @return
	 */
	@RequiresPermissions("sys:orderbill:view")
	@RequestMapping("orderBillDetail")
	public String orderBillDetail(Model model,
			@RequestParam(value = "obId") Integer obId) {
		try {
			OrderBill orderBill = orderBillService.findOrderBillById(obId);
			model.addAttribute("orderBill", orderBill);
			return "/trade/bill/billDetail";
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("加载失败!");
		}
	}

	/**
	 * 平台确认订单结算-页面跳转
	 * 
	 * @param model
	 * @param obId
	 * @return
	 */
	@RequiresPermissions("sys:orderbill:view")
	@RequestMapping("auditBillIndex")
	public String auditBillIndex(Model model,
			@RequestParam(value = "obId") Integer obId) {
		try {
			OrderBill orderBill = orderBillService.findOrderBillById(obId);
			model.addAttribute("orderBill", orderBill);
			return "/trade/bill/auditBill";
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("加载失败!");
		}
	}

	/**
	 * 平台确认订单结算
	 * 
	 * @param obId
	 *            结算id
	 * @param obPayTime
	 *            付款时间
	 * @param obPayContent
	 *            付款备注
	 * @return
	 */
	@RequiresPermissions("sys:orderbill:edit")
	@RequestMapping("auditBill")
	@ResponseBody
	public String auditBill(
			@RequestParam(value = "obId") Integer obId,
			@RequestParam(value = "obPayContent", required = false, defaultValue = "") String obPayContent) {
		try {
			int result = orderBillService.updateAdminAudit(obId, obPayContent);
			if (result == 1) {
				// 将成功的信号传导前台
				showSuccessJson("审核成功!");
			} else if (result == 2) {
				// 将失败的信号传到前台
				showErrorJson("数据不存在!");
			} else if (result == 3) {
				// 将失败的信号传到前台
				showErrorJson("请勿重复操作!");
			}
			return json;
		} catch (Exception e) {
			e.printStackTrace();
			// 将失败的信号传到前台
			showErrorJson("审核失败");
			return json;
		}
	}

	/**
	 * 导出结算明细信息excel
	 */
	@RequestMapping("/exportBillExcel")
	public void exportBillExcel(
			@RequestParam(required = false, value = "storeId", defaultValue = "") String storeId,
			@RequestParam(required = false, value = "storeName", defaultValue = "") String storeName,
			@RequestParam(required = false, value = "obState", defaultValue = "") String obState,
			@RequestParam(required = false, value = "osMonth", defaultValue="")String osMonth,
			@RequestParam(required = false, value = "osYear", defaultValue="")String osYear,
			@RequestParam(required = false, value = "obStartTime", defaultValue = "") String obStartTime,
			@RequestParam(required = false, value = "obEndTime", defaultValue = "") String obEndTime,
			HttpServletResponse response) throws Exception {
		OrderBill orderBill = new OrderBill();
		if(StringUtils.isNotBlank(storeId)){
			orderBill.setObStoreId(Integer.valueOf(storeId));
		}
		if(StringUtils.isNotBlank(storeName)){
			orderBill.setObStoreName(storeName);
		}
		// 结算状态
		if (StringUtils.isNotBlank(obState) && !"99".equals(obState)) {
			orderBill.setObState(Integer.valueOf(obState));
		}
		
		//账单所在年份
	    if(StringUtils.isNotBlank(osYear)){
	    	orderBill.setOsYear(Integer.valueOf(osYear));
	    }
	    
	    //账单所在年月份
	    if(StringUtils.isNotBlank(osMonth)){
	    	orderBill.setOsMonth(Integer.valueOf(osMonth));
	    }
	    
	    //结算周期开始时间
		if(StringUtils.isNoneBlank(obStartTime)){
			orderBill.setObStartTime(DateUtils.strToLong(obStartTime+" 00:00:00"));
		}
		
		//结算周期结束时间
		if(StringUtils.isNoneBlank(obEndTime)){
			orderBill.setObEndTime(DateUtils.strToLong(obEndTime+" 23:59:59"));
		}
		
		List<OrderBillExcelVo> billExcelList = orderBillService.findExcelVoList(orderBill);
		//新建一个结算总金额
		double resultTotals = 0.0;
		for(OrderBillExcelVo orderBillExcelVo:billExcelList){
			resultTotals += orderBillExcelVo.getObResultTotals().doubleValue();
		}
		resultTotals = NumberUtils.round(resultTotals, 2);
		if (billExcelList != null && billExcelList.size()!=0) {
			// 定义文件的标头
			String[] headers = { "结算单编号", "订单金额", "运费", "退单金额",
					"佣金金额", "退还佣金", "店铺促销活动费用", "应结金额", "结算单月份",
					"结算单年份", "店铺ID ", "店铺名" };
			String excelurl = ExportExcelUtils.export(billExcelList ,CommonConstants.FILE_BASEPATH + Constants.STORE_BILL_URL, headers, "总计:" + resultTotals);
			response.setContentType("application/x-msdownload");
			response.setHeader("Content-disposition", "attachment; filename="
					+ excelurl);
			BufferedInputStream bis = null;
			BufferedOutputStream bos = null;
			try {
				bis = new BufferedInputStream(new FileInputStream(CommonConstants.FILE_BASEPATH + Constants.STORE_BILL_URL + excelurl));
				bos = new BufferedOutputStream(response.getOutputStream());
				byte[] buff = new byte[2048000];
				int bytesRead = 0;
				while (-1 != (bytesRead = (bis.read(buff, 0, buff.length)))) {
					bos.write(buff, 0, buff.length);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (bis != null) {
					bis.close();
				}
				if (bos != null) {
					bos.close();
				}
			}
			return;
		}
	}
	
	/**
	 * 导出总账单信息excel
	 */
	@RequestMapping("/exportBillVoExcel")
	public void exportBillVoExcel(
			@RequestParam(required = false, value = "storeName", defaultValue = "") String storeName,
			@RequestParam(required = false, value = "obStartTime", defaultValue = "") String obStartTime,
			@RequestParam(required = false, value = "obEndTime", defaultValue = "") String obEndTime,
			HttpServletResponse response) throws Exception {
		BillVo billVo = new BillVo();
		
		// 店铺名称
		if (StringUtils.isNotBlank(storeName)) {
			billVo.setStoreName(storeName.trim());
		}
		
		//结算周期开始时间
		if(StringUtils.isNoneBlank(obStartTime)){
			billVo.setObStartTime(DateUtils.strToLong(obStartTime+" 00:00:00"));
		}
		
		//结算周期结束时间
		if(StringUtils.isNoneBlank(obEndTime)){
			billVo.setObEndTime(DateUtils.strToLong(obEndTime+" 23:59:59"));
		}
		
		List<BillExcelVo> billVoExcelList = orderBillService.findBillExcelVoList(billVo);
		
		if (billVoExcelList != null && billVoExcelList.size()!=0) {
			// 定义文件的标头
			String[] headers = { "店铺", "订单金额", "运费", "退单金额",
					"佣金金额", "退还佣金", "店铺促销活动费用", "应结金额" };
			String excelurl = ExportExcelUtils.export(billVoExcelList ,CommonConstants.FILE_BASEPATH + Constants.STORE_BILL_URL, headers);
			response.setContentType("application/x-msdownload");
			response.setHeader("Content-disposition", "attachment; filename="
					+ excelurl);
			BufferedInputStream bis = null;
			BufferedOutputStream bos = null;
			try {
				bis = new BufferedInputStream(new FileInputStream(CommonConstants.FILE_BASEPATH + Constants.STORE_BILL_URL + excelurl));
				bos = new BufferedOutputStream(response.getOutputStream());
				byte[] buff = new byte[2048000];
				int bytesRead = 0;
				while (-1 != (bytesRead = (bis.read(buff, 0, buff.length)))) {
					bos.write(buff, 0, buff.length);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (bis != null) {
					bis.close();
				}
				if (bos != null) {
					bos.close();
				}
			}
			return;
		}
	}
	
	/**
	 * 手动生成测试数据
	 * @return
	 */
	@RequestMapping("/manualOperationTest")
	@ResponseBody
	public String manualOperationTest(){
		try{
			//结算周期开始时间，上个月第一天
	    	String obStartTime = DateUtils.firstDayOfLastMonth();
	    	//结算周期结束时间，上个月最后一天
	    	String obEndTime = DateUtils.lastDayOfLastMonth();
	    	
	    	orderBillService.addBill(obStartTime, obEndTime);
	    	//将成功的信号传导前台
			showSuccessJson("手动生成测试数据成功");
			
    		return json;
		}catch (Exception e) {
			e.printStackTrace();
			//将失败的信号传到前台
			showErrorJson("手动生成测试数据失败!");
			return json;
		}
	}
}
