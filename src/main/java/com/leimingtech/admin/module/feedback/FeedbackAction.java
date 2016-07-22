/**
 * 
 */
package com.leimingtech.admin.module.feedback;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.leimingtech.core.common.Constants;
import com.leimingtech.core.entity.base.Feedback;
import com.leimingtech.service.module.feedback.service.FeedbackService;
import com.leimingtech.service.utils.page.Pager;

/**
 * <p>Title: FeedbackAction.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2014-2018</p>
 * <p>Company: leimingtech.com</p>
 * @author linjm
 * @date 2015年8月25日
 * @version 1.0
 */

@Controller
@RequestMapping("/feedback")
@Slf4j
public class FeedbackAction {
	
	@Autowired
	private FeedbackService feedbackService;
	
	@RequiresPermissions("sys:feedback:view")
	@RequestMapping("/list")
	public String list(Model model, @RequestParam(required = false, value = "pageNo", defaultValue = "") String pageNoStr,
			Feedback feedback){
		Pager pager = new Pager();
		if (StringUtils.isNumeric(pageNoStr)) {
			pager.setPageNo(Integer.parseInt(pageNoStr));
		}
		pager.setCondition(feedback);//实体加载在pager中
		List<Feedback> results = feedbackService.findBylist(pager);
        pager.setResult(results);
        model.addAttribute("pager",pager);
		return "/feedback/listFeedback";
	}
	
	@RequiresPermissions("sys:feedback:view")
	@RequestMapping("/add")
	public String add(Model model, Feedback feedback){
		return "/feedback/addFeedback";
	}
	
	@RequiresPermissions("sys:feedback:edit")
	@RequestMapping("/saveOrUpdate")
	public String saveOrUpdate(Model model,Feedback feedback,
			HttpServletRequest request,
			@RequestParam(required = false, value = "div", defaultValue = "") String div) {
        //String referer = request.getHeader("Referer");
        model.addAttribute("referer", "/feedback/listFeedback");
		feedbackService.save(feedback);
		model.addAttribute("msg", "修改成功");
		return Constants.MSG_URL;
	}
}
