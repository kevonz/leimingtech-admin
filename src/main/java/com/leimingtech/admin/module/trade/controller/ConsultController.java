package com.leimingtech.admin.module.trade.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;
import com.leimingtech.core.common.Constants;
import com.leimingtech.core.entity.base.Consult;
import com.leimingtech.service.module.trade.service.ConsultService;
import com.leimingtech.service.utils.page.Pager;

/**
 * Created by rabook on 2015/3/5.
 */

@Slf4j
@Controller
@RequestMapping("/consult")
public class ConsultController {

    @Resource
    private ConsultService consultService;


    /**
     * 查询列表
     * @return
     */
    @RequestMapping("/index")
    public String list(Model model,
    		@RequestParam(required=false ,value="pageNo" ,defaultValue="1")String pageNo, 
			@RequestParam(required=false ,value="consultReply" ,defaultValue="") String consultReply,
			@RequestParam(required=false ,value="cur" ,defaultValue="") String cur) {
        try {
            Pager pager = new Pager();
            Consult consult = new Consult();
            /**查询条件，放入实体中，**/
            if(StringUtils.isNotBlank(pageNo)){
                pager.setPageNo(Integer.parseInt(pageNo));
            }

            consult.setStoreId(Constants.PLATFORM_STORE_ID);
            if(StringUtils.isNotEmpty(consultReply)){
            	consult.setReplyStatus(Integer.parseInt(consultReply));
            }
            pager.setPageSize(1);
            pager.setCondition(consult);//实体加载在pager中

            List<Consult> results = consultService.findList(pager);// 结果集

            model.addAttribute("datas", results);// 结果集
            model.addAttribute("pageNo", pager.getPageNo());// 当前页
            model.addAttribute("pageSize", pager.getPageSize());// 每页显示条数
            model.addAttribute("recordCount", pager.getTotalRows());// 总数
            model.addAttribute("toUrl","/consult/index");
            model.addAttribute("pager",pager);
            
    		model.addAttribute("consultReply", consultReply);
    		if(StringUtils.isEmpty(cur)) cur="index";
    		model.addAttribute("cur", cur);
    		model.addAttribute("apm", "consult");
            //log.info(JsonUtils.toJsonStr(results));
            return "/platform/customer/cus-consult-index";
        } catch (Exception e) {
            e.printStackTrace();
            log.error("投诉列表加载失败");
            throw new RuntimeException("导航失败!");
        }
    }

    @RequestMapping("/findById")
    public String findById(Model model,@RequestParam int replyId){

        model.addAttribute("consult",consultService.findById(replyId));
        return "/platform/customer/cus-consult-edit";
    }
    

    @RequestMapping("/edit")
    public @ResponseBody Map<String,Object> edit(@ModelAttribute Consult consult){

        Map<String,Object> map = Maps.newHashMap();
        try{
        	consult.setUpdateTime(System.currentTimeMillis());
        	consult.setConsultReplyTime(System.currentTimeMillis());
            consultService.updateReply(consult);
            map.put("success",true);
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.toString());
            map.put("success",false);
            map.put("msg","回复失败");
        }
        return map;
    }

    @RequestMapping("/delete")
    public @ResponseBody Map<String,Object> delete(@RequestParam int replyId){

        Map<String,Object> map = Maps.newHashMap();
        try{
            consultService.delete(replyId);
            map.put("success",true);
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.toString());
            map.put("success",false);
            map.put("msg","删除失败");
        }
        return map;
    }
    
    @RequestMapping("/deleteConsults")
    @ResponseBody
    public Map<String,Object> deleteConsults(@RequestParam String replyIds){
    	Map<String,Object> map = Maps.newHashMap();
        try{
        	String[] ids = replyIds.split(",");
        	for(String id:ids){
        		consultService.delete(Integer.valueOf(id));
        	}
            map.put("success",true);
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.toString());
            map.put("success",false);
            map.put("msg","删除失败");
        }
        return map;
    }
	
	/**
	 * 投诉管理
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping("/complainaccuserlist")
	public ModelAndView complainaccuserlist(){
		try{
			ModelAndView model = new ModelAndView("/platform/customer/complain_accuser_list");
			return model;
		}catch (Exception e){
			e.printStackTrace();
			log.error("投诉管理加载失败！");
			throw new RuntimeException("投诉管理加载失败!");
		}
	}
	
	/**
	 * 被举报禁止
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping("/storeinform")
	public ModelAndView storeinform(){
		try{
			ModelAndView model = new ModelAndView("/platform/customer/store_inform");
			return model;
		}catch (Exception e){
			e.printStackTrace();
			log.error("被举报禁止加载失败！");
			throw new RuntimeException("被举报禁止加载失败!");
		}
	}
}
