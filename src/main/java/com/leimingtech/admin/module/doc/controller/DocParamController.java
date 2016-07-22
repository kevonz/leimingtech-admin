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
import com.leimingtech.core.entity.base.DocParamEntity;
import com.leimingtech.service.module.doc.service.DocParamService;
import com.leimingtech.service.utils.page.Pager;
import com.leimingtech.service.utils.sessionkey.CacheUser;
import com.leimingtech.service.utils.sessionkey.CacheUtil;

/**
 *    
 * 项目名称：leimingtech-admin   
 * 类名称：DocParamController   
 * 类描述：API文档参数管理
 * 创建人：lkang   
 * 创建时间：2015年5月07日 02:00:00   
 */
@Controller
@RequestMapping("/doc/api/param")
@Slf4j
public class DocParamController {

	@Autowired
	private DocParamService docParamServiceImpl;
	
	/**
	 * 参数列表页
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
			
			DocParamEntity param = new DocParamEntity();
			param.setDocid(docid);
			
			// 根据名称查询
			if(StringUtils.isNotEmpty(name)){
				param.setName(name);
			}
			
			// 查询条件放入分页pager中
			pager.setCondition(param);
			
			List<DocParamEntity> resultList = docParamServiceImpl.getParamList(pager);
			
	        pager.setResult(resultList);

			model.addAttribute("doc", resultList);
			model.addAttribute("pager", pager);
			model.addAttribute("name", name);
			model.addAttribute("docid", docid);
			return "/doc/api/docparamlist";
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("获取文档列表失败!");
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
		return "/doc/api/docparamadd";
	}
	
	/**
	 * 修改或新增参数
	 * @return
	 */
	@RequestMapping("/saveOrUpdate")
	public String saveOrUpdate(
			@ModelAttribute DocParamEntity param,
			HttpServletRequest request,
			Model model,
			@RequestParam(value = "docid", required=false, defaultValue = "0") int docid
			){
        
        CacheUser user = CacheUtil.getCacheUser();
		Admin admin = user.getAdmin();
		model.addAttribute("referer", CommonConstants.ADMIN_SERVER + "/doc/api/param/list?docid=" + docid);
		if(param.getId() == null){
			param.setDocid(docid);
			param.setCreatedby(admin.getAdminName());
			docParamServiceImpl.save(param);
			model.addAttribute("msg", "新增成功");
		}else{
			DocParamEntity findParam = docParamServiceImpl.getParamById(param.getId());
			if(null == findParam){
				 log.warn("未找到，Id=" + param.getId());
			} else {
				param.setUpdateby(admin.getAdminName());
				docParamServiceImpl.update(param);
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
				docParamServiceImpl.delete(Integer.parseInt(idStr));
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
		DocParamEntity param = docParamServiceImpl.getParamById(id);
		model.addAttribute("param", param);
		model.addAttribute("docid", docid);
		return "/doc/api/docparamedit";
	}
}
