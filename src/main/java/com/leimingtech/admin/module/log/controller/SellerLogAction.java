package com.leimingtech.admin.module.log.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Maps;
import com.leimingtech.admin.utils.ImportExcelUtils;
import com.leimingtech.core.common.CommonConstants;
import com.leimingtech.core.common.Constants;
import com.leimingtech.core.common.DateUtils;
import com.leimingtech.core.entity.base.SellerLog;
import com.leimingtech.service.module.log.service.SellerLogService;
import com.leimingtech.service.utils.page.Pager;

@Controller
@RequestMapping("/sellerlog")
@Slf4j
public class SellerLogAction {

	@Autowired
	private SellerLogService sellerLogService;

	@RequestMapping("/list")
	public String list(
			Model model,
			@RequestParam(required = false, value = "pageNo", defaultValue = "") String pageNo,
			@RequestParam(required = false, value = "sellerName", defaultValue = "") String sellerName,
			@RequestParam(required = false, value = "startTime", defaultValue = "") String startTime,
			@RequestParam(required = false, value = "endTime", defaultValue = "") String endTime) {
		Pager pager = new Pager();
		SellerLog sellerLog = new SellerLog();
		if (StringUtils.isNotEmpty(sellerName)) {
			sellerLog.setLogSellerName(sellerName);
		}
		if (StringUtils.isNotEmpty(startTime)) {
			sellerLog.setStartTime(DateUtils.strToLong(startTime));
		}
		if (StringUtils.isNotEmpty(endTime)) {
			sellerLog.setEndTime(DateUtils.strToLong(endTime));
		}
		if (StringUtils.isNotEmpty(pageNo)) {
			pager.setPageNo(Integer.parseInt(pageNo));
		}
		// 将sellerlog对象放入page中
		pager.setCondition(sellerLog);
		// 获取总数,放入page
		// 根据条件获取到sellerlog放入page类中
		pager.setResult(sellerLogService.selectSellerLogByPager(pager));
		model.addAttribute("pager", pager);
		model.addAttribute("sellerName", sellerName);
		model.addAttribute("startTime", startTime);
		model.addAttribute("endTime", endTime);
		return "/sellerlog/list";
	}

	@RequestMapping("/delete")
	public String delete(Model model, @RequestParam("logId") long[] logIds,
			HttpServletRequest request) {
		for(long logId : logIds){
			sellerLogService.deleteSellerLog(Integer.parseInt(logId+""));
		}
		String referer = request.getHeader("Referer");
		log.debug(referer);
		model.addAttribute("referer", referer);
		model.addAttribute("msg", "删除成功");
		return Constants.MSG_URL;
	}	
	//导出excel
	@RequestMapping("/exportExcel")  
    public void exportExcel(@RequestParam(required = false, value = "pageNo", defaultValue = "") String pageNo,
			@RequestParam(required = false, value = "sellerName", defaultValue = "") String sellerName,
			@RequestParam(required = false, value = "startTime", defaultValue = "") String startTime,
			@RequestParam(required = false, value = "endTime", defaultValue = "") String endTime,
			HttpServletResponse response,
			HttpServletRequest request) throws Exception{  
		String fileName = System.currentTimeMillis() +"";
        response.setCharacterEncoding("utf-8");  
        response.setContentType("multipart/form-data");    
        response.setHeader("Content-Disposition", "attachment;fileName="+fileName+".xls");  
        Pager pager = new Pager();
		SellerLog sellerLog = new SellerLog();
		if (StringUtils.isNotEmpty(sellerName)) {
			sellerLog.setLogSellerName(sellerName);
		}
		if (StringUtils.isNotEmpty(startTime)) {
			sellerLog.setStartTime(DateUtils.strToLong(startTime));
		}
		if (StringUtils.isNotEmpty(endTime)) {
			sellerLog.setEndTime(DateUtils.strToLong(endTime));
		}
		if (StringUtils.isNotEmpty(pageNo)) {
			pager.setPageNo(Integer.parseInt(pageNo));
		}
		// 将sellerlog对象放入page中
		pager.setCondition(sellerLog);
		// 获取总数,放入page
		// 获取list,得到
		//List<SellerLog> logs = sellerLogService.selectSellerLogByPager(pager);	
		String realPath = CommonConstants.FILE_BASEPATH + Constants.SELLERLOG_PATH;
		String url = realPath + "/" + fileName;
		log.debug(url);
		//ExportExcelUtils.export(logs, url);
        try {  
            File file=new File(url + ".xls");  
            response.setHeader("Content-type", "application/vnd.ms-excel"); 
            InputStream inputStream=new FileInputStream(file);  
            OutputStream os=response.getOutputStream();  
            byte[] b=new byte[1024];  
            int length;  
            while((length=inputStream.read(b))>0){  
                os.write(b,0,length);  
            }  
            inputStream.close();  
        } catch (FileNotFoundException e) {  
            log.error("",e);
        } catch (IOException e) {  
            log.error("",e);
        }
    }
	
	//上传excel,导入到数据库
//	@RequestMapping("/importExcel")  
//    public void importDate(@RequestParam("file") MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws Exception{  
//		
//        System.out.println(file.getOriginalFilename());
//    }
	
	@RequestMapping("/importExcel")
	public String addUser(@RequestParam("file") MultipartFile[] myfiles, HttpServletRequest request, HttpServletResponse response){  
		
		//可以在上传文件的同时接收其它参数
        log.debug("收到用户[]的文件上传请求");
        Map<String, String> map = Maps.newHashMap();
        
        //如果用的是Tomcat服务器，则文件会上传到\\%TOMCAT_HOME%\\webapps\\YourWebProject\\upload\\文件夹中
        //这里实现文件上传操作用的是commons.io.FileUtils类,它会自动判断/upload是否存在,不存在会自动创建
        String realPath = request.getSession().getServletContext().getRealPath(Constants.SELLERLOG_PATH);
        //设置响应给前台内容的数据格式
        response.setContentType("text/plain; charset=UTF-8");
        //设置响应给前台内容的PrintWriter对象
        //上传文件的原名(即上传前的文件名字)
        String originalFilename = null;
        //如果只是上传一个文件,则只需要MultipartFile类型接收文件即可,而且无需显式指定@RequestParam注解
        //如果想上传多个文件,那么这里就要用MultipartFile[]类型来接收文件,并且要指定@RequestParam注解
        //上传多个文件时,前台表单中的所有<input type="file"/>的name都应该是myfiles,否则参数里的myfiles无法获取到所有上传的文件
       
        log.debug(myfiles.length+"");
        for(MultipartFile myfile : myfiles){
            if(myfile.isEmpty()){
                map.put("result", "请选择文件后上传");                
            }else{
                originalFilename = String.valueOf(new DateTime().getMillis())+ myfile.getOriginalFilename().substring( myfile.getOriginalFilename().indexOf("."));
                log.debug("文件原名: " + myfile.getOriginalFilename());
                log.debug("文件名称: " + myfile.getName());
                log.debug("文件长度: " + myfile.getSize());
                log.debug("文件类型: " + myfile.getContentType());
                log.debug("文件个数: " + myfiles.length);
                log.debug("realpath: " + realPath);
                log.debug("originalFilename" + originalFilename);
                log.debug("========================================");
                File file = null;
                try {
                    //这里不必处理IO流关闭的问题,因为FileUtils.copyInputStreamToFile()方法内部会自动把用到的IO流关掉
                    //此处也可以使用Spring提供的MultipartFile.transferTo(File dest)方法实现文件的上传
                    FileUtils.copyInputStreamToFile(myfile.getInputStream(), new File(realPath, originalFilename));
                    file = new File(realPath +"\\" +originalFilename);
                    InputStream in = new FileInputStream(file);
                    List<SellerLog> list = (List<SellerLog>) ImportExcelUtils.readExcelTitle(in, new SellerLog());
                    if(list != null){
                    	for(int i = 0; i < list.size(); i++){
                    		//sellerLogService.saveSellerLog(list.get(i));
                    	}                    	
                    }                    
                } catch (Exception e) {
                    log.error("文件[" + originalFilename + "]上传失败,堆栈轨迹如下",e);
                    map.put("result", "文件上传失败，请重试！！");
                    if(file.exists()){
                    	file.delete();
                    }
                }
            }
    }
        return "redirect:/sellerlog/list";
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
