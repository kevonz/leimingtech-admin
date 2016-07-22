package com.leimingtech.admin.module.stat.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.collect.Lists;
import com.leimingtech.core.common.DateUtils;
import com.leimingtech.core.entity.Order;

/**
 * Created by rabook on 2014/11/15.
 */

@Slf4j
@Controller
@RequestMapping("/stat/storeRank")
public class StatStoreRankAction {


    /**
     * 首页
     * @param model
     * @return
     */
    @RequestMapping("/index")
    public String index(Model model){

        String date =  DateUtils.getDate24();
        model.addAttribute("date",date);//结果集
        return "stat/store/store_rank_index";
    }

    /**
     * 列表页
     * @param model
     * @return
     */
    @RequestMapping("/topList")
    public String topList(Model model,
                          @RequestParam(required=false, value="monthendtime",defaultValue="")String endtime,
                          @RequestParam(required=false, value="statType",defaultValue="")Integer statType,
                           @ModelAttribute Order order){

        /***按日查询的时间**/
        if(StringUtils.isBlank(endtime)){
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM");
            endtime = df.format(new Date())+"-";
        }else{
            endtime = endtime.replaceAll("年", "-").replaceAll("月", "-");
        }
        String starttime = endtime + "01";//从1号开始

        //order.setStartTime(starttime);
        //order.setEndTime(endtime+DataUtil.lastDayOfMonth(endtime+"-01"));
        List<Order> list = Lists.newArrayList();
        if(statType == null || statType == 0){
             //list = orderService.findStatMonthTop15(order);
        }else{
             //list = orderService.findStatAmount(order);
        }
        model.addAttribute("list",list);
        model.addAttribute("datas",StringUtils.join(getDatas(list),","));
       // model.addAttribute("totalOrder",orderService.findStatTotalOrder(order));
       // model.addAttribute("totalAmount",orderService.findStatTotalAmount(order));
        return "stat/store/store_rank_list";
    }

    private List<String> getDatas(List<Order> orders){

        List<String> list = Lists.newArrayList();
        if(orders.size() < 15){
            for(Order order : orders){
                //list.add(order.getAllNum()+"");
            }
            for(int i = 0 ; i < 15-orders.size() ; i++){
                list.add("0");
            }
        }else{
            for(Order order : orders){
                //list.add(order.getAllNum()+"");
            }
        }
        return list;
    }
}
