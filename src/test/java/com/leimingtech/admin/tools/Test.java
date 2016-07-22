package com.leimingtech.admin.tools;

import com.allinpay.xmltrans.service.TranxServiceImpl;
import com.leimingtech.core.common.SpringContextUtil;
import com.leimingtech.extend.module.payment.module.allinpay.pc.pay.service.AllinpayService;

public  class Test {
	private static TranxServiceImpl serviceImpl;
	static{
		TranxServiceImpl serviceImpl = SpringContextUtil.getBean(TranxServiceImpl.class);
	}
	
	public static void main(String[] args) {
		// 调用测试方法
		try {
			serviceImpl.singleTranx(null, "https://113.108.182.3/aipg/ProcessServlet", "100011", "00600", false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
