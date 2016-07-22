package com.leimingtech.admin.module.setting.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Maps;
import com.leimingtech.core.common.CommonConstants;
import com.leimingtech.core.common.Constants;
import com.leimingtech.core.entity.base.Payment;
import com.leimingtech.core.jackson.JsonUtils;
import com.leimingtech.service.module.setting.service.PaymentService;

/**
 * 支付方式设置
 * @author 
 */
@Controller
@RequestMapping("/setting/payment")
@Slf4j
public class PaymentAction {
	@Autowired
	private PaymentService paymentService;

	String message = "success";

	/**
	 * 查询支付方式
	 * @Title: list 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 * @param @param model
	 * @param @return    设定文件 
	 * @return String    返回类型 
	 * @throws
	 */
	@RequiresPermissions("sys:payment:view")
	@RequestMapping(value = "/list")
	public String list(Model model) {
		List<Payment> results = paymentService.queryAll();
		model.addAttribute("datas", results);// 结果集
		// 转发请求到FTL页面
		return "/setting/payment/list";
	}
	
	/**
	 * 弹出的新增层
	 * @Title: addpayment 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 * @param @return    设定文件 
	 * @return String    返回类型 
	 * @throws
	 */
	@RequiresPermissions("sys:payment:edit")
	@RequestMapping(value = "/addpayment")
	public String addpayment(Model model,
			@RequestParam(required=false, value="paymentId",defaultValue="")String paymentId){
		if(StringUtils.isBlank(paymentId)){
			return "/setting/payment/addpayment";
		}else{
			Payment dbpayment = paymentService.findById(Long.valueOf(paymentId));
			model.addAttribute("dbpayment",dbpayment);
			return "/setting/payment/editpayment";
		}
	}
	
	
	 /**
     * 编辑或修改用户
     * @param payment
     * @param model
     * @return
     */
	@RequiresPermissions("sys:payment:edit")
    @RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST)
    public String saveOrUpdate(@ModelAttribute Payment payment,HttpServletRequest request, Model model) {
        String referer = request.getHeader("Referer");
        String msg="";
        if (payment.getPaymentId() == null) {
        	paymentService.save(payment);
        	msg="添加成功";
        } else {
        	//Payment dbpayment = paymentService.findById(payment.getPaymentId());
            //if (dbpayment == null) {
            //}else {
//            	dbpayment.setPaymentId(payment.getPaymentId());
//            	dbpayment.setPaymentCode(payment.getPaymentCode());
//            	dbpayment.setPaymentName(payment.getPaymentName());
//                dbpayment.setPaymentState(payment.getPaymentState());
//                dbpayment.setPaymentLogo(payment.getPaymentLogo());
                paymentService.update(payment);
                msg="修改成功";
            //}
        }
        model.addAttribute("referer", referer);
        model.addAttribute("msg",msg);
        return Constants.MSG_URL;
    }
    
    
    /**
	  * 
	  * @Title: delDemo 
	  * @Description: TODO(这里用一句话描述这个方法的作用) 
	  * @param @param ids
	  * @param @param model
	  * @param @return    设定文件 
	  * @return Map<String,String>    返回类型 
	  * @throws
	  */
	@RequiresPermissions("sys:payment:edit")
   @RequestMapping(value = "/deletePayment", method = RequestMethod.POST)
   public @ResponseBody Map<String, String> deletePayment(
		   @RequestParam(required=false, value="paymentId",defaultValue="")String paymentId,
		   Model model) {
       
   		Map<String, String> map = Maps.newHashMap();
   	
   		if (StringUtils.isBlank(paymentId)) {
           model.addAttribute("result", "ID为空");
           map.put("result", "ID为空");
           map.put(message, "true");
           return map;
   		}
   		paymentService.delete(Long.parseLong(paymentId));
   		map.put("result", "删除成功");
   		map.put(message, "true");
   		return map;
   }
   
   @RequestMapping(value = "/fileUpload")
   public String fileUploads(@RequestParam MultipartFile[] files,
                            HttpServletRequest request, HttpServletResponse response) throws IOException {
       //可以在上传文件的同时接收其它参数
       Map<String, Object> map = Maps.newHashMap();
       try {
           map = com.leimingtech.core.common.FileUtils.fileUpload(files,
                   CommonConstants.FILE_BASEPATH, Constants.STORE_PAYMENT_LOGO, request,"images",1);
       } catch (IOException e) {
           e.printStackTrace();
           log.error("上传文件失败", e.toString());
       }
       String json = JsonUtils.toJsonStr(map);
       response.setContentType("text/html");
       response.getWriter().write(json);

       return null;
   }
    
    
}
