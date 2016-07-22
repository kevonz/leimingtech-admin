package com.leimingtech.admin.module.platformstore;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;
import com.leimingtech.core.common.Constants;
import com.leimingtech.core.common.DateUtils;
import com.leimingtech.core.entity.Area;
import com.leimingtech.core.entity.Order;
import com.leimingtech.core.entity.base.Daddress;
import com.leimingtech.core.entity.base.Express;
import com.leimingtech.core.entity.base.Payment;
import com.leimingtech.core.entity.base.RefundLog;
import com.leimingtech.core.entity.base.RefundReturn;
import com.leimingtech.core.entity.base.StoreExtend;
import com.leimingtech.service.module.area.service.AreaService;
import com.leimingtech.service.module.setting.service.ExpressService;
import com.leimingtech.service.module.setting.service.PaymentService;
import com.leimingtech.service.module.store.service.StoreExtendService;
import com.leimingtech.service.module.trade.service.DaddressService;
import com.leimingtech.service.module.trade.service.OrderService;
import com.leimingtech.service.module.trade.service.RefundLogService;
import com.leimingtech.service.module.trade.service.RefundReturnService;
import com.leimingtech.service.utils.page.Pager;

@RequestMapping("/platformOrder")
@Controller
@Slf4j
public class PlatformOrderAction {
	
	@Resource
    private OrderService orderService;
	@Resource
    private PaymentService paymentService;
	@Resource
	private RefundLogService refundLogService;
	@Resource
	private DaddressService daddressService;
	@Resource
	private StoreExtendService storeExtendService;
	@Resource
	private AreaService areaService;
	@Resource
	private ExpressService expressService;
	@Resource
	private RefundReturnService refundReturnService;
	
	/**
     * @param @return 设定文件
     * @return String 返回类型
     * @throws
     * @Title: list
     * @Description: TODO(查询方法)
     */
    @RequestMapping("/list")
    public String list(
            Model model,
            @RequestParam(required = false, value = "pageNo", defaultValue = "") String pageNo,
            @ModelAttribute Order order) {
    	Pager pager = new Pager();
        if (StringUtils.isNotBlank(pageNo)) {
            pager.setPageNo(Integer.parseInt(pageNo));
        }
        order.setStoreId(Constants.PLATFORM_STORE_ID); //查找平台自营订单
        pager.setCondition(order);// 实体加载在pager中
        List<Order> results = orderService.findOrderList(pager);// 结果集
        pager.setResult(results);
        model.addAttribute("pager", pager);
        model.addAttribute("order", order);
        List<Payment> paymentList = paymentService.queryAll();
        model.addAttribute("datas", paymentList);// 结果集
        return "/platform/order/platformOrderList";
    }	
    
    /**
	 * 进入调整费用首页
	 * 
	 * @Title: cancelOrderIndex
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param apm 加载的
	 * @param @return 设定文件
	 * @return ModelAndView 返回类型
	 * @throws RuntimeException
	 */
	@RequestMapping("/updateAmountIndex")
	public ModelAndView updateAmountIndex(
			@RequestParam(required = false, value = "orderId", defaultValue = "") String orderId) {
		try {
			ModelAndView model = new ModelAndView("/platform/order/pOrderUpdateamount");
			Order order = orderService.findById(Integer.valueOf(orderId));
			model.addObject("order", order);
			return model;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("卖家中心首页加载失败！");
			throw new RuntimeException("导航失败!");
		}
	}
	
	/**
	 * 调整订单费用
	 * @param model
	 * @param orderId 订单id
	 * @param orderAmount 调整后的价格,订单总额
	 * @return
	 */
	@RequestMapping("/updateAmount")
	public @ResponseBody Map<String, Object> updateAmount(
			Model model,
			@RequestParam(required = false, value = "orderId", defaultValue = "") String orderId,
			@RequestParam(required = false, value = "orderAmount", defaultValue = "") String orderAmount
			) {
		Map<String,Object> map = new HashMap<String, Object>();
		try {
			Order order = new Order();
			order.setOrderId(Integer.valueOf(orderId));
			order.setOrderAmount(BigDecimal.valueOf(Double.valueOf(orderAmount)));
			orderService.updateOrder(order);
			map.put("success", true);
		} catch (Exception e) {
			map.put("success", false);
			e.printStackTrace();
			log.error("卖家中心首页加载失败！");
			throw new RuntimeException("导航失败!");
		}
		return map;
	}
	
	/**
	 * 进入确认订单首页(货到付款确认)
	 * 
	 * @Title: cancelOrderIndex
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param apm 加载的
	 * @param @return 设定文件
	 * @return ModelAndView 返回类型
	 * @throws RuntimeException
	 */
	@RequestMapping("/confirmOrderIndex")
	public ModelAndView confirmOrderIndex(
			@RequestParam(required = false, value = "orderId", defaultValue = "") String orderId) {
		try {
			ModelAndView model = new ModelAndView("/platform/order/pOrderConfirmOrder");
			Order order = orderService.findById(Integer.valueOf(orderId));
			model.addObject("order", order);
			return model;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("卖家中心首页加载失败！");
			throw new RuntimeException("导航失败!");
		}
	}
	
	/**
	 * 确认订单(货到付款确认订单)
	 * @param model
	 * @param orderId 订单id
	 * @param orderAmount 调整后的价格,订单总额
	 * @return
	 */
	@RequestMapping("/confirmOrder")
	public @ResponseBody Map<String, Object> confirmOrder(
			Model model,
			@RequestParam(required = false, value = "orderSn", defaultValue = "") String orderSn
			) {
		Map<String,Object> map = new HashMap<String, Object>();
		try {
			orderService.updateConfirmOrder(orderSn);
			map.put("success", true);
		} catch (Exception e) {
			map.put("success", false);
			e.printStackTrace();
			log.error("卖家中心首页加载失败！");
			throw new RuntimeException("导航失败!");
		}
		return map;
	}
	
	/**
	 * 进入订单退款页
	 * 
	 * @Title: cancelOrderIndex
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param apm 加载的
	 * @param @return 设定文件
	 * @return ModelAndView 返回类型
	 * @throws RuntimeException
	 */
	@RequiresPermissions("sys:platformrefund:view")
	@RequestMapping("/refundOrderIndex")
	public ModelAndView refundOrderIndex(
			@RequestParam(required = false, value = "logId", defaultValue = "") String logId) {
		try {
			ModelAndView model = new ModelAndView("/platform/order/pOrderRefund");
			RefundLog refundLog = refundLogService.findRefundLogByLogId(Integer.valueOf(logId));
			model.addObject("refundLog", refundLog);
			return model;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("卖家中心首页加载失败！");
			throw new RuntimeException("导航失败!");
		}
	}
	
	/**
	 * 订单退款
	 * @param model
	 * @param orderId 订单id
	 * @param orderAmount 调整后的价格,订单总额
	 * @return
	 */
	@RequiresPermissions("sys:platformrefund:edit")
	@RequestMapping("/refundOrder")
	public @ResponseBody Map<String, Object> refundOrder(
			Model model,
			@RequestParam(required = false, value = "logId", defaultValue = "") String logId,
			@RequestParam(required = false, value = "refundState", defaultValue = "") String refundState,
			@RequestParam(required = false, value = "refundMessage", defaultValue = "") String refundMessage) {
		Map<String,Object> map = new HashMap<String, Object>();
		try {
			refundLogService.updateRefundLogSeller(Integer.valueOf(logId), Integer.valueOf(refundState), refundMessage, System.currentTimeMillis());
			map.put("success", true);
		} catch (Exception e) {
			map.put("success", false);
			e.printStackTrace();
			log.error("卖家中心首页加载失败！");
			throw new RuntimeException("导航失败!");
		}
		return map;
	}
	
	/**
	 * 进入订单退货页
	 * 
	 * @Title: returnOrderIndex
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param apm 加载的
	 * @param @return 设定文件
	 * @return ModelAndView 返回类型
	 * @throws RuntimeException
	 */
	@RequiresPermissions("sys:platformreturngoods:view")
	@RequestMapping("/returnOrderIndex")
	public ModelAndView returnOrderIndex(
			@RequestParam(required = false, value = "refundId", defaultValue = "") String refundId) {
		try {
			ModelAndView model = new ModelAndView("/platform/order/pOrderReturn");
			RefundReturn refundReturn = refundReturnService.findRefundReturnDetail(Integer.valueOf(refundId), null, Constants.PLATFORM_STORE_ID);
			model.addObject("refundReturn", refundReturn);
			return model;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("卖家中心首页加载失败！");
			throw new RuntimeException("导航失败!");
		}
	}
	
	/**
	 * 订单退货
	 * @param model
	 * @param orderId 订单id
	 * @param orderAmount 调整后的价格,订单总额
	 * @return
	 */
	@RequiresPermissions("sys:platformreturngoods:edit")
	@RequestMapping("/returnOrder")
	public @ResponseBody Map<String, Object> returnOrder(
			Model model,
			@RequestParam(required = false, value = "refundId", defaultValue = "") String refundId,
			@RequestParam(required = false, value = "sellerState", defaultValue = "") String sellerState,
			@RequestParam(required = false, value = "sellerMessage", defaultValue = "") String sellerMessage) {
		Map<String,Object> map = new HashMap<String, Object>();
		try {
			refundReturnService.updateRefundReturnSeller(Integer.valueOf(refundId), Integer.valueOf(sellerState), sellerMessage, "平台自营");
			map.put("success", true);
		} catch (Exception e) {
			map.put("success", false);
			e.printStackTrace();
			log.error("卖家中心首页加载失败！");
			throw new RuntimeException("导航失败!");
		}
		return map;
	}
	
	/**
	 * 进入订单退货确认收货
	 * 
	 * @Title: refundReturnConfirmIndex
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param apm 加载的
	 * @param @return 设定文件
	 * @return ModelAndView 返回类型
	 * @throws RuntimeException
	 */
	@RequiresPermissions("sys:platformreturngoods:view")
	@RequestMapping("/refundReturnConfirmIndex")
	public ModelAndView refundReturnConfirmIndex(
			@RequestParam(required = false, value = "refundId", defaultValue = "") String refundId) {
		try {
			ModelAndView model = new ModelAndView("platform/order/returnConfirm");
			RefundReturn refundReturn = refundReturnService.findRefundReturnDetail(Integer.valueOf(refundId), null, Constants.PLATFORM_STORE_ID);
			model.addObject("refundReturn", refundReturn);
			return model;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("卖家中心首页加载失败！");
			throw new RuntimeException("导航失败!");
		}
	}
	
	/**
	 * 订单退货确认收货
	 * @param model
	 * @return
	 */
	@RequiresPermissions("sys:platformreturngoods:edit")
	@RequestMapping("/refundReturnConfirm")
	public @ResponseBody Map<String, Object> refundReturnConfirm(
			Model model,
			@RequestParam(required = false, value = "refundId", defaultValue = "") String refundId,
			@RequestParam(required = false, value = "receiveMessage", defaultValue = "") String receiveMessage) {
		Map<String,Object> map = new HashMap<String, Object>();
		try {
			refundReturnService.updateRefundReturnConfirm(Integer.valueOf(refundId), receiveMessage, "平台自营");
			map.put("success", true);
		} catch (Exception e) {
			map.put("success", false);
			e.printStackTrace();
			log.error("卖家中心首页加载失败！");
			throw new RuntimeException("导航失败!");
		}
		return map;
	}
	
	/**
	 * 进入退款记录页
	 * 
	 * @Title: cancelOrderIndex
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param apm 加载的
	 * @param @return 设定文件
	 * @return ModelAndView 返回类型
	 * @throws RuntimeException
	 */
	@RequiresPermissions("sys:platformrefund:view")
	@RequestMapping("/refundOrderList")
	public ModelAndView refundOrderList(@RequestParam(required = false, value = "pageNo", defaultValue = "") String pageNo,
            							@ModelAttribute RefundLog refundLog) {
		try {
			ModelAndView model = new ModelAndView("/platform/order/refundList");
			refundLog.setStoreId(Constants.PLATFORM_STORE_ID); //设为平台自营的退款记录
			
			Pager pager = new Pager();
			pager.setCondition(refundLog);
			
	        List<RefundLog> refundLogList = refundLogService.findRefundLogList(pager);// 结果集
	        pager.setResult(refundLogList);
	        model.addObject("pager", pager);
	        model.addObject("refundLog", refundLog); //结果集
			return model;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("卖家中心首页加载失败！");
			throw new RuntimeException("导航失败!");
		}
	}
	
	/**
	 * 退款查询页面
	 * 
	 * @Title: refundIndex
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param apm 加载的
	 * @param @return 设定文件
	 * @return ModelAndView 返回类型
	 * @throws RuntimeException
	 */
	@RequiresPermissions("sys:platformrefund:view")
	@RequestMapping("/refundLogDetail")
	public ModelAndView refundIndex(@RequestParam(value = "logId") Integer logId) {
		try {
			ModelAndView model = new ModelAndView("/platform/order/refundDetail");
			RefundLog refundLog = refundLogService.findRefundLogByLogId(logId);
			model.addObject("refundLog", refundLog);
			return model;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("卖家中心首页加载失败！");
			throw new RuntimeException("导航失败!");
		}
	}
	
	/**
	 * 进入退货记录页
	 * 
	 * @Title: returnOrderList
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param apm 加载的
	 * @param @return 设定文件
	 * @return ModelAndView 返回类型
	 * @throws RuntimeException
	 */
	@RequiresPermissions("sys:platformreturngoods:view")
	@RequestMapping("/returnOrderList")
	public ModelAndView returnOrderList(@RequestParam(required = false, value = "pageNo", defaultValue = "") String pageNo,
			@RequestParam(required = false, value = "orderSn", defaultValue = "") String orderSn,
			@RequestParam(required = false, value = "refundSn", defaultValue = "") String refundSn,
			@RequestParam(required = false, value = "buyerName", defaultValue = "") String buyerName,
				@RequestParam(required = false, value = "startTime", defaultValue = "") String startTime,
			    @RequestParam(required = false, value = "endTime", defaultValue = "") String endTime) {
		try {
			ModelAndView model = new ModelAndView("/platform/order/returnList");
			RefundReturn refundReturn = new RefundReturn();
			
			if(StringUtils.isNoneBlank(orderSn.trim())){
				refundReturn.setOrderSn(orderSn.trim());
			}
			
			if(StringUtils.isNoneBlank(refundSn.trim())){
				refundReturn.setRefundSn(refundSn.trim());
			}
			
			if(StringUtils.isNoneBlank(buyerName.trim())){
				refundReturn.setBuyerName(buyerName.trim());
			}
			
			if(StringUtils.isNotBlank(startTime)){
				refundReturn.setStartTime(DateUtils.strToLong(startTime+" 00:00:00"));
				model.addObject("startTime", startTime);
			}
			
			if(StringUtils.isNotBlank(endTime)){
				refundReturn.setEndTime(DateUtils.strToLong(endTime+" 23:59:59"));
				model.addObject("endTime", endTime);
			}
			refundReturn.setStoreId(Constants.PLATFORM_STORE_ID); //设置查找自营
			
			Pager pager = new Pager();
			pager.setCondition(refundReturn);
			
			List<RefundReturn> refundReturnList = refundReturnService.findRefundReturnPagerList(pager);// 结果集
	        pager.setResult(refundReturnList);
	        model.addObject("pager", pager);
	        model.addObject("refundReturn", refundReturn);
			return model;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("卖家中心首页加载失败！");
			throw new RuntimeException("导航失败!");
		}
	}
	
	/**
	 * 退货查询页面
	 * 
	 * @Title: returnOrderDetail
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param apm 加载的
	 * @param @return 设定文件
	 * @return ModelAndView 返回类型
	 * @throws RuntimeException
	 */
	@RequiresPermissions("sys:platformreturngoods:view")
	@RequestMapping("/refundReturnDetail")
	public ModelAndView refundReturnDetail(@RequestParam(value = "refundId") Integer refundId) {
		try {
			ModelAndView model = new ModelAndView("/platform/order/returnDetail");
			RefundReturn refundReturn = refundReturnService.findRefundReturnDetail(Integer.valueOf(refundId), null, Constants.PLATFORM_STORE_ID);
			model.addObject("refundReturn", refundReturn);
			return model;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("卖家中心首页加载失败！");
			throw new RuntimeException("导航失败!");
		}
	}
	
	/**
	 * 发货列表
	 * @param model
	 * @param id
	 * @return
	 */
	@RequiresPermissions("sys:platformdeliver:view")
	@RequestMapping("/shipments")
	public ModelAndView shipments(
			@RequestParam(required=false, value="pageNo",defaultValue="")String pageNoStr,
			@RequestParam(required=false, value="orderState",defaultValue="20")String orderState,
			@ModelAttribute Order order){
		try{
			ModelAndView model = new ModelAndView("/platform/order/tradeShipments");
			Pager pager = new Pager();
			
			if(StringUtils.isNotBlank(pageNoStr)){
				pager.setPageNo(Integer.parseInt(pageNoStr));
			}
			
			if(order.getOrderSn()!=null){
				order.setOrderSn(order.getOrderSn().trim());
			}
			
			pager.setPageSize(5);//设置每页五条数据
			order.setStoreId(Constants.PLATFORM_STORE_ID); //设置店铺id为平台自营店铺
			int[] orderStates = {Integer.valueOf(orderState),60};
			order.setOrderStates(orderStates); //设置多个订单状态,待发货与货到付款待发货
			pager.setCondition(order);//实体加载在pager中
			
			List<Order> results = orderService.findOrderList(pager);// 结果集
		    pager.setResult(results);
			
			model.addObject("orderLists", results);// 结果集
			model.addObject("pager", pager); //分页信息
			model.addObject("order", order); //查询条件实体
			model.addObject("orderState",orderState);
			model.addObject("pageSize", pager.getPageSize());// 每页显示条数
			return model;
		}catch (Exception e) {
			e.printStackTrace();
			log.error("发货加载失败！");
			throw new RuntimeException("发货加载失败!");
			
		}
	}
	
	/**
	 * 发货
	 * @return
	 */
	@RequiresPermissions("sys:platformdeliver:view")
	@RequestMapping("/shipments_deliver")
	public String shipmentsDeliver(
				Model model,
				@RequestParam(required=false, value="orderId",defaultValue="")String orderId){
		Order order = orderService.findOrderDetail(Integer.valueOf(orderId),null,Constants.PLATFORM_STORE_ID);
		List<Daddress> daddressList = daddressService.findDaddressByStoreId(Constants.PLATFORM_STORE_ID); //设置平台自营店铺
		List<Express> expressList = storeExtendService.findExpressByExpressIds(Constants.PLATFORM_STORE_ID); //设置平台自营店铺
		model.addAttribute("expressList", expressList);
		model.addAttribute("daddressList", daddressList);
		model.addAttribute("order",order);
		return  "/platform/order/pShipmentsDeliver";
	}
	
	/**
	 * 编辑发货信息
	 * @return
	 */
	@RequiresPermissions("sys:platformdeliver:view")
	@RequestMapping("/shipments_deliver_update")
	public String shipmentsDeliverUpdate(
				Model model,
				@RequestParam(required=false, value="orderId",defaultValue="")String orderId){
		Order order = orderService.findOrderDetail(Integer.valueOf(orderId),null,Constants.PLATFORM_STORE_ID);
		List<Daddress> daddressList = daddressService.findDaddressByStoreId(Constants.PLATFORM_STORE_ID); //设置店铺为平台自营店铺
		List<Express> expressList = storeExtendService.findExpressByExpressIds(Constants.PLATFORM_STORE_ID);
		model.addAttribute("expressList", expressList);
		model.addAttribute("daddressList", daddressList);
		model.addAttribute("order",order);
		return  "/platform/order/pShipmentsDeliverUpdate";
	}
	
	/**
	 *  保存发货信息
	 * @return
	 */
	@RequiresPermissions("sys:platformdeliver:edit")
	@RequestMapping("/shipments_save")  
	@ResponseBody
	public Map<String,Object> shipmentsSave(@RequestParam(required=false, value="orderSn",defaultValue="")String orderSn,
											@RequestParam(required=false, value="daddressId",defaultValue="0")String daddressId,
											@RequestParam(required=false, value="shippingExpressId",defaultValue="0")String shippingExpressId,
											@RequestParam(required=false, value="shippingCode",defaultValue="")String shippingCode,
											@RequestParam(required=false, value="deliverExplain",defaultValue="")String deliverExplain
											){
		Map<String,Object> map = new HashMap<String, Object>();
		try{
			orderService.updateDeliveryOrder(orderSn, Integer.valueOf(daddressId), Integer.valueOf(shippingExpressId), shippingCode, deliverExplain);
			map.put("success", true);
		}catch (Exception e) {
			map.put("success", false);
			e.printStackTrace();
			log.error("发货保存失败！");
			throw new RuntimeException("发货保存失败！");
		}
		return map;
	}
	
	/**
	 * 发货地址设置
	 * @return
	 */
	@RequiresPermissions("sys:platformdeliversetting:view")
	@RequestMapping("/shipments_daddress")
	public String shipmentsDaddress(Model model){
		List<Daddress> daddressList = daddressService.findDaddressByStoreId(Constants.PLATFORM_STORE_ID); //设置店铺为自营店铺
		model.addAttribute("daddressList", daddressList);
		return  "/platform/order/pShipmentsDaddress";
	}
	
	/**
	 * 发货设置页面
	 * @param model
	 * @param id
	 * @return
	 */
	@RequiresPermissions("sys:platformdeliversetting:view")
	@RequestMapping("/shipmentSetting")
	public ModelAndView shipments_setting(){
		try{
			ModelAndView model = new ModelAndView("/platform/order/pShipmentsSetting");
			List<Daddress> list = daddressService.findDaddressByStoreId(Constants.PLATFORM_STORE_ID); //设置店铺为平台自营店铺
			model.addObject("list", list);
			return model;
		}catch (Exception e) {
			e.printStackTrace();
			log.error("发货设置失败！");
			throw new RuntimeException("发货设置失败!");
		}
	}
	
	/**
	 * 设置默认发货地址
	 * @param addressId
	 * @return
	 */
	@RequiresPermissions("sys:platformdeliversetting:edit")
	@RequestMapping("/defaultDaddress")
	@ResponseBody
	public Map<String,Object> defaultDaddress(@RequestParam(required=false, value="addressId",defaultValue="")String addressId){
		Map<String,Object> map = new HashMap<String, Object>();
		try{
			daddressService.updateDefault(0, Integer.valueOf(addressId));
			map.put("success", true);
		}catch (Exception e) {
			map.put("success", false);
			e.printStackTrace();
			log.error("默认地址设置失败！");
			throw new RuntimeException("默认地址设置失败!");
		}
		return map;
	}
	
	/**
	 * 删除发货地址
	 * @param addressId
	 * @return
	 */
	@RequiresPermissions("sys:platformdeliversetting:edit")
	@RequestMapping("/deleteDaddress")
	@ResponseBody
	public Map<String,Object> deleteDaddress(@RequestParam(required=false, value="addressId",defaultValue="")String addressId){
		Map<String,Object> map = new HashMap<String, Object>();
		try{
			daddressService.deleteDaddress(Integer.valueOf(addressId));
			map.put("success", true);
		}catch (Exception e) {
			map.put("success", false);
			e.printStackTrace();
			log.error("删除地址失败！");
			throw new RuntimeException("删除地址成功!");
		}
		return map;
	}
	
	/**
	 * 添加发货地址
	 * @return
	 */
	@RequiresPermissions("sys:platformdeliversetting:view")
	@RequestMapping("/addAddress")
	public ModelAndView addAddress(){
		try{
			ModelAndView model = new ModelAndView("/platform/order/pDaddressAdd");
			
			List<Area> areas = areaService.queryAll();
			model.addObject("storeId", Constants.PLATFORM_STORE_ID);
			model.addObject("areas",areas);
			return model;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("卖家中心首页加载失败！");
			throw new RuntimeException("导航失败!");
		}
	}
	
	/**
	 * 保存发货地址
	 * @param model
	 * @param daddress
	 * @return
	 */
	@RequiresPermissions("sys:platformdeliversetting:edit")
	@RequestMapping("/saveAddress")
	@ResponseBody
	public Map<String, Object> saveAddress(Model model,@ModelAttribute Daddress daddress){
		Map<String, Object> map = Maps.newHashMap();
		try{
			if(daddressService.findDefaultCountByStoreId(Constants.PLATFORM_STORE_ID)==0){ //判断店铺下是否有默认选中的项
				daddress.setIsDefault("1");
			}else{
				daddress.setIsDefault("0");
			}
			daddressService.saveDaddress(daddress);
			map.put("success", true);
		} catch (Exception e) {
			map.put("success", false);
			e.printStackTrace();
			log.error("卖家中心首页加载失败！");
			throw new RuntimeException("导航失败!");
		}
		return map;
	}
	
	/**
	 * 修改发货地址
	 * @param addressId
	 * @return
	 */
	@RequiresPermissions("sys:platformdeliversetting:view")
	@RequestMapping("/updateAddress")
	public ModelAndView updateAddress(@RequestParam(required=false, value="id",defaultValue="")String addressId){
		try{
			ModelAndView model = new ModelAndView("/platform/order/pDaddressAdd");
			Daddress daddress = daddressService.findDaddressById(Integer.valueOf(addressId));
			List<Area> areas = areaService.queryAll();
			model.addObject("storeId", Constants.PLATFORM_STORE_ID);
			model.addObject("daddress", daddress);
			model.addObject("areas",areas);
			return model;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("卖家中心首页加载失败！");
			throw new RuntimeException("导航失败!");
		}
	}
	
	/**
	 * 修改发货地址
	 * @param model
	 * @param daddress
	 * @return
	 */
	@RequiresPermissions("sys:platformdeliversetting:edit")
	@RequestMapping("/updateAdd")
	@ResponseBody
	public Map<String, Object> updateAdd(Model model,@ModelAttribute Daddress daddress){
		Map<String, Object> map = Maps.newHashMap();
		try{
			daddressService.updateDaddress(daddress);
			map.put("success", true);
		} catch (Exception e) {
			map.put("success", false);
			e.printStackTrace();
			log.error("卖家中心首页加载失败！");
			throw new RuntimeException("导航失败!");
		}
		return map;
	}
	/**
	 * 默认物流公司
	 * @param model
	 * @param id
	 * @return
	 */
	@RequiresPermissions("sys:platformdeliversetting:view")
	@RequestMapping("/tradelogis")
	public ModelAndView tradelogis(){
		try{
			ModelAndView model = new ModelAndView("/platform/order/pLogisLtd");
			StoreExtend storeExtend = storeExtendService.findByStoreId(Constants.PLATFORM_STORE_ID);
			if(storeExtend!=null){
				String[] expressIds = storeExtend.getExpress().split(",");//将查询到的公司物流公司id几个拆分为字符串
				model.addObject("storeExtend", storeExtend);
				model.addObject("expressIds", expressIds);
			}
			List<Express> list = expressService.findExpressListByState(1,null);//查询所有物流公司
			
			model.addObject("list", list);
			return model;
		}catch (Exception e) {
			e.printStackTrace();
			log.error("默认物流公司加载失败！");
			throw new RuntimeException("默认物流公司加载失败!");
		}
	}
	
	/**
	 * 保存店铺物流公司
	 * @param storeId
	 * @param cexpress
	 * @return
	 */
	@RequiresPermissions("sys:platformdeliversetting:edit")
	@RequestMapping("/saveStoreExtend")
	@ResponseBody
	public Map<String,Object> saveStoreExtend(@RequestParam(required=false, value="storeId",defaultValue="")String storeId,
											  @RequestParam(required=false, value="cexpress[]",defaultValue="")String[] cexpress){
		Map<String,Object> map = new HashMap<String, Object>();
		try{
			StoreExtend storeExtend = new StoreExtend();
			String str = "";//新建一个字符串用来存储物流公司id
			for(String expressId:cexpress){
				str +=  expressId + ",";
			}
			storeExtend.setExpress(str);
			if("".equals(storeId)){
				storeExtend.setStoreId(Constants.PLATFORM_STORE_ID);
				storeExtendService.saveStoreExtend(storeExtend);
			}else{
				storeExtend.setStoreId(Integer.valueOf(storeId));
				storeExtendService.updateStoreExtend(storeExtend);
			}
			map.put("success", true);
		}catch (Exception e) {
			map.put("success", false);
			e.printStackTrace();
			log.error("添加物流公司失败！");
			throw new RuntimeException("添加物流公司失败!");
		}
		return map;
	}
}
