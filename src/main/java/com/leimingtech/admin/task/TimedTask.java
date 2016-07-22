package com.leimingtech.admin.task;


import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.leimingtech.core.common.DateUtils;
import com.leimingtech.core.entity.Order;
import com.leimingtech.core.entity.base.Goods;
import com.leimingtech.core.state.goods.GoodsState;
import com.leimingtech.service.module.goods.service.GoodsService;
import com.leimingtech.service.module.setting.service.SettingService;
import com.leimingtech.service.module.trade.common.OrderState;
import com.leimingtech.service.module.trade.service.OrderBillService;
import com.leimingtech.service.module.trade.service.OrderService;
import com.leimingtech.service.utils.page.Pager;


/**
 * @author linjm
 * @Description:定时任务
 * @date 2017/7/7
 */
@Component
public class TimedTask {
	
	@Resource
	private OrderService orderService;
	
	@Resource
	private GoodsService goodsService;
	
	@Resource
	private OrderBillService orderBillService;
	
	@Resource
	private SettingService settingService;
	
//	/** 京东联盟接口 */
//	@Resource
//	private UnionJDService unionJDService;

//	 每隔5秒执行一次：*/5 * * * * ?
//    每隔1分钟执行一次：0 */1 * * * ?
//    每天23点执行一次：0 0 23 * * ?
//    每天凌晨1点执行一次：0 0 1 * * ?
//    每月1号凌晨1点执行一次：0 0 1 1 * ?
//    每月最后一天23点执行一次：0 0 23 L * ?
//    每周星期天凌晨1点实行一次：0 0 1 ? * L
//    在26分、29分、33分执行一次：0 26,29,33 * * * ?
//    每天的0点、13点、18点、21点都执行一次：0 0 0,13,18,21 * * ?
    //任务执行时间设置 10分钟一次
    @Scheduled(cron="0 */10 * * * ?")
    public void timedTask(){
    	System.out.println("＝＝＝＝＝＝＝＝定时任务执行中＝＝＝＝＝＝＝＝＝＝");
    }
    
//	/**
//	 * 定时刷新京东联盟token 每一小时执行一次
//	 * 注：京东联盟应用未上线token有效时间为24小时，上线后有效时间为1年，在有效期内可以重复刷新token以此来延长有效时间
//	 * 
//	 */
//	@Scheduled(cron = "0 */60 * * * ?")
//	public void RefreshAccessToken() {
//		System.out
//				.println("========================京东联盟token定时刷新任务开始 当前频率为一小时执行一次========================");
//		APIResult result = unionJDService.getRefreshAccessToken();
//		System.out.println("==============京东联盟token定时刷新任务本次结束："
//				+ result.getCode() + "\t" + result.getMsg() + "===========");
//	}
    
    /**
     * 订单24小时不支付自动取消订单
     * 每晚12:00执行
     */
    @Scheduled(cron="59 59 23 * * ? ")
    public void canCleOrder(){
    	int pageSize = 100;
    	Pager pager = new Pager();
    	pager.setPageSize(pageSize);
    	Order order = new Order();
    	order.setOrderState(OrderState.ORDER_STATE_NO_PATMENT);
    	
    	int count = orderService.findOrderCount(order);
    	int rate = count/pageSize;
    	
    	for (int i = 0; i < rate; i++) {
    		pager.setPageNo(i);
    		List<Order> orderList = orderService.findOrderList(pager);
    		for (int j = 0; j < orderList.size(); j++) {
    			Order order1 = orderList.get(j);
    			// 添加时间时间戳
    			long addLong = order1.getCreateTime();
    			// 当前时间时间戳
    			Date nowDate = new Date();
    	    	long nowLong = nowDate.getTime();
    	    	// 24小时的时间戳
    	    	long chaTime = 1000 * 60 * 60 * 24;
    	    	// 如果当前时间－添加时间大于24小时自动取消
    	    	if((nowLong - addLong) > chaTime){
    	    		orderService.updateCancelOrder(order1.getOrderSn(),"买家未及时付款,订单定时取消",3);
    	    	}
    	    	
    		}
		}
    }
    
    /**
     * 商品定时上架
     * 每晚12:00执行
     */
    @Scheduled(cron="59 59 23 * * ? ")
    public void goodsGrounding(){
    	int pageSize = 100;
    	Pager pager = new Pager();
    	pager.setPageSize(pageSize);
    	Goods goods = new Goods();
    	goods.setGoodsState(GoodsState.GOODS_OPEN_STATE);
    	goods.setGoodsShow(GoodsState.GOODS_OFF_SHOW);
    	goods.setUpdateTime(System.currentTimeMillis());
    	pager.setCondition(goods);
    	int count = goodsService.countGoods(goods);
    	int rate = count/pageSize;
    	for (int i = 0; i < rate; i++) {
    		pager.setPageNo(i);
    		List<Goods> goodsList = goodsService.findGoodPagerList(pager);
    		for (int j = 0; j < goodsList.size(); j++) {
    			Goods goods2 = goodsList.get(j);
    			Goods goods3 = new Goods();
    			goods3.setGoodsId(goods2.getGoodsId());
    			goods3.setGoodsShow(GoodsState.GOODS_ON_SHOW);
    			try {
					goodsService.updateGoods(goods3);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
		}
    	
    	
    }
    
    
    /**
     * 订单发货以后14天后自动改为完成状态
     * 每晚12:00执行
     */
    @Scheduled(cron="59 59 23 * * ? ")
    public void finishOrder(){
    	int pageSize = 100;
    	Pager pager = new Pager();
    	pager.setPageSize(pageSize);
    	Order order = new Order();
    	order.setOrderState(OrderState.ORDER_STATE_NOT_RECEIVING);
    	
    	int count = orderService.findOrderCount(order);
    	int rate = count/pageSize;
    	
    	for (int i = 0; i < rate; i++) {
    		pager.setPageNo(i);
    		List<Order> orderList = orderService.findOrderList(pager);
    		for (int j = 0; j < orderList.size(); j++) {
    			Order order1 = orderList.get(j);
    			// 发送时间时间戳
    			long addLong =  order1.getShippingTime();
    			// 当前时间时间戳
    	    	long nowLong = System.currentTimeMillis();
    	    	// 24小时的时间戳
    	    	long chaTime = 1000 * 60 * 60 * 24 * 14;
    	    	// 如果当前时间－添加时间大于14天自动完成
    	    	if((nowLong - addLong) > chaTime){
    	    		orderService.updateFinishOrder(order1.getOrderSn());
    	    	}
    	    	
    		}
		}
    }
    
    /**
     * 每晚1点查询是否结算
     */
    //@Scheduled(cron="0 */1 * * * ?") //一分钟一次
    @Scheduled(cron="0 0 1 * * ? ")
    public void addBill(){
    	//查询设置中结算周期开始时间和结算周期天数
    	String startTime = settingService.findByNameAndCode("bill","startTime");
    	String cycle = settingService.findByNameAndCode("bill", "cycle");
    	if(StringUtils.isNoneBlank(cycle) && startTime!=null){
    		//若没有设置开始时间,默认设置为当月第一天
    		if(startTime==""){
    			startTime = DateUtils.firstDayOfCurrentMonth("yyyy-MM-dd");
    		}
        	Date date = DateUtils.parse(startTime,DateUtils.DEFAULT_FORMAT_YYYY_MM_DD);
    		//结算周期结束时间
        	String obEndTime = DateUtils.getDateAddDays(date,Integer.valueOf(cycle)-1,DateUtils.DEFAULT_FORMAT_YYYY_MM_DD);
        	long longEndTime = DateUtils.strToLong(obEndTime+" 00:00:00");
        	//判断当前时间是否大于开始时间(大于达到结算条件才能结算)
    		if(System.currentTimeMillis()>longEndTime){
            	orderBillService.addBill(startTime, obEndTime);
            	String newStartTime = DateUtils.getDateAddDays(date,Integer.valueOf(cycle),DateUtils.DEFAULT_FORMAT_YYYY_MM_DD);
            	//结算完成后修改设置中开始时间,设置上一周期结束时间
            	settingService.updateByNameAndcode("bill","startTime",newStartTime);
        	}
    	}
    }
    
}
