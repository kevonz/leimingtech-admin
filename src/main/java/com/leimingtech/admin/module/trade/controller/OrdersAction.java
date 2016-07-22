package com.leimingtech.admin.module.trade.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.leimingtech.core.base.BaseController;
import com.leimingtech.core.common.Constants;
import com.leimingtech.core.common.DateUtils;
import com.leimingtech.core.entity.AliPayRefund;
import com.leimingtech.core.entity.Area;
import com.leimingtech.core.entity.Order;
import com.leimingtech.core.entity.WeiRefund;
import com.leimingtech.core.entity.base.OrderAddress;
import com.leimingtech.core.entity.base.Payment;
import com.leimingtech.core.entity.base.RefundReturn;
import com.leimingtech.core.entity.vo.ReturnDetailVo;
import com.leimingtech.extend.module.payment.module.alipay.pc.china.refund.service.AlipayRefundService;
import com.leimingtech.extend.module.payment.module.whchat.h5.refund.service.WechatRefundService;
import com.leimingtech.extend.module.payment.module.whchat.mobile.refund.service.WechatMobileRefundService;
import com.leimingtech.service.module.area.service.AreaService;
import com.leimingtech.service.module.setting.service.PaymentService;
import com.leimingtech.service.module.trade.common.OrderState;
import com.leimingtech.service.module.trade.service.OrderAddressService;
import com.leimingtech.service.module.trade.service.OrderService;
import com.leimingtech.service.module.trade.service.RefundReturnService;
import com.leimingtech.service.utils.page.Pager;

/**
 * 订单
 * @author liukai
 */
@Controller
@RequestMapping("/orders")
@Slf4j
public class OrdersAction extends BaseController{

    String message = "success";

    @Resource
    private PaymentService paymentService;
    @Resource
    private OrderService orderService;
    @Resource
    private RefundReturnService refundReturnService;
    @Resource
    private OrderAddressService orderAddressService;
    @Resource
    private AreaService areaService;
    @Resource
	private AlipayRefundService alipayRefundService;
    @Resource
	private WechatRefundService wechatRefundService;
    @Resource
	private WechatMobileRefundService wechatMobileRefundService;

    /**
     * 订单列表
     * @param @return 设定文件
     * @return String 返回类型
     * @throws
     * @Title: list
     * @Description: TODO(查询方法)
     */
    @RequiresPermissions("sys:order:view")
    @RequestMapping("/list")
    public String list(
            Model model,
            @RequestParam(required = false, value = "pageNo", defaultValue = "") String pageNo,
            @RequestParam(required = false, value = "searchStartTime", defaultValue = "") String searchStartTime,
            @RequestParam(required = false, value = "searchEndTime", defaultValue = "") String searchEndTime,
            @ModelAttribute Order order) {
    	Pager pager = new Pager();
        if (StringUtils.isNotBlank(pageNo)) {
            pager.setPageNo(Integer.parseInt(pageNo));
        }
        if(StringUtils.isNotBlank(order.getOrderSn())){
        	String orderSn = order.getOrderSn().trim();
        	order.setOrderSn(orderSn);
        }
        if (StringUtils.isNotBlank(searchStartTime)) {
            order.setStartTime(DateUtils.strToLong(searchStartTime+" 00:00:00"));
        }
        if (StringUtils.isNotBlank(searchEndTime)) {
            order.setEndTime(DateUtils.strToLong(searchEndTime+" 23:59:59"));
        }
        if (StringUtils.isNotBlank(pageNo)) {
            pager.setPageNo(Integer.parseInt(pageNo));
        }
        pager.setCondition(order);// 实体加载在pager中
        List<Order> results = orderService.findOrderList(pager);// 结果集
        pager.setResult(results);
        model.addAttribute("pager", pager);
        model.addAttribute("order", order);
        model.addAttribute("searchStartTime", searchStartTime);
        model.addAttribute("searchEndTime", searchEndTime);
        List<Payment> paymentList = paymentService.querybystatelist(1+"");
        model.addAttribute("datas", paymentList);// 结果集
        return "/trade/orders/ordersList";
    }

    /**
     * @param @return 设定文件
     * @return String 返回类型
     * @throws
     * @Title: cancleOrder
     * @Description: TODO(取消订单)
     */
    @RequiresPermissions("sys:order:edit")
    @RequestMapping("/cancleOrder")
    public String cancleOrder(Model model,
                              @RequestParam(required = false, value = "id", defaultValue = "") String id
            , HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        Order order = new Order();
        order.setOrderId(Integer.valueOf(id));
        order.setOrderState(OrderState.ORDER_STATE_CANCLE);
        orderService.updateOrder(order);
        model.addAttribute("referer", referer);
        model.addAttribute("msg", "取消成功");
        return Constants.MSG_URL;

    }

    /**
     * 显示订单详细信息
     *
     * @param model
     * @param id
     * @return
     */
    @RequiresPermissions("sys:order:view")
    @RequestMapping("/show")
    public String showOrder(Model model, @RequestParam int id) {

    	Order order = orderService.findById(id);
    	List<Area> areas = areaService.queryAll();
    	model.addAttribute("order", order);
    	model.addAttribute("areas", areas);
        return "trade/orders/orderDetail";
    }
    
    /**
     * 修改订单发货信息
     * @param orderAddress
     * @return
     */
    @RequiresPermissions("sys:order:edit")
    @RequestMapping("/updateOrderAddress")
    @ResponseBody
    public String updateOrderAddress(@ModelAttribute OrderAddress orderAddress){
    	try{
    		// 验证提交数据有效性
			if (!beanValidatorForJson(orderAddress)){
				return json;
			}
			orderAddressService.updateAddress(orderAddress);
			//将成功的信号传导前台
			showSuccessJson("发货地址保存成功");
			
    		return json;
		}catch(Exception e){
			e.printStackTrace();
			//将失败的信号传到前台
			showErrorJson("商品数据保存异常");
			return json;
		}
    }
    
    /**
     * 订单结算列表
     * @param 设定文件
     * @return String 返回类型
     * @throws
     * @Title: list
     * @Description: TODO(查询方法)
     */
    @RequiresPermissions("sys:settlement:view")
    @RequestMapping("/balanceList")
    public String balanceList(
            Model model,
            @RequestParam(required = false, value = "pageNo", defaultValue = "") String pageNo,
            @RequestParam(required = false, value = "startTime", defaultValue = "") String startTime,
			@RequestParam(required = false, value = "endTime", defaultValue = "") String endTime,
			@RequestParam(required = false, value = "storeName", defaultValue = "") String storeName) {
    	Pager pager = new Pager();
    	Order order = new Order();
        if (StringUtils.isNotBlank(pageNo)) {
            pager.setPageNo(Integer.parseInt(pageNo));
        }
        //店铺名称
        if(StringUtils.isNotBlank(storeName)){
        	order.setStoreName(storeName.trim());
        }
        //订单开始时间
	    if(StringUtils.isNotBlank(startTime)){
	    	order.setStartTime(DateUtils.strToLong(startTime+" 00:00:00"));
	    	model.addAttribute("startTime", startTime);
	    }
	    //订单截止时间
	    if(StringUtils.isNotBlank(endTime)){
	    	order.setEndTime(DateUtils.strToLong(endTime+" 23:59:59"));
	    	model.addAttribute("endTime", endTime);
	    }
        order.setOrderState(OrderState.ORDER_STATE_FINISH); //查询已完成的订单
        pager.setCondition(order);// 实体加载在pager中
        List<Order> results = orderService.findOrderList(pager);// 结果集
        pager.setResult(results);
        model.addAttribute("pager", pager);
        model.addAttribute("order", order);
        List<Payment> paymentList = paymentService.queryAll();
        model.addAttribute("datas", paymentList);// 结果集
        return "/trade/orders/ordersBalanceList";
    }
    
    /**
     * 订单结算
     * @param orderSn
     * @param ids
     * @return
     */
    @RequiresPermissions("sys:settlement:edit")
    @RequestMapping("/balance")
    @ResponseBody
    public Map<String,Object> balance(@RequestParam(required = false, value = "orderSn", defaultValue = "") String orderSn,
    								  @RequestParam(required = false, value = "ids", defaultValue = "") String ids
    								 ){
    	Map<String,Object> map = new HashMap<String, Object>();
    	try {
			if(StringUtils.isNotBlank(orderSn)){ //单个更改
				orderService.updateBalanceOrder(orderSn);
			}else if(StringUtils.isNotBlank(ids)){ //批量更改
				orderService.updateBalanceOrderByIds(ids);
			}
			map.put("success", true);
		} catch (Exception e) {
			map.put("success", false);
			e.printStackTrace();
		}
    	return map;
    }
    
    
    /**
	 * 退货查询列表
	 * 
	 * @Title: returnList
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param apm 加载的
	 * @param @return 设定文件
	 * @return ModelAndView 返回类型
	 * @throws RuntimeException
	 */
    @RequiresPermissions("sys:return:view")
	@RequestMapping("/returnList")
	public String returnList(Model model,
							 @RequestParam(required = false, value = "type", defaultValue = "") String type,
						   	 @RequestParam(required = false, value = "key", defaultValue = "") String key,
						     @RequestParam(required = false, value = "pageNo", defaultValue = "") String pageNo,
						     @RequestParam(required = false, value = "startTime", defaultValue = "") String startTime,
						     @RequestParam(required = false, value = "endTime", defaultValue = "") String endTime) {
		RefundReturn refundReturn = new RefundReturn();
		
		if(StringUtils.isNotBlank(key)){
			if("orderSn".equals(type)){
				refundReturn.setOrderSn(key.trim());
				model.addAttribute("key", key);
			}else if("returnSn".equals(type)){
				refundReturn.setRefundSn(key.trim());
				model.addAttribute("key", key);
			}else if("storeName".equals(type)){
				refundReturn.setStoreName(key.trim());
				model.addAttribute("key", key);
			}
		}
		model.addAttribute("type",type); 
		
		if(StringUtils.isNotBlank(startTime)){
			refundReturn.setStartTime(DateUtils.strToLong(startTime+" 00:00:00"));
			model.addAttribute("startTime", startTime);
		}
		
		if(StringUtils.isNotBlank(endTime)){
			refundReturn.setEndTime(DateUtils.strToLong(endTime+" 23:59:59"));
			model.addAttribute("endTime", endTime);
		}
		
		Pager pager = new Pager();
		if(StringUtils.isNotEmpty(pageNo)){
			pager.setPageNo(Integer.parseInt(pageNo));
		}
		pager.setCondition(refundReturn);
        List<RefundReturn> refundReturnList = refundReturnService.findRefundReturnPagerList(pager);// 结果集
        pager.setResult(refundReturnList);
        model.addAttribute("pager", pager);
		return "/trade/orders/returnOrderList";
	}
	
	/**
     * 显示退货详细信息
     *
     * @param model
     * @param id
     * @return
     */
    @RequiresPermissions("sys:return:view")
    @RequestMapping("/returnDetail")
    public String returnDetail(Model model, @RequestParam int id) {

    	ReturnDetailVo returnDetailVo  = refundReturnService.findRefundReturnDetail(id, null, null);
    	model.addAttribute("refundReturn", returnDetailVo);
        return "trade/orders/returnOrderDetail";
    }
    
    /**
     * 退货退款审核页
     * @param model
     * @param id
     * @return
     */
    @RequiresPermissions("sys:return:view")
    @RequestMapping("/refundReturnAuditing")
    public String refundReturnAuditing(Model model, @RequestParam int id) {

    	ReturnDetailVo returnDetailVo  = refundReturnService.findRefundReturnDetail(id, null, null);
    	//根据退款id查询出订单的支付类型
    	String orderpaytype="";
    	if(returnDetailVo!=null && returnDetailVo.getOrderId()!=null){
    		Order order=orderService.findById(returnDetailVo.getOrderId());
    		orderpaytype=order.getPaymentCode();
    		//判断是否全部为余额支付
    		if(order.getOrderAmount().doubleValue()==0){
    			orderpaytype = "2";
    		}
    	}
    	model.addAttribute("orderpaytype", orderpaytype);
    	model.addAttribute("refundReturn", returnDetailVo);
        return "trade/orders/returnOrderAuditing";
    }
    
    
    
    /**
     * 退货退款审核页
     * @param model
     * @param id
     * @return
     */
    @RequiresPermissions("sys:return:edit")
    @RequestMapping("/auditing")
    public String auditing(Model model, @RequestParam int id,
    					   @RequestParam(required=false,value="adminMessage",defaultValue="") String adminMessage,
    					   @RequestParam(required=false,value="returntype",defaultValue="") String returntype,
    					   @RequestParam(required=false,value="orderpaytype",defaultValue="") String orderpaytype,
    					   HttpServletRequest request ,HttpServletResponse response) {
    	//returntype 值为0时表示人工确认后打钱给用户  1表示自动返款给用户
    	String backurl="/refund/refund_result";
    	//orderpaytype 等于2时为货到付款将执行人工退款
    	if(returntype.equals("1")&&!orderpaytype.equals("2")){
    		RefundReturn refundReturn=refundReturnService.findRefundReturnById(id);
    		if(refundReturn!=null){
    			Order order=orderService.findById(refundReturn.getOrderId());
    			if(order!=null){
    				if(order.getPaymentBranch().equals("alipay")){//支付宝退款
    					String bathno=System.currentTimeMillis()+"";//批次号
    					refundReturnService.saveBatchNo(id,bathno);//将批次号存入退款表
						AliPayRefund aliPayRefund=new AliPayRefund();
						//支付宝交易号 ，退款金额，退款理由
						aliPayRefund.setRefundAmountNum(1);//退款数量，目前是单笔退款
						aliPayRefund.setBatchNo(bathno);
						aliPayRefund.setDetaildata(order.getTradeSn(),refundReturn.getRefundAmount(),refundReturn.getReasonInfo());
						aliPayRefund.setNotifyurl("/refundpayment/payRefundback");
						toalirefund(aliPayRefund,response);
    			    }else if(order.getPaymentBranch().equals("open_weichatpay")){//微信开放平台支付
    			    	WeiRefund weiRefund=new WeiRefund();
    			    	weiRefund.setOutrefundno(order.getTradeSn());//微信交易号
    					weiRefund.setOuttradeno(order.getPaySn());//订单号
    					weiRefund.setRefundfee((int)(refundReturn.getRefundAmount().doubleValue()*100));//单位，整数微信里以分为单位
    					weiRefund.setTotalfee((int)(refundReturn.getRefundAmount().doubleValue()*100));
    					backurl=toweichatrefund(weiRefund,id,adminMessage,"open_weichatpay",model);
    			    	//toweichatrefund();
    			    }else if(order.getPaymentBranch().equals("mp_weichatpay")){//微信公共平台支付
    			    	WeiRefund weiRefund=new WeiRefund();
    			    	weiRefund.setOutrefundno(order.getTradeSn());//微信交易号
    					weiRefund.setOuttradeno(order.getPaySn());//订单号
    					weiRefund.setRefundfee((int)(refundReturn.getRefundAmount().doubleValue()*100));//单位，整数微信里以分为单位
    					weiRefund.setTotalfee((int)(refundReturn.getRefundAmount().doubleValue()*100));
    					backurl=toweichatrefund(weiRefund,id,adminMessage,"mp_weichatpay",model);
    			    }else if(order.getPaymentBranch().equals("pc_unionpay")){//银联pc端支付
    			    	
    			    }else if(order.getPaymentBranch().equals("mobile_unionpay")){//银联手机端支付
    			    	
    			    }
    		   }
    		}
    	}else{
    		refundReturnService.updateRefundReturnAudiReturn(id, adminMessage);
    		backurl = Constants.MSG_URL;
    	}
    	model.addAttribute("referer", "returnList");
    	model.addAttribute("msg", "审核成功");
    	return backurl;
    }
    
    /**
     * 跳到支付宝退款接口
     * @param weiRefund
     * @param id
     * @param adminMessage
     * @return
     */
    public void toalirefund(AliPayRefund aliPayRefund,HttpServletResponse response){
    	try {
			String sHtmlText = "";
			sHtmlText=alipayRefundService.toRefund(aliPayRefund);//构造提交支付宝的表单
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(sHtmlText);
		}catch(IOException e) {
			log.error("",e);
		}
    }
    
    /**
     * 跳到微信退款接口
     * @param aliPayRefund
     * @param response
     */
    public String toweichatrefund(WeiRefund weiRefund,Integer id,String adminMessage,String weitype,Model model){
        Map<String,Object> map=null;
        if(weitype.equals("open_weichatpay")){//微信开放平台退款
        	map=wechatMobileRefundService.toRefund(weiRefund);
        }else if(weitype.equals("mp_weichatpay")){//微信公共平台退款
        	map=wechatRefundService.toRefund(weiRefund);
    	}
    	String rebackurl="";
		String msg="";
    	if(map.size()!=0 && map.get("result_code").equals("SUCCESS")){
    		refundReturnService.updateRefundReturnAudiReturn(id, adminMessage);
			rebackurl="/refund/refund_result";
			msg = "审核成功";
			model.addAttribute("msg",msg);
	   }
      return rebackurl;
    }
    
}