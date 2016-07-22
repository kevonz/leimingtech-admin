package com.leimingtech.admin.module.doc.controller;

import java.util.List;
import java.util.Map;

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
import com.leimingtech.core.entity.base.DocEnRefEntity;
import com.leimingtech.service.module.doc.service.DocEntityRefService;
import com.leimingtech.service.module.doc.service.DocEntityService;
import com.leimingtech.service.utils.page.Pager;
import com.leimingtech.service.utils.sessionkey.CacheUser;
import com.leimingtech.service.utils.sessionkey.CacheUtil;

/**
 *    
 * 项目名称：leimingtech-admin   
 * 类名称：DocEntityRefController   
 * 类描述：文档实体关联管理
 * 创建人：lkang   
 * 创建时间：2015年5月05日 02:00:00   
 */
@Controller
@RequestMapping("/doc/api/docentityref")
@Slf4j
public class DocEntityRefController {
	@Autowired
	private DocEntityRefService docEntityRefService;
	@Autowired
	private DocEntityService docEntityService;
	
	/**
	 * 获取文档和实体关联列表
	 * @param model
	 * @param pageNoStr
	 * @param name
	 * @return
	 */
	@RequestMapping("/list")
	public String list(
			Model model,
			@RequestParam(value = "pageNo", required=false, defaultValue = "1") String pageNoStr,
			@RequestParam(value = "docid", required=false, defaultValue = "0") int docid
			) {
		try {
			
			Pager pager = new Pager();
			pager.setPageSize(6);
			if (null != pageNoStr && !pageNoStr.equals("")) {
				pageNoStr = pageNoStr.replace("," , "");
				pager.setPageNo(Integer.parseInt(pageNoStr));
			}
			
			DocEnRefEntity ref = new DocEnRefEntity();
			ref.setDocid(docid);
			
			pager.setCondition(ref);
			
			List<Map<String, String>> resultList = docEntityRefService.getRefList(pager);
			
	        pager.setResult(resultList);

			model.addAttribute("ref", resultList);
			model.addAttribute("pager", pager);
			model.addAttribute("docid", docid);
			
			return "/doc/api/docentityreflist";
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("获取文档和实体关联列表失败!");
		}
	}
	
	/**
	 * 保存关联
	 * @return
	 */
	@RequestMapping("/save")
	public String save(
			@ModelAttribute DocEnRefEntity docRef,
			HttpServletRequest request,
			Model model
			){
		String entityid = request.getParameter("entityid");
		String docid = request.getParameter("docid");
		CacheUser user = CacheUtil.getCacheUser();
		Admin admin = user.getAdmin();
		model.addAttribute("referer", CommonConstants.ADMIN_SERVER + "/doc/api/docentityref/list?docid=" + docid);
		try {
			docRef.setEntityid(Integer.parseInt(entityid));
			docRef.setDocid(Integer.parseInt(docid));
			docRef.setCreatedby(admin.getAdminName());
			docEntityRefService.save(docRef);
			model.addAttribute("msg", "保存成功");
		} catch (Exception e) {
			model.addAttribute("msg", "保存失败");
		}
        
		return Constants.MSG_URL;
	}
	
	/**
	 * 删除实体
	 * @param ids
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/del")
	public String deleteDoc(@RequestParam(value = "ids") String ids,
			Model model, HttpServletRequest request){

		String referer = request.getHeader("Referer");
		model.addAttribute("referer", referer);
		if (StringUtils.isBlank(ids)) {
			model.addAttribute("result", "ID为空");
			model.addAttribute("msg", "删除失败，ID为空");
		}else{
			String[] idArray = StringUtils.split(ids, ",");
			for (String idStr : idArray) {
				docEntityRefService.delete(Integer.parseInt(idStr));
			}
			model.addAttribute("msg", "删除成功");
		}
		return Constants.MSG_URL;
	}
	
}
