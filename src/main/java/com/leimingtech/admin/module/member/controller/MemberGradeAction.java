package com.leimingtech.admin.module.member.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.leimingtech.core.common.Constants;
import com.leimingtech.core.entity.base.MemberGrade;
import com.leimingtech.service.module.member.service.MemberGradeService;
import com.leimingtech.service.utils.page.Pager;

/**
 * @author cgl
 * 会员等级
 * 2015年08月24日17:04:17
 */
@Controller
@RequestMapping("/member/grade")
public class MemberGradeAction {

	String message = "success";

	@Autowired
	private MemberGradeService memberGradeService;


	/** 
	 * 列表页
	 */
	@RequiresPermissions("sys:membergrade:view")
	@RequestMapping("/list")
	public ModelAndView list(
			@RequestParam(required = false, value = "pageNo", defaultValue = "") String pageNo
			) {
		
		Pager pager = new Pager();
		
		ModelAndView modelAndView = new ModelAndView("/membergrade/list");
		
		pager.setCondition(new MemberGrade());
		
		int total = memberGradeService.findMemberGradeCount(new MemberGrade());//总条数
		
		if(!StringUtils.isEmpty(pageNo)){
            pager.setPageNo(Integer.parseInt(pageNo));
        }
		
		List<MemberGrade> list = memberGradeService.findMemberGradePageList(pager);
		
		pager.setResult(list);
		pager.setTotalRows(total);
		modelAndView.addObject("MemberGrades", list);
		modelAndView.addObject("pager", pager);//总数
		modelAndView.addObject("toUrl", "member/grade/list");
		
		return modelAndView;
	}
	
	/**
	 * 删除
	 */
	@RequiresPermissions("sys:membergrade:edit")
	@RequestMapping("/delete")
    public String delete(@RequestParam int[] ids,HttpServletRequest request,Model model){

        String referer = request.getHeader("Referer");
        for(int id : ids){
        	memberGradeService.delete(id);
        }
        model.addAttribute("referer", referer);
        model.addAttribute("msg", "删除成功");
        return Constants.MSG_URL;
    }
	
	/**
	 * 新增页面
	 */
	@RequiresPermissions("sys:membergrade:view")
	@RequestMapping("/add")
	public ModelAndView add(){
		
		ModelAndView modelAndView = new ModelAndView("/membergrade/add");
		
		return modelAndView;
	}
	
	/**
	 * 新增
	 */
	@RequiresPermissions("sys:membergrade:view")
	@RequestMapping("/add.do")
	public String addDo(@ModelAttribute MemberGrade memberGrade,Model model,HttpServletRequest request){
		
		String referer = request.getHeader("Referer");
		
	    model.addAttribute("referer", referer);
	    
	    memberGradeService.save(memberGrade);
	    
	    model.addAttribute("msg", "保存成功");
	    
	    return Constants.MSG_URL;
	}
	
	/**
	 * 修改页面
	 */
	@RequiresPermissions("sys:membergrade:view")
	@RequestMapping("/update")
	public ModelAndView update(Integer id){
		
		ModelAndView modelAndView = new ModelAndView("/membergrade/update");
		
		MemberGrade memberGrade = memberGradeService.findMembeGraderById(id);
		
		modelAndView.addObject("memberGrade", memberGrade);
		
		return modelAndView;
	}
	
	/**
	 * 修改
	 */
	@RequiresPermissions("sys:membergrade:view")
	@RequestMapping("/update.do")
	public String updateDo(@ModelAttribute MemberGrade memberGrade,Model model,HttpServletRequest request){
		
		String referer = request.getHeader("Referer");
		
		model.addAttribute("referer", referer);
		
		memberGradeService.update(memberGrade);
		
		model.addAttribute("msg", "保存成功");
		
		return Constants.MSG_URL;
	}
	
	/**
	 * 修改默认的会员等级
	 */
	@RequiresPermissions("sys:membergrade:edit")
	@RequestMapping("/updateDefault.do")
	public String updateDefaultDo(@RequestParam int id,@RequestParam int value){
		
		memberGradeService.updateDefault(id);
		
		return "redirect:/member/grade/list";
	}
}