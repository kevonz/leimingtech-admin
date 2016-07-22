package com.leimingtech.admin.module.dictionary.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.leimingtech.core.common.CommonConstants;
import com.leimingtech.core.common.Constants;
import com.leimingtech.core.entity.base.Dictionary;
import com.leimingtech.service.module.dictionary.service.DictionaryGroupService;
import com.leimingtech.service.module.dictionary.service.DictionaryService;
import com.leimingtech.service.utils.page.Pager;

@Controller
@RequestMapping("/group/dictionary")
@Slf4j
public class DictionaryAction {

	@Resource
	private DictionaryService dictionaryService;
	
	@Resource
	private DictionaryGroupService dictionaryGroupService;
	
	/**
	 * 字典项分页列表
	 * @param model
	 * @param dictionary
	 * @param div
	 * @param pageNo
	 * @return
	 */
	@RequestMapping("/list")
	public String list(Model model,@ModelAttribute Dictionary dictionary,
			@RequestParam(required = false, value = "groupId", defaultValue = "") String groupId,
			@RequestParam(required = false, value = "pageNo", defaultValue = "") String pageNo){
		try {
			Pager pager = new Pager();
			if(StringUtils.isNotEmpty(groupId)){
				dictionary.setGroupId(Integer.valueOf(groupId));
			}
			pager.setCondition(dictionary);
			if(!StringUtils.isEmpty(pageNo)){
	            pager.setPageNo(Integer.parseInt(pageNo));
	        }
			List<Dictionary> dictionaryList = dictionaryService.queryDictionaryidList(pager);
			pager.setResult(dictionaryList);
			model.addAttribute("dictionaryList", dictionaryList);
			model.addAttribute("pager", pager);//总数
			model.addAttribute("groupId", groupId);
			model.addAttribute("dictionaryGroup", dictionaryGroupService.findByGroupId(Integer.valueOf(groupId)));
			model.addAttribute("toUrl", "group/dictionary/list");
			return "group/dictionary/list";
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("导航失败!");
		}
	}
	
	/**
	 * 跳转保存、编辑页面
	 * @param id
	 * @param model
	 * @param dictionary
	 * @return
	 */
	@RequestMapping("/forward")
    public String forward(@RequestParam int id,@RequestParam int groupId,Model model,@ModelAttribute Dictionary dictionary){
		model.addAttribute("dictionaryCode", dictionaryGroupService.findByGroupId(Integer.valueOf(groupId)).getGroupCode());
		if(id==0){
			model.addAttribute("groupId", groupId);
			return  "group/dictionary/save";
		}else{
			model.addAttribute("groupId", groupId);
			model.addAttribute("dictionary", dictionaryService.findByDictionaryId(id));
			return "group/dictionary/edit";
		}
	}
	/**
	 * 字典保存、修改
	 * @param dictionary
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/save")
    public String save(@ModelAttribute Dictionary dictionary,Model model,HttpServletRequest request){
//		String referer = request.getHeader("Referer");
		model.addAttribute("referer", CommonConstants.ADMIN_SERVER+"/group/dictionary/list?groupId="+dictionary.getGroupId());
		if(dictionary.getDictionaryId()!=null){
	    	dictionaryService.update(dictionary);
	    	model.addAttribute("msg", "修改成功");
	    }else{
	    	dictionaryService.save(dictionary);
	    	model.addAttribute("msg", "保存成功");
	    }
		return Constants.MSG_URL;
	}
	/**
	 * 字典删除
	 * @param ids
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping("/delete")
    public String delete(@RequestParam int[] ids,HttpServletRequest request,Model model){
        String referer = request.getHeader("Referer");
        for(int id : ids){
        	dictionaryService.delete(id);
        }
        model.addAttribute("referer", referer);
        model.addAttribute("msg", "删除成功");
//        adminLogService.save("删除数据字典", request);
        return Constants.MSG_URL;
    }
}
