package com.leimingtech.admin.module.attachpicture.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
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
import com.leimingtech.core.entity.base.AttachPicture;
import com.leimingtech.core.jackson.JsonUtils;
import com.leimingtech.service.module.attachpicture.service.AttachPictureService;
import com.leimingtech.service.utils.page.Pager;

@Controller
@RequestMapping("/attachPicture")
@Slf4j
public class AttachPictureController {
	
	@Resource
	private AttachPictureService attachPictureService; 
	
	/**
	 * 列表页面
	 * @param model
	 * @param pageNo
	 * @param attachPicture
	 * @return
	 */
	@RequestMapping("/list")
	public String list(Model model,
					   @RequestParam(required=false, value="pageNo",defaultValue="")String pageNo,
					   @ModelAttribute AttachPicture attachPicture){
		Pager pager = new Pager();
		pager.setCondition(attachPicture);
		pager.setPageSize(50); //每页显示的数据
		int total  = attachPictureService.findCount(pager);//获取总条数
        if(!StringUtils.isEmpty(pageNo)){
            pager.setPageNo(Integer.parseInt(pageNo));
            pager.setPageSize(50);
        }
        List<AttachPicture> list=attachPictureService.findPageList(pager);
        pager.setResult(list);
        pager.setTotalRows(total);
        model.addAttribute("pager", pager);//总数
		return "attachpicture/list";
	}
	
	/**
	 * 上传图片
	 * @param myfiles
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="/fileUpload")
    public @ResponseBody String fileUpload(@RequestParam MultipartFile[] myfiles,HttpServletRequest request, HttpServletResponse response) throws IOException{
        //可以在上传文件的同时接收其它参数
        Map<String,Object> map = Maps.newHashMap();
        try {
        	for(MultipartFile multipartFile : myfiles){
        		MultipartFile[] files = myfiles;
        		//使用公用上传方法上传图片
                map = com.leimingtech.core.common.FileUtils.fileUpload(files,
                        CommonConstants.FILE_BASEPATH,Constants.IMGALBUM_UPLOAD_URL, request,"images",1);
                //将图片信息存入表
                attachPictureService.saveUpload(map,null);
        	}
            response.setStatus(200);
        } catch (IOException e) {
        	response.setStatus(500);
            log.error("上传文件失败", e);
        }
        //上传后信息写入json回显
        String json = JsonUtils.toJsonStr(map);
        response.setContentType("text/html");
        response.getWriter().write(json);
        
        return null;
    }
	
	/**
     * 多张删除
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    public String  delete(@RequestParam int[] ids,HttpServletRequest request,Model model){
        String referer = request.getHeader("Referer");
        for(int id : ids){
        	//查询实体
        	//AttachPicture attachPicture = attachPictureService.findById(id);
    		//String path = CommonConstants.FILE_BASEPATH + attachPicture.getLocalPath();
    		//表删除
    		attachPictureService.delete(id);
    		//文件删除
    		//File file = new File(path);
    		//org.apache.commons.io.FileUtils.deleteQuietly(file);
        }
        model.addAttribute("referer", referer);
        model.addAttribute("msg", "删除成功");
        return Constants.MSG_URL;
    }
    
    //单个删除
    @RequestMapping("/deleteid")
    public @ResponseBody Map<String,Object> deleteid(@RequestParam int id){
    	Map<String,Object> map = Maps.newHashMap();
    	try{
    		//查询实体
    		//AttachPicture attachPicture = attachPictureService.findById(id);
    		//String path = CommonConstants.FILE_BASEPATH + attachPicture.getLocalPath();
    		//删除表
    		attachPictureService.delete(id);
    		//删除文件
    		//File file = new File(path);
    		//org.apache.commons.io.FileUtils.deleteQuietly(file);
    		
    		map.put("success", "success");
    	}catch (Exception e) {
    		map.put("success", "false");
    		e.printStackTrace();
		}
    	return map;
    }
}
