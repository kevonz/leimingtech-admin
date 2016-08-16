package com.leimingtech.admin.module.goods.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.leimingtech.core.common.Constants;
import com.leimingtech.core.entity.base.Spec;
import com.leimingtech.core.entity.base.SpecValue;
import com.leimingtech.service.module.goods.service.GoodsClassService;
import com.leimingtech.service.module.goods.service.SpecService;
import com.leimingtech.service.module.goods.service.SpecValueService;
import com.leimingtech.service.utils.page.Pager;

@Controller
@RequestMapping("/goods/spec")
public class SpecAction {
	@Resource
    private SpecService specService;
    @Resource
    private GoodsClassService goodsClassService;
    @Resource
    private SpecValueService specValueService;

    /**
     * 列表
     * @param model
     * @return
     */
    @RequiresPermissions("sys:spec:view")
    @RequestMapping("/list")
    public String list(Model model,
                       @RequestParam(required=false, value="pageNo",defaultValue="")String pageNo){

        Pager pager = new Pager();
        Spec spec = new Spec();
        pager.setCondition(spec);
        //int total  = specService.findPageListCount(pager);//获取总条数
        if(!StringUtils.isEmpty(pageNo)){
            pager.setPageNo(Integer.parseInt(pageNo));
        }
        List<Spec> list=specService.findPageList(pager);
        pager.setResult(list);
        //pager.setTotalRows(total);
        model.addAttribute("pager", pager);//总数
        return "goods/spec/list";
    }

    /**
     * 删除
     * @return
     */
    @RequiresPermissions("sys:spec:edit")
    @RequestMapping("/delete")
    public String  delete(@RequestParam int[] ids,HttpServletRequest request,Model model){

        String referer = request.getHeader("Referer");
        for(int id : ids){
            specService.deleteSpecBySpId(id);
        }
        model.addAttribute("referer", referer);
        model.addAttribute("msg", "删除成功");
        return Constants.MSG_URL;
    }

    /**
     * 跳转
     * @param id
     * @return
     */
    @RequiresPermissions("sys:spec:view")
    @RequestMapping("/forward")
    public String forward(@RequestParam Integer id,Model model){

        model.addAttribute("list",goodsClassService.findAll());
        if(id == 0){
            return "goods/spec/save";
        }else{
        	//规格
            Spec goodsSpec=specService.findById(id);
            model.addAttribute("goodsSpec",goodsSpec);
            //规格值
            List<SpecValue> specValues = specValueService.findListBySpId(id);
            model.addAttribute("specValues",specValues);
            return "goods/spec/edit";
        }
    }

    /**
     * 保存或修改
     * @param goodsSpec
     * @return
     */
    @RequiresPermissions("sys:spec:edit")
    @RequestMapping("/saveOrUpdate")
    public String saveOrUpdate(@ModelAttribute Spec goodsSpec,HttpServletRequest request,Model model,String specValues){

        String referer = request.getHeader("Referer");
        model.addAttribute("referer", referer);
        if(goodsSpec.getSpId() == null){
            specService.save(goodsSpec, specValues);
            model.addAttribute("msg", "新增成功");
        }else{
            specService.update(goodsSpec, specValues);
            model.addAttribute("msg", "修改成功");
        }
        return Constants.MSG_URL;
    }

    /**18823
     * 修改排序åØ
     * @return
     */
    @RequiresPermissions("sys:spec:edit")
    @RequestMapping("/modifySort")
    public @ResponseBody Boolean modifySort(@RequestParam int id,@RequestParam Integer value){

        Spec spec = new Spec();
        spec.setSpId(id);
        spec.setSpSort(value);
        specService.update(spec);
        return true;
    }
    
    
    /**
     * 删除
     * @return
     */
    @RequiresPermissions("sys:spec:edit")
    @RequestMapping("/deleteSpeVal")
    public String  deleteSpeVal(@RequestParam int[] ids,HttpServletRequest request,Model model){
        for(int id : ids){
        	specValueService.deleteBySpValueId(id);
        }
        model.addAttribute("success", true);
        model.addAttribute("msg", "删除成功");
        return Constants.MSG_URL;
    }
}
