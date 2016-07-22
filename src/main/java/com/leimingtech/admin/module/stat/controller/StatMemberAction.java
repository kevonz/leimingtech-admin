package com.leimingtech.admin.module.stat.controller;

import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.leimingtech.core.common.DateUtils;
import com.leimingtech.core.entity.base.Member;
import com.leimingtech.service.module.stat.service.StatMemberService;

/**
 * 
 *    
 * 项目名称：leimingtech-admin   
 * 类名称：MemberAction   
 * 类描述：   
 * 创建人：liuhao   
 * 创建时间：2014年11月7日 下午11:54:09   
 * 修改人：liuhao   
 * 修改时间：2014年11月7日 下午11:54:09   
 * 修改备注：   
 * @version    
 *
 */
@Controller
@RequestMapping("/stat/member")
@Slf4j
public class StatMemberAction {
	
	String message = "success";
	
	@Autowired
	private StatMemberService statmemberService;

	/**
	 * 导航至主操作页面
	 * @Title: index 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 * @param @return    设定文件 
	 * @return String    返回类型 
	 * @throws
	 */
	@RequestMapping("/index")
	public String index(Model model){
		try{
			String date =  DateUtils.getDate24();
			model.addAttribute("date",date);//结果集
			return "/stat/member_index";
		}catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("导航失败!");
		}
	}
	
	/**
	 * 按日查询统计会员图表
	 * @Title: list 
	 * @Description: TODO(加载数据页面) 
	 * @param @param model
	 * @param @param div
	 * @param @param pageNoStr
	 * @param @param adminName
	 * @param @param starttime
	 * @param @param endtime
	 * @param @return    设定文件 
	 * @return String    返回类型 
	 * @throws
	 */
	@RequestMapping(value = "/list")
	public String list(Model model,
			@RequestParam(required=false, value="div",defaultValue="")String div,
			@RequestParam(required=false, value="endtime",defaultValue="")String endtime){
		
		/***按日查询的时间**/
		if(StringUtils.isBlank(endtime)){
			endtime = DateUtils.getDateString();
		}else{
			endtime = endtime.replaceAll("-", "");
		}
		String starttime = DateUtils.getNextDay_YYYYMMDD(endtime, -1);
		String data = starttime+","+endtime;
		String datas[] = data.split(","); 
		
		Member member = new Member();
		member.setStarttime(starttime);
		member.setEndtime(endtime);
		List<Member> results = statmemberService.queryStatMemberList(member);//结果集
		
		Map<String, List<String>> map = getMap(datas, results);
		
		List<String> lastdays = map.get(starttime);
		List<String> todays = map.get(endtime);
		
		String yestoday = getVals(lastdays);
		String today = getVals(todays);
		
		model.addAttribute("today", today);//当天日期值
		model.addAttribute("yestoday", yestoday);//昨天日期值
		List<Member> members = getMembers(lastdays, todays);//处理得到的list值
		model.addAttribute("datas", members);//list 结果集回显到页面下方的 列表
		
		//转发请求到FTL页面
		return "/stat/member_day_list";
		
	}
	
	
	
	
	/**
	 * 按月统计会员图表数据
	 * @Title: list 
	 * @Description: TODO(加载数据页面) 
	 * @param @param model
	 * @param @param div
	 * @param @param pageNoStr
	 * @param @param adminName
	 * @param @param starttime
	 * @param @param endtime
	 * @param @return    设定文件 
	 * @return String    返回类型 
	 * @throws
	 */
	@RequestMapping(value = "/monthlist")
	public String monthlist(Model model,
			@RequestParam(required=false, value="div",defaultValue="")String div,
			@RequestParam(required=false, value="monthendtime",defaultValue="")String endtime){
		
		/***按日查询的时间**/
		if(StringUtils.isBlank(endtime)){
			endtime = DateUtils.getDateYYYYMM();
		}else{
			endtime = endtime.replaceAll("年", "").replaceAll("月", "");
		}
		String starttime = DateUtils.getMonth(endtime, -1);
		String data = starttime+","+endtime;
		String datas[] = data.split(","); 
		
		Member member = new Member();
		member.setStarttime(starttime+"01");//默认01开始
		member.setEndtime(endtime+DateUtils.lastDayOfMonth(endtime));//默认月结束
		List<Member> results = statmemberService.queryStatMemberMonthList(member);//结果集
		
		Map<String, List<String>> map = getMapMonth(datas, results);
		
		List<String> lastdays = map.get(starttime);
		List<String> todays = map.get(endtime);
		
		String yestoday = getVals(lastdays);
		String today = getVals(todays);
		
		model.addAttribute("today", today);//当天日期值
		model.addAttribute("yestoday", yestoday);//昨天日期值
		List<Member> members = getMembersMonth(lastdays, todays);//处理得到的list值
		model.addAttribute("datas", members);//list 结果集回显到页面下方的 列表
		
		//转发请求到FTL页面
		return "/stat/member_month_list";
		
	}
	/**
	 * 统计map数据 实例
	 * @Title: list 
	 * @Description: TODO(加载数据页面) 
	 * @param @param model
	 * @param @param div
	 * @param @param pageNoStr
	 * @param @param adminName
	 * @param @param starttime
	 * @param @param endtime
	 * @param @return    设定文件 
	 * @return String    返回类型 
	 * @throws
	 */
	@RequestMapping(value = "/maplist")
	public String maplist(Model model,
			@RequestParam(required=false, value="div",defaultValue="")String div,
			@RequestParam(required=false, value="monthendtime",defaultValue="")String endtime){
		
		
		//转发请求到FTL页面
		return "/stat/member_map_list";
		
	}
	
	
	
	
	
	
	/**
	 * 获取日期加小时
	 * @Title: getHH 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 * @param @param date
	 * @param @param val
	 * @param @return    设定文件 
	 * @return String    返回类型 
	 */
	private String getHH(String date,int val){
		String newval="";
		if(val <10 ){
			newval = date+"0"+String.valueOf(val);
		}else{
			newval = date+String.valueOf(val);
		}
		return newval;
	}
	
	
	/**
	 * 把list 数据 转成字符串格式，方便前台图形统计表展示
	 * @Title: getVals 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 * @param @param lists
	 * @param @return    设定文件 
	 * @return String    返回类型 
	 * @throws
	 */
	private String getVals(List<String> lists){
		String val = "";
		for(String str : lists){
			val+=str+",";
		}
		val = val.substring(0,val.length()-1);
		return val;
	}
	
	
	/**
	 * 拼接24小时下面的列表数据
	 * @Title: getMembers 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 * @param @param lastdays
	 * @param @param todays
	 * @param @return    设定文件 
	 * @return List<Member>    返回类型 
	 * @throws
	 */
	private List<Member> getMembers(List<String> lastdays,List<String> todays){
		List<Member> members = Lists.newArrayList();
		for(int i=0; i<24; i++){
			Member mem = new Member();
			mem.setHour(String.valueOf(i));
			mem.setYesCount(lastdays.get(i));
			mem.setTodayCount(todays.get(i));
			members.add(mem);
		}
		return members;
	}
	
	/**
	 * 拼接24小时展示数据 返回map
	 * @Title: getMap 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 * @param @param datas
	 * @param @param results
	 * @param @return    设定文件 
	 * @return Map<String,List<String>>    返回类型 
	 * @throws
	 */
	private Map<String, List<String>> getMap(String[] datas,List<Member> results){
		Map<String, List<String>> map = Maps.newHashMap();
		for(String time :datas){
			List<String> list = Lists.newArrayList();
			for(int i=0; i<24; i++){
				String getHH = getHH(time,i);
				String val = "0";
				for(int j = 0 ; j< results.size() ; j++){
					Member member = results.get(j);
					if(member.getCreateTime().equals(getHH)){
						val = member.getCount();
						results.remove(j);
						break;
					}
				}
				list.add(val);
			}
			map.put(time, list);
		}
		return map;
	}
	
	
	/**
	 * 拼接月31天展示数据 返回map
	 * @Title: getMap 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 * @param @param datas
	 * @param @param results
	 * @param @return    设定文件 
	 * @return Map<String,List<String>>    返回类型 
	 * @throws
	 */
	private Map<String, List<String>> getMapMonth(String[] datas,List<Member> results){
		Map<String, List<String>> map = Maps.newHashMap();
		for(String time :datas){
			List<String> list = Lists.newArrayList();
			for(int i=1; i<=31; i++){
				String getHH = getHH(time,i);
				String val = "0";
				for(int j = 0 ; j< results.size() ; j++){
					Member member = results.get(j);
					if(member.getCreateTime().equals(getHH)){
						val = member.getCount();
						results.remove(j);
						break;
					}
				}
				list.add(val);
			}
			map.put(time, list);
		}
		return map;
	}
	
	/**
	 * 拼接31天的列表数据
	 * @Title: getMembers 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 * @param @param lastdays
	 * @param @param todays
	 * @param @return    设定文件 
	 * @return List<Member>    返回类型 
	 * @throws
	 */
	private List<Member> getMembersMonth(List<String> lastdays,List<String> todays){
		List<Member> members = Lists.newArrayList();
		for(int i=0; i<31; i++){
			Member mem = new Member();
			mem.setHour(String.valueOf(i+1));
			mem.setYesCount(lastdays.get(i));
			mem.setTodayCount(todays.get(i));
			mem.setCount(String.valueOf(Long.valueOf(todays.get(i)) - Long.valueOf(lastdays.get(i))));
			members.add(mem);
		}
		return members;
	}
}