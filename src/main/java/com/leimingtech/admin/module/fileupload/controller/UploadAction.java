package com.leimingtech.admin.module.fileupload.controller;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Maps;
import com.leimingtech.core.common.CommonConstants;
import com.leimingtech.core.common.Constants;
import com.leimingtech.core.jackson.JsonUtils;
import com.leimingtech.core.platform.info.PlatformInfo;

@Controller
@RequestMapping("/upload")
@Slf4j
public class UploadAction {
	String message = "success";
	
	/**
	 * 这个action只提供平台商品图片的上传!!
	 * @param files
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
    @RequestMapping(value = "/goodsImage")
    public String goodsImage(@RequestParam MultipartFile[] files,
                             HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> map = Maps.newHashMap();
		//店铺id
		String storeId =  PlatformInfo.PLATFORM_STORE_ID+"";
        try {
            map = com.leimingtech.core.common.FileUtils.fileUpload(files,
                    CommonConstants.FILE_BASEPATH, Constants.STORE_IMG_PATH + "/" + storeId, request,"images",1);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("上传文件失败", e.toString());
        }
        map.put("imageServer", CommonConstants.IMG_SERVER);
        String json = JsonUtils.toJsonStr(map);
        response.setContentType("text/html");
        response.getWriter().write(json);

        return null;
    }
	
    @RequestMapping(value = "/specImage")
    public String specImage(@RequestParam MultipartFile[] files,
                             HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> map = Maps.newHashMap();
        try {
            map = com.leimingtech.core.common.FileUtils.fileUpload(files,
                    CommonConstants.FILE_BASEPATH, Constants.SPECIMAGE_PATH, request,"images",1);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("上传文件失败", e.toString());
        }
        map.put("imageServer", CommonConstants.IMG_SERVER);
        String json = JsonUtils.toJsonStr(map);
        response.setContentType("text/html");
        response.getWriter().write(json);

        return null;
    }
    
    @RequestMapping(value = "/gradeImage")
    public void gradeImage(@RequestParam MultipartFile[] files,
    		HttpServletRequest request, HttpServletResponse response) throws IOException {
    	Map<String, Object> map = Maps.newHashMap();
    	try {
    		map = com.leimingtech.core.common.FileUtils.fileUpload(files,
    				CommonConstants.FILE_BASEPATH, Constants.MEMBER_GRADE_PATH, request,"images",1);
    	} catch (IOException e) {
    		e.printStackTrace();
    		log.error("上传文件失败", e.toString());
    	}
    	map.put("imageServer", CommonConstants.IMG_SERVER);
    	String json = JsonUtils.toJsonStr(map);
    	response.setContentType("text/html");
    	response.getWriter().write(json);
    }
    

    @RequestMapping(value = "/fileUpload")
    public String fileUploads(@RequestParam MultipartFile[] files,
                             HttpServletRequest request, HttpServletResponse response) throws IOException {
        //可以在上传文件的同时接收其它参数
        Map<String, Object> map = Maps.newHashMap();
        try {
            map = com.leimingtech.core.common.FileUtils.fileUpload(files,
                    CommonConstants.FILE_BASEPATH, Constants.LOGO_UPLOAD_URL, request,"images",1);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("上传文件失败", e.toString());
        }
        String json = JsonUtils.toJsonStr(map);
        response.setContentType("text/html");
        response.getWriter().write(json);

        return null;
    }
    
    
    @RequestMapping(value="/imageFileUpload")
    public String imageFileUpload(@RequestParam MultipartFile[] myfiles, HttpServletRequest request, HttpServletResponse response){
    	
    	//店铺id
		String storeId =  PlatformInfo.PLATFORM_STORE_ID+"";
    	
		String type = request.getParameter("type");
		//可以在上传文件的同时接收其它参数
        Map<String, String> map = Maps.newHashMap();
        
        response.setContentType("text/plain; charset=UTF-8");
        
        String originalFilename = null;
        
        StringBuffer photoSrc = new StringBuffer();//StringBuffer用来存放上传文件的所有地址
        StringBuffer photoNewName = new StringBuffer();//用来存放图片的新名字
        StringBuffer oldName = new StringBuffer();//原来的名字
        for(MultipartFile myfile : myfiles){
            if(myfile.isEmpty()){
                map.put("result", "请选择文件后上传");
                map.put(message, "false");
            }else{
                originalFilename = String.valueOf(new DateTime().getMillis())+ myfile.getOriginalFilename().substring( myfile.getOriginalFilename().indexOf("."));
               try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					log.error("",e1);
				}                
                try {
            		String realPath = CommonConstants.FILE_BASEPATH + Constants.STORE_IMG_PATH + "/" + storeId;
            		//这里不必处理IO流关闭的问题,因为FileUtils.copyInputStreamToFile()方法内部会自动把用到的IO流关掉
            		//此处也可以使用Spring提供的MultipartFile.transferTo(File dest)方法实现文件的上传
            		FileUtils.copyInputStreamToFile(myfile.getInputStream(), new File(realPath, originalFilename));
            		//上传成功的时候将图片的地址给已经准备好的Stringbuffer
            		photoSrc.append(CommonConstants.FILE_BASEPATH + Constants.STORE_IMG_PATH+ "/" + storeId+"/" + originalFilename + ",");
            		//上传成功的时候将图片的新名字给StringBuffer
            		photoNewName.append(originalFilename +  ",");
                } catch (IOException e) {
                	if("attach"==type){
                		log.error("文件[" + myfile.getOriginalFilename() + "]上传失败,堆栈轨迹如下");
                	}else{
                		log.error("文件[" + originalFilename + "]上传失败,堆栈轨迹如下");
                	}
                    map.put("result", "文件上传失败，请重试！！");
                    map.put(message, "false");
                    
                }
            }
        }
        if("attach".equals(type)){
        	 map.put("oldName", oldName.toString());
        }
        map.put("imgPath",Constants.STORE_IMG_PATH+ "/" + storeId);
        map.put("photoNewName", photoNewName.toString());
        map.put("photoSrc", photoSrc.toString());
    	map.put("result", request.getContextPath() + Constants.GOODS_UPLOAD_URL+"/" + originalFilename);
        map.put("filename", originalFilename);
		map.put(message, "true");
		map.put("listimgSize", myfiles.length+"");
		String json = JsonUtils.toJsonStr(map);
        response.setContentType("text/html");
        try {
			response.getWriter().write(json);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error("",e);
		}
        
        return null;
    }
}
