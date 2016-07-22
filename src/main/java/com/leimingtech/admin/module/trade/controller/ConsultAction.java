package com.leimingtech.admin.module.trade.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.leimingtech.core.common.Constants;
import com.leimingtech.core.entity.base.Consult;
import com.leimingtech.service.module.trade.service.ConsultService;
import com.leimingtech.service.utils.page.Pager;

/**
 * Created by rabook on 2014/12/20.
 */
@Slf4j
@Controller
@RequestMapping("/trade/consult")
public class ConsultAction {

    @Resource
    private ConsultService consultService;

    /**
     * 列表
     * @param model
     * @param consult
     * @param pageNo
     * @return
     */
    @RequiresPermissions("sys:consultative:view")
    @RequestMapping("/list")
    public String list(Model model,@ModelAttribute Consult consult,
                       @RequestParam(required = false, value = "pageNo", defaultValue = "") String pageNo){

        Pager pager = new Pager();
        if(StringUtils.isNotBlank(pageNo)){
            pager.setPageNo(Integer.parseInt(pageNo));
        }
        pager.setCondition(consult);
        List<Consult> list = consultService.findList(pager);
        pager.setResult(list);
        model.addAttribute("pager",pager);
        model.addAttribute("consult",consult);
        return "trade/consult/list";
    }

    @RequiresPermissions("sys:consultative:edit")
    @RequestMapping("/delete")
    public String delete(@RequestParam int[] ids,Model model,HttpServletRequest request){

        String referer = request.getHeader("Referer");
        for(int id : ids){
            consultService.delete(id);
        }
        model.addAttribute("referer", referer);
        model.addAttribute("msg", "删除成功");
        return Constants.MSG_URL;
    }
}
