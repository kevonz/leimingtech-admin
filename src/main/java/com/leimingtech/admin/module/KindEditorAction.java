package com.leimingtech.admin.module;

import java.io.File;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Maps;
import com.leimingtech.core.common.CommonConstants;
import com.leimingtech.core.common.Constants;
import com.leimingtech.core.common.DateUtils;

/**
 * @author llf
 * @Package com.leimingtech.admin.module
 * @Description:
 * @date 2015/2/2 16:09
 */

@Controller
@Slf4j
@RequestMapping("/kind")
public class KindEditorAction {

    @RequestMapping("/uploadt")
    public @ResponseBody Map<String,Object> uploadImg1(HttpServletResponse response,HttpServletRequest request
            ,@RequestParam("imgFile") MultipartFile imgFile) {

        response.setContentType("text/html; charset=UTF-8");
        // 文件保存目录URL
        String saveUrl = CommonConstants.FILE_BASEPATH + "/" +Constants.GOODS_UPLOAD_URL;
        // 最大文件大小
        long maxSize = Constants.FILE_SIZE_IMG;

        if (imgFile == null) {
            return getError("请选择文件。");
        }

        String imgFileFileName = imgFile.getOriginalFilename();
        String fileExt = imgFileFileName.substring(imgFileFileName.lastIndexOf(".") + 1).toLowerCase();
        Map<String, String> extMap = Maps.newHashMap();
        extMap.put("image", "gif,jpg,jpeg,png,bmp");
        if (extMap.containsKey(fileExt)) {
            return getError("上传文件扩展名[" + fileExt + "]是不允许的扩展名。");
        }
        if (imgFile.getSize() > maxSize) {
            return getError("[ " + imgFileFileName + " ]超过单个文件大小限制，文件大小[ " + imgFile.getSize() + " ]，限制为[ " + maxSize + " ] ");
        }
        String newFileName = System.currentTimeMillis() + "." + fileExt;
        Map<String,Object> map = Maps.newHashMap();
        try {
            FileUtils.copyInputStreamToFile(imgFile.getInputStream(),
                    new File(saveUrl, newFileName));
            map.put("error", 0);
            map.put("url", CommonConstants.IMG_SERVER + "/" +Constants.GOODS_UPLOAD_URL + "/" + newFileName);
            log.debug("kindeditor",map);
            log.debug("上传图片:[" + newFileName + "]" + ">>>[" + newFileName + "]成功");
            return map;
        } catch (Exception e) {
            log.error("图片上传失败:" + e);
            map.put("error", 0);
            map.put("url", saveUrl + newFileName);
            return map;
        }
    }
    /**
     * 
     * @param response
     * @param request
     * @param imgFile
     * @return支持多图单图上传
     */
    @RequestMapping("/upload")
    public @ResponseBody Map<String,Object> uploadImgs(HttpServletResponse response,HttpServletRequest request
            ,@RequestParam("imgFile") MultipartFile[] imgFile) {
       // response.setContentType("text/html; charset=UTF-8");
        response.setContentType("text/plain;charset=UTF-8");
    	Map<String,Object> map = Maps.newHashMap();
        String url="";
        try{
	        for(MultipartFile myFile : imgFile){
	        	Map<String,Object> map1=uploadImg1(response,request,myFile);
				if(map1.get("error").equals(0)){
					String imgPath = map1.get("url").toString();
					System.out.println("url="+map1.get("url").toString());
					url += imgPath + ",";
				}
			}
	        map.put("error", 0);
	        if(url.length()>0){
    			map.put("url", url.substring(0, url.length() - 1)); // 上传成功的所有的图片地址的路径
    		}else{
    			map.put("url", url); // 上传成功的所有的图片地址的路径
    		}
	        log.debug("kindeditor",map);
        }catch(Exception e){
        	e.printStackTrace();
        	 map.put("error", 1);
             map.put("url",url);
        }
        return map;
    }
    private Map<String,Object> getError(String message) {
        Map<String,Object> map = Maps.newHashMap();
        map.put("error", 1);
        map.put("message", message);
        log.debug("kindeditor",map);
        return map;
    }
}
