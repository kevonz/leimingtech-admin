package com.leimingtech.admin.module.trade.controller;

import java.util.List;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.leimingtech.core.entity.base.Complain;
import com.leimingtech.service.module.trade.service.ComplainService;
import com.leimingtech.service.utils.page.Pager;

/**
 * Created by rabook on 2014/12/20.
 */

@Slf4j
@Controller
@RequestMapping("/trade/complain")
public class ComplainAction {

    @Resource
    private ComplainService complainService;

    /**
     * 列表
     * @param model
     * @param complain
     * @param pageNo
     * @return
     */
    @RequestMapping("/list")
    public String list(Model model,@ModelAttribute Complain complain,
                       @RequestParam(required = false, value = "pageNo", defaultValue = "") String pageNo){

        Pager pager = new Pager();
        if(StringUtils.isNotBlank(pageNo)){
            pager.setPageNo(Integer.parseInt(pageNo));
        }
        if(complain.getComplainState() == null){
            complain.setComplainState(10);
        }
        pager.setCondition(complain);
        int total = complainService.findCount(pager);
        List<Complain> list = complainService.findList(pager);
        pager.setTotalRows(total);
        pager.setResult(list);
        model.addAttribute("pager",pager);
        model.addAttribute("complain",complain);
        return "trade/complain/list";
    }
}
