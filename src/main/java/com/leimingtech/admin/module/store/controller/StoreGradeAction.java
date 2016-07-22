package com.leimingtech.admin.module.store.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.leimingtech.core.common.Constants;
import com.leimingtech.core.entity.base.StoreGrade;
import com.leimingtech.service.module.store.service.StoreGradeService;
import com.leimingtech.service.utils.page.Pager;

/**
 * Created by rabook on 2014/12/12.
 */
@Slf4j
@Controller
@RequestMapping("/store/grade")
public class StoreGradeAction {

    @Resource
    private StoreGradeService storeGradeService;
    /**
     * 列表
     * @param model
     * @return
     */
    @RequiresPermissions("sys:storegrade:view")
    @RequestMapping("/list")
    public String list(Model model,@RequestParam(required=false, value="sgName",defaultValue="") String sgName){

        Pager pager = new Pager();
        StoreGrade storeGrade = new StoreGrade();
        storeGrade.setSgName(sgName);
        pager.setCondition(storeGrade);
        model.addAttribute("list",storeGradeService.queryStoreGradeList(pager));
        model.addAttribute("sgName",sgName);
        return "store/grade/list";
    }

    /**
     * 跳转
     * @param id
     * @param model
     * @return
     */
    @RequiresPermissions("sys:storegrade:view")
    @RequestMapping("/forward")
    public String forward(@RequestParam long id,Model model){
        model.addAttribute("grade",storeGradeService.queryById(id));
        if(id == 0){
            return "store/grade/save";
        }else{
            return "store/grade/edit";
        }
    }

    /**
     * 校验重复
     * @return
     */
    @RequiresPermissions("sys:storegrade:view")
    @RequestMapping("/validate")
    public @ResponseBody boolean validate(@ModelAttribute StoreGrade storeGrade){

        if(storeGradeService.queryCount(storeGrade) > 0){
            return false;
        }else{
            return true;
        }
    }

    /**
     * 编辑或新增
     * @param storeGrade
     * @param request
     * @param model
     * @return
     */
    @RequiresPermissions("sys:storegrade:edit")
    @RequestMapping("/edit")
    public String edit(@ModelAttribute StoreGrade storeGrade,HttpServletRequest request,Model model){

        String referer = request.getHeader("Referer");
        model.addAttribute("referer", referer);
        if(storeGrade.getSgId() == null){
            //新增
            storeGrade.setSgSpaceLimit(100);
            //默认需要审核
            storeGrade.setSgConfirm(1);
            storeGradeService.save(storeGrade);
            model.addAttribute("msg", "新增成功");
        }else{
            //修改
            storeGradeService.update(storeGrade);
            model.addAttribute("msg", "修改成功");
        }
        return Constants.MSG_URL;
    }

    @RequiresPermissions("sys:storegrade:edit")
    @RequestMapping("/delete")
    public String delete(@RequestParam long[] ids,HttpServletRequest request,Model model){

        String referer = request.getHeader("Referer");
        for(long id : ids){
            storeGradeService.delete(id);
        }
        model.addAttribute("referer", referer);
        model.addAttribute("msg", "删除成功");
        return Constants.MSG_URL;
    }
}
