/**
 * 
 */
package com.leimingtech.admin.module.adv.controller;

import java.io.IOException;
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
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Maps;
import com.leimingtech.core.common.CommonConstants;
import com.leimingtech.core.common.Constants;
import com.leimingtech.core.common.DateUtils;
import com.leimingtech.core.entity.base.Adv;
import com.leimingtech.core.entity.base.AdvPosition;
import com.leimingtech.core.jackson.JsonUtils;
import com.leimingtech.service.module.adv.service.AdvPositionService;
import com.leimingtech.service.module.adv.service.AdvService;
import com.leimingtech.service.utils.page.Pager;

/**
 * <p>Title: AdvAction.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2014-2018</p>
 * <p>Company: leimingtech.com</p>
 * @author linjm
 * @date 2015年7月7日
 * @version 1.0
 */
@Controller
@Slf4j
@RequestMapping("/adv")
public class AdvAction {
	
	@Resource
	private AdvService advService;
	
	@Resource
	private AdvPositionService advPositionService;
	
	@RequiresPermissions("sys:adv:view")
	@RequestMapping("/list")
	public String list(Model model,@RequestParam(required=false , value="pageNo" ,defaultValue="")String pageNo,
			@RequestParam(required=false ,value="apId" ,defaultValue="")String apId,
			@RequestParam(required=false ,value="advTitle" ,defaultValue="")String advTitle){
		Adv adv = new Adv();
		Pager pager = new Pager();
		if(StringUtils.isNotBlank(pageNo)){
			pager.setPageNo(Integer.valueOf(pageNo));
		}
		if(StringUtils.isNotEmpty(apId)){
			adv.setApId(Integer.valueOf(apId));
		}
		if(StringUtils.isNotEmpty(advTitle)){
			adv.setAdvTitle(advTitle);
		}
		pager.setCondition(adv);
		
		List<Adv> results = advService.findAdvPagerList(pager);
		pager.setResult(results);
		model.addAttribute("pager", pager);//总数
		return "/adv/list";
	}
	
	@RequiresPermissions("sys:adv:edit")
	@RequestMapping("/forward")
	public String add(Model model ,@RequestParam(required=false , value="advId" ,defaultValue="")int advId
			,@RequestParam(required=false , value="apId" ,defaultValue="0")int apId){
		if(advId==0){
			List<AdvPosition> aplist = advPositionService.findAllAdvPosition(new AdvPosition());
			model.addAttribute("aplist", aplist);
			return "/adv/add";
		}else{
			AdvPosition advPosition = advPositionService.findAdvPositionById(apId);
			Adv adv = advService.findAdvById(advId);
			model.addAttribute("advPosition", advPosition);
			model.addAttribute("adv", adv);	
			return "/adv/edit";
		}
	}
	
	@RequiresPermissions("sys:adv:edit")
	@RequestMapping("/saveOrUpdate")
	public String saveOrUpdate(@ModelAttribute Adv adv,Model model,@RequestParam(required=false, value="startTime", defaultValue="") String startTime,
			@RequestParam(required=false, value="endTime", defaultValue="") String endTime,
			@RequestParam(required=false, value="advId", defaultValue="") String advId,
			HttpServletRequest request, HttpServletResponse response){
		if(StringUtils.isNotEmpty(startTime)){
			adv.setStartDate(DateUtils.strToLong(startTime));
		}
		if(StringUtils.isNotEmpty(endTime)){
			adv.setEndDate(DateUtils.strToLong(endTime));
		}
		if(adv!=null && StringUtils.isNotEmpty(advId)){
			advService.update(adv);
			model.addAttribute("msg", "修改成功");
		}else{
			advService.save(adv);
			model.addAttribute("msg", "保存成功");
		}
		model.addAttribute("referer", CommonConstants.ADMIN_SERVER + "/adv/list");
		return Constants.MSG_URL;
	}
	
	@RequiresPermissions("sys:adv:edit")
	@RequestMapping("/delete")
	public String delete(Model model,@RequestParam(required=false , value="advId",defaultValue="0")int advId){
		try {
			if(advId != 0){
				advService.delete(advId);
				model.addAttribute("msg", "删除成功");
			}
			model.addAttribute("referer", CommonConstants.ADMIN_SERVER + "/adv/list");
			return Constants.MSG_URL;
		} catch (Exception e) {
			log.error("删除失败",e);
			throw new RuntimeException();
		}
	}
	
	@RequestMapping("/uploadImage")
	public @ResponseBody String uploadImage(@RequestParam MultipartFile[] myfiles,HttpServletRequest request, HttpServletResponse response)throws Exception{
		//可以在上传文件的同时接收其它参数
        Map<String,Object> map = Maps.newHashMap();
        try {
        	for(MultipartFile multipartFile : myfiles){
        		MultipartFile[] files = myfiles;
        		//使用公用上传方法上传图片
                map = com.leimingtech.core.common.FileUtils.fileUpload(files,
                        CommonConstants.FILE_BASEPATH, Constants.ADV_UPLOAD_URL, request,"images",1);
                //将图片信息存入表
        	}
        } catch (IOException e) {
            log.error("上传文件失败", e);
        }
        //上传后信息写入json回显
        String json = JsonUtils.toJsonStr(map);
		return json;
	}

}
