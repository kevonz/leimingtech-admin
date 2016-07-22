package com.leimingtech.admin.module.stat.controller;
//package com.leimingtech.admin.module.stat.controller;
//
//import java.util.List;
//import java.util.Map;
//
//import lombok.extern.slf4j.Slf4j;
//
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import com.leimingtech.core.common.DateUtils;
//import com.leimingtech.core.entity.Store;
//import com.leimingtech.service.module.store.service.StoreService;
//import com.google.common.collect.Lists;
//import com.google.common.collect.Maps;
//
///**
// * Created by rabook on 2014/11/15.
// */
//
//@Slf4j
//@Controller
//@RequestMapping("/stat/newStore")
//public class StatNewStoreAction{
//
//    @Autowired
//    private StoreService storeService;
//    /**
//     * 首页
//     * @param model
//     * @return
//     */
//    @RequestMapping("/index")
//    public String index(Model model){
//
//        String date =  DateUtils.getDateStr();
//        model.addAttribute("date",date);//结果集
//        return "stat/store/new_store_index";
//    }
//
//    /**
//     * 月统计
//     */
//    @RequestMapping("/list")
//    public String monthList(Model model,
//                            @RequestParam(required=false, value="div",defaultValue="")String div,
//                            @RequestParam(required=false, value="monthendtime",defaultValue="")String endtime){
//
//        /***按日查询的时间**/
//        if(StringUtils.isBlank(endtime)){
//            endtime = DateUtils.getDateStr("yyyyMM");
//        }else{
//            endtime = endtime.replaceAll("年", "").replaceAll("月", "");
//        }
//        String starttime = DateUtils.getMonth(endtime,"yyyyMM","yyyyMM",-1);
//        String[] datas ={starttime,endtime};
//        Store store = new Store();
////        store.setStartTime(starttime+"01");
////        store.setEndTime(DateUtils.lastDayOfMonth(endtime,"yyyyMM","yyyyMMdd"));
//        List<Store> list = storeService.findStatMonthList(store);
//
//        Map<String, List<String>> map = getMapMonth(datas, list);
//
//        List<String> lastdays = map.get(starttime);
//        List<String> todays = map.get(endtime.replaceAll("-",""));
//
//        String yestoday = StringUtils.join(lastdays,",");
//        String today = StringUtils.join(todays, ",");
//
//        model.addAttribute("today", today);//当天日期值
//        model.addAttribute("yestoday", yestoday);//昨天日期值
//        List<Store> stores = getMembersMonth(lastdays, todays);//处理得到的list值
//        model.addAttribute("datas", stores);//list 结果集回显到页面下方的 列表
//
//        return "stat/store/new_store_list";
//    }
//
//    /**
//     * 拼接月31天展示数据 返回map
//     * @Title: getMap
//     * @Description: TODO(这里用一句话描述这个方法的作用)
//     * @param @param datas
//     * @param @param results
//     * @param @return    设定文件
//     * @return Map<String,List<String>>    返回类型
//     * @throws
//     */
//    private Map<String, List<String>> getMapMonth(String[] datas,List<Store> results){
//        Map<String, List<String>> map = Maps.newHashMap();
//        for(String time :datas){
//            List<String> list = Lists.newArrayList();
//            for(int i=1; i<=31; i++){
//                String getHH = getHH(time,i);
//                String val = "0";
//                for(int j = 0 ; j< results.size() ; j++){
//                    Store store = results.get(j);
//                    if(store.getStoreTime().equals(getHH)){
//                        //val = store.getCount();
//                        results.remove(j);
//                        break;
//                    }
//                }
//               // list.add(val);
//            }
//            map.put(time, list);
//        }
//        return map;
//    }
//
//    private String getHH(String date,int val){
//        String newval="";
//        if(val <10 ){
//            newval = date+"0"+String.valueOf(val);
//        }else{
//            newval = date+String.valueOf(val);
//        }
//        return newval;
//    }
//
//    /**
//     * 拼接31天的列表数据
//     * @Title: getMembers
//     * @Description: TODO(这里用一句话描述这个方法的作用)
//     * @param @param lastdays
//     * @param @param todays
//     * @param @return    设定文件
//     * @return List<Member>    返回类型
//     * @throws
//     */
//    private List<Store> getMembersMonth(List<String> lastdays,List<String> todays){
//        List<Store> stores = Lists.newArrayList();
//        for(int i=0; i<31; i++){
//            Store store = new Store();
//         /* store.setDay(String.valueOf(i+1));
//            store.setYesCount(lastdays.get(i));
//            store.setTodayCount(todays.get(i));*/
//            stores.add(store);
//        }
//        return stores;
//    }
//}
