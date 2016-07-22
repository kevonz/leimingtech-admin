package com.leimingtech.admin.module.doc.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.leimingtech.core.common.CommonConstants;
import com.leimingtech.core.common.Constants;
import com.leimingtech.core.entity.base.Admin;
import com.leimingtech.core.entity.base.DocEnProEntity;
import com.leimingtech.service.module.doc.service.DocEntityProService;
import com.leimingtech.service.utils.page.Pager;
import com.leimingtech.service.utils.sessionkey.CacheUser;
import com.leimingtech.service.utils.sessionkey.CacheUtil;

/**
 *    
 * 项目名称：leimingtech-admin   
 * 类名称：DocEntityController   
 * 类描述：文档实体属性管理
 * 创建人：lkang   
 * 创建时间：2015年5月04日 02:00:00   
 */
@Controller
@RequestMapping("/doc/api/docentitypro")
@Slf4j
public class DocEntityProController {
	@Autowired
	private DocEntityProService docEntityProService;
	
	/**
	 * 获取属性列表
	 * @param model
	 * @param pageNoStr
	 * @param name
	 * @return
	 */
	@RequestMapping("/list")
	public String list(
			Model model,
			@RequestParam(value = "pageNo", required=false, defaultValue = "1") String pageNoStr,
			@RequestParam(value = "name", required=false, defaultValue = "") String name,
			@RequestParam(value = "entityid", required=false, defaultValue = "0") int entityid
			) {
		try {
			
			Pager pager = new Pager();
			pager.setPageSize(6);
			if (null != pageNoStr && !pageNoStr.equals("")) {
				pageNoStr = pageNoStr.replace("," , "");
				pager.setPageNo(Integer.parseInt(pageNoStr));
			}
			
			DocEnProEntity doc = new DocEnProEntity();
			doc.setEntityid(entityid);
			if(StringUtils.isNotEmpty(name)){
				doc.setName(name);
			}
			
			pager.setCondition(doc);
			
//			int total = docEntityProService.getProTotal(pager);
			List<DocEnProEntity> resultList = docEntityProService.getProList(pager);
			
//			pager.setTotalRows(total);
	        pager.setResult(resultList);

			model.addAttribute("docentity", resultList);
			model.addAttribute("pager", pager);
			model.addAttribute("name", name);
			model.addAttribute("entityid", entityid);
			
			return "/doc/api/docentityprolist";
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("获取属性列表失败!");
		}
	}
	
	/**
	 * 新增属性
	 * @return
	 */
	@RequestMapping("/add")
	public String add(
			Model model,
			@RequestParam(value = "entityid", required=false, defaultValue = "0") int entityid
			){
		model.addAttribute("entityid", entityid);
		return "/doc/api/docentityproadd";
	}
	
	/**
	 * 修改或新增属性
	 * @return
	 */
	@RequestMapping("/saveOrUpdate")
	public String saveOrUpdate(
			@ModelAttribute DocEnProEntity docEn,
			HttpServletRequest request,
			Model model
			){
        model.addAttribute("referer", CommonConstants.ADMIN_SERVER + "/doc/api/docentitypro/list?entityid=" + docEn.getEntityid());
        CacheUser user = CacheUtil.getCacheUser();
		Admin admin = user.getAdmin();
		if(docEn.getId() == null){
			docEn.setCreatedby(admin.getAdminName());
			docEntityProService.save(docEn);
			model.addAttribute("msg", "新增成功");
		}else{
			DocEnProEntity findDoc = docEntityProService.getProById(docEn.getId());
			if(null == findDoc){
				 log.warn("未找到，Id=" + docEn.getId());
			} else {
				docEn.setUpdateby(admin.getAdminName());
				docEntityProService.update(docEn);
				model.addAttribute("msg", "修改成功");
			}
		}
		return Constants.MSG_URL;
	}
	
	/**
	 * 根据id获取属性
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/findById")
	public String findByid(Model model,
			@RequestParam(required = false, value = "id", defaultValue = "") int id){
		DocEnProEntity doc = docEntityProService.getProById(id);
		model.addAttribute("doc", doc);
		return "/doc/api/docentityproedit";
	}
	
	/**
	 * 删除实体
	 * @param ids
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/delDoc")
	public String deleteDoc(
			@RequestParam(value = "ids") String ids,
			Model model, HttpServletRequest request
			){

		String referer = request.getHeader("Referer");
		model.addAttribute("referer", referer);
		if (StringUtils.isBlank(ids)) {
			model.addAttribute("result", "ID为空");
			model.addAttribute("msg", "删除失败，ID为空");
		}else{
			String[] idArray = StringUtils.split(ids, ",");
			for (String idStr : idArray) {
				docEntityProService.delete(Integer.parseInt(idStr));
			}
			model.addAttribute("msg", "删除成功");
		}
		return Constants.MSG_URL;
	}
}
