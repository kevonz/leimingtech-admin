package com.leimingtech.admin.module.stat.controller;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.collect.Lists;
import com.leimingtech.core.common.Collections3;
import com.leimingtech.core.common.DateUtils;
import com.leimingtech.core.entity.Order;
import com.leimingtech.core.entity.base.Goods;
import com.leimingtech.core.entity.base.OrderGoods;
import com.leimingtech.core.entity.base.Goods;
import com.leimingtech.service.module.goods.service.GoodsService;
import com.leimingtech.service.utils.page.Pager;

/**
 * Created by rabook on 2014/11/17.
 */

@Slf4j
@Controller
@RequestMapping("/stat/tradeGoods")
public class StatTradeGoodsAction {

    @Autowired
    private GoodsService goodsService;

    /**
     * 首页
     * @param model
     * @return
     */
    @RequestMapping("/index")
    public String index(Model model){

        String date =  DateUtils.getDateStr();
        model.addAttribute("date",date);//结果集
        return "stat/trade/trade_goods_index";
    }

    /**
     * 列表
     * @param model
     * @param div
     * @param endtime
     * @return
     */
    @RequestMapping("/list")
    public String list(Model model,
                       @RequestParam(required=false, value="div",defaultValue="")String div,
                       @RequestParam(required=false, value="rankType") String rankType,
                       @RequestParam(required=false, value="monthendtime",defaultValue="")String endtime){

        //计算时间
        if(StringUtils.isBlank(endtime)){
            endtime = DateUtils.getDateStr("yyyyMM");
        }else{
            endtime = endtime.replaceAll("年", "").replaceAll("月", "");
        }
        String starttime = endtime+"01";
        endtime = DateUtils.lastDayOfMonth(endtime,"yyyyMM","yyyyMMdd");
        //统计上架商品数
        Pager pager = new Pager();
        Goods goods = new Goods();
        goods.setGoodsShow(1);
        pager.setCondition(goods);
        int goodsCount = goodsService.countGoods(goods);
        model.addAttribute("goodsCount",0);
        //统计下单商品数
        OrderGoods orderGoods = new OrderGoods();
        //orderGoods.setStartTime(starttime);
        //orderGoods.setEndTime(endtime);
        //int orderGoodsNum = orderGoodsService.statGoodsNum(orderGoods);
        //model.addAttribute("orderGoodsNum",orderGoodsNum);
        //总下单量
        Order order = new Order();
        //order.setStartTime(starttime);
        //order.setEndTime(endtime);
        //model.addAttribute("totalOrder",orderService.findStatTotalAmount(order));
        //总下单客户数
       // model.addAttribute("totalBuyer",orderService.findStatTotalBuyer(order));
        //合计金额
        //model.addAttribute("totalAmount",orderService.findStatTotalOrder(order));
        //列表
        List<OrderGoods> list = Lists.newArrayList();
        if(StringUtils.isBlank(rankType) || "0".equals(rankType)){
           // list = orderGoodsService.statGoods(orderGoods);
        }else{
            //list = orderGoodsService.statGoodsPrice(orderGoods);
        }
        model.addAttribute("list",list);
        //图表数据
        model.addAttribute("data", Collections3.convertToString(getDatas(list),","));
        return  "stat/trade/trade_goods_list";
    }

    private List<String> getDatas(List<OrderGoods> orders){

        List<String> list = Lists.newArrayList();
        if(orders.size() < 15){
            for(OrderGoods order : orders){
                //list.add(order.getAllNum()+"");
            }
            for(int i = 0 ; i < 15-orders.size() ; i++){
                list.add("0");
            }
        }else{
            for(OrderGoods order : orders){
               // list.add(order.getAllNum()+"");
            }
        }
        return list;
    }
}
