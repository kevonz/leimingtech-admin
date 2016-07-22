package com.leimingtech.admin.module.trade.controller;

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

import com.leimingtech.core.common.Constants;
import com.leimingtech.core.entity.base.EvaluateStore;
import com.leimingtech.service.module.store.service.EvaluateStoreService;
import com.leimingtech.service.utils.page.Pager;

/**
 * @author llf
 * @Package com.leimingtech.admin.module.trade.controller
 * @Description:
 * @date 2014/11/12 17:57
 */

@Slf4j
@Controller
@RequestMapping("/trade/evalStore")
public class EvaluateStoreController {

    @Autowired
    private EvaluateStoreService evaluateStoreService;

    @RequestMapping("/index")
    public String index(){
        return "trade/evalStore/index";
    }

    @RequestMapping("/list")
    public String list(Model model,@ModelAttribute EvaluateStore evaluateStore,
                       @RequestParam(required=false, value="div",defaultValue="")String div,
                       @RequestParam(required=false, value="pageNo",defaultValue="") String pageNoStr){

        Pager pager = new Pager();
        int total  = evaluateStoreService.findCount(evaluateStore);//获取总条数
        if(!StringUtils.isEmpty(pageNoStr)){
            pager.setPageNo(Integer.parseInt(pageNoStr));
        }
        pager.setCondition(evaluateStore);
        List<EvaluateStore> results = evaluateStoreService.findPageList(pager);//结果集
        pager.setTotalRows(total);
        pager.setResult(results);
        model.addAttribute("pager",pager);
        model.addAttribute("seval",evaluateStore);
        return "trade/evalStore/list";
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @RequestMapping("/delete")
    public String delete(@RequestParam int id,Model model,HttpServletRequest request){

        String referer = request.getHeader("Referer");
        model.addAttribute("referer", referer);
        if(id == 0){
            model.addAttribute("msg", "删除失败");
        }else{
            evaluateStoreService.delete(id);
            model.addAttribute("msg", "删除成功");
        }
        return Constants.MSG_URL;
    }
}
