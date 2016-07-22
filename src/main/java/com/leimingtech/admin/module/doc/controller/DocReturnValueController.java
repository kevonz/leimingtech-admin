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
import com.leimingtech.core.entity.base.DocReturnValueEntity;
import com.leimingtech.service.module.doc.service.DocReturnValueService;
import com.leimingtech.service.utils.page.Pager;
import com.leimingtech.service.utils.sessionkey.CacheUser;
import com.leimingtech.service.utils.sessionkey.CacheUtil;

/**
 *    
 * 项目名称：leimingtech-admin   
 * 类名称：DocReturnValueController   
 * 类描述：API文档返回值管理
 * 创建人：lkang   
 * 创建时间：2015年5月08日 10:00:00   
 */
@Controller
@RequestMapping("/doc/api/returnvalue")
@Slf4j
public class DocReturnValueController {
	@Autowired
	private DocReturnValueService docReturnValueServiceImpl;
	
	
	/**
	 * 返回值列表页
	 * @param model
	 * @param pageNoStr
	 * @param typeid
	 * @param pid
	 * @param name
	 * @return
	 */
	@RequestMapping("/list")
	public String list(
			Model model,
			@RequestParam(value = "pageNo", required=false, defaultValue = "1") String pageNoStr,
			@RequestParam(value = "docid", required=false, defaultValue = "0") int docid,
			@RequestParam(value = "name", required=false, defaultValue = "") String name
			) {
		try {
			Pager pager = new Pager();
			pager.setPageSize(6);
			if (null != pageNoStr && !pageNoStr.equals("")) {
				pageNoStr = pageNoStr.replace("," , "");
				pager.setPageNo(Integer.parseInt(pageNoStr));
			}
			
			DocReturnValueEntity returnvalue = new DocReturnValueEntity();
			returnvalue.setDocid(docid);
			
			// 根据名称查询
			if(StringUtils.isNotEmpty(name)){
				returnvalue.setName(name);
			}
			
			// 查询条件放入分页pager中
			pager.setCondition(returnvalue);
			
			List<DocReturnValueEntity> resultList = docReturnValueServiceImpl.getReturnValueList(pager);
			
	        pager.setResult(resultList);

			model.addAttribute("doc", resultList);
			model.addAttribute("pager", pager);
			model.addAttribute("name", name);
			model.addAttribute("docid", docid);
			return "/doc/api/docreturnvaluelist";
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("获取返回值列表失败!");
		}
	}
	
	/**
	 * 跳转输入参数添加页
	 * @return
	 */
	@RequestMapping("/add")
	public String add(
			Model model,
			@RequestParam(value = "docid", required=false, defaultValue = "0") int docid
			){
		model.addAttribute("docid", docid);
		return "/doc/api/docreturnvalueadd";
	}
	
	/**
	 * 修改或新增参数
	 * @return
	 */
	@RequestMapping("/saveOrUpdate")
	public String saveOrUpdate(
			@ModelAttribute DocReturnValueEntity returnvalue,
			HttpServletRequest request,
			Model model,
			@RequestParam(value = "docid", required=false, defaultValue = "0") int docid
			){
        
        CacheUser user = CacheUtil.getCacheUser();
		Admin admin = user.getAdmin();
		model.addAttribute("referer", CommonConstants.ADMIN_SERVER + "/doc/api/returnvalue/list?docid=" + docid);
		if(returnvalue.getId() == null){
			returnvalue.setDocid(docid);
			returnvalue.setCreatedby(admin.getAdminName());
			docReturnValueServiceImpl.save(returnvalue);
			model.addAttribute("msg", "新增成功");
		}else{
			DocReturnValueEntity findParam = docReturnValueServiceImpl.getReturnValueById(returnvalue.getId());
			if(null == findParam){
				 log.warn("未找到，Id=" + returnvalue.getId());
			} else {
				returnvalue.setUpdateby(admin.getAdminName());
				docReturnValueServiceImpl.update(returnvalue);
				model.addAttribute("msg", "修改成功");
			}
		}
		return Constants.MSG_URL;
	}
	
	/**
	 * 删除API文档
	 * @param ids
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/del")
	public String deleteDoc(
			@RequestParam(value = "ids") String ids,
			Model model, 
			HttpServletRequest request
			){

		String referer = request.getHeader("Referer");
		model.addAttribute("referer", referer);
		if (StringUtils.isBlank(ids)) {
			model.addAttribute("result", "ID为空");
			model.addAttribute("msg", "删除失败，ID为空");
		}else{
			String[] idArray = StringUtils.split(ids, ",");
			for (String idStr : idArray) {
				docReturnValueServiceImpl.delete(Integer.parseInt(idStr));
			}
			model.addAttribute("msg", "删除成功");
		}
		return Constants.MSG_URL;
	}
	
	/**
	 * 根据id获取参数
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/findById")
	public String findByid(
			Model model,
			@RequestParam(required = false, value = "id", defaultValue = "") int id,
			@RequestParam(value = "docid", required=false, defaultValue = "0") int docid
			){
		DocReturnValueEntity returnvalue = docReturnValueServiceImpl.getReturnValueById(id);
		model.addAttribute("value", returnvalue);
		model.addAttribute("docid", docid);
		return "/doc/api/docreturnvalueedit";
	}
}
