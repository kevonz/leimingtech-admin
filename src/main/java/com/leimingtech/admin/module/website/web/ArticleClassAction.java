package com.leimingtech.admin.module.website.web;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.leimingtech.core.common.Constants;
import com.leimingtech.core.entity.ArticleClass;
import com.leimingtech.core.entity.ArticleClassVo;
import com.leimingtech.service.module.website.service.ArticleClassService;

/**
 * Created by rabook on 2014/11/4.
 */
@Controller
@RequestMapping("/website/class")
public class ArticleClassAction {

    @Resource
    private ArticleClassService acService;

    /**
     * 列表
     * @param model
     * @return
     */
    @RequiresPermissions("sys:articleclass:view")
    @RequestMapping("/list")
    public String list(Model model){
        List<ArticleClassVo> results = acService.findListForPage();//结果集
        model.addAttribute("list", results);//结果集
        return "website/class/list";
    }

    @RequiresPermissions("sys:articleclass:edit")
    @RequestMapping("/delete")
    public String delete(@RequestParam int[] ids,Model model,HttpServletRequest request){

        String referer = request.getHeader("Referer");
        for(int id : ids){
            acService.delete(id);
        }
        model.addAttribute("referer", referer);
        model.addAttribute("msg", "删除成功");
        return Constants.MSG_URL;
    }

    @RequiresPermissions("sys:articleclass:view")
    @RequestMapping("/findOne")
    public String findOne(@RequestParam int id,Model model){

        model.addAttribute("list",acService.findList());
        model.addAttribute("parentId",id);
        return "website/class/save";
    }

    @RequiresPermissions("sys:articleclass:edit")
    @RequestMapping("/saveOrUpdate")
    public String saveOrUpdate(@ModelAttribute ArticleClass articleClass,Model model,HttpServletRequest request){

        String referer = request.getHeader("Referer");
        model.addAttribute("referer", referer);
        if(articleClass.getAcId() == null){
        	articleClass.setCreateTime(System.currentTimeMillis());
            acService.save(articleClass);
            model.addAttribute("msg", "新增成功");
        }else{
            ArticleClass ac = acService.findById(articleClass.getAcId());
            ac.setAcName(articleClass.getAcName());
            ac.setAcSort(articleClass.getAcSort());
            ac.setUpdateTime(System.currentTimeMillis());
            acService.update(ac);
            model.addAttribute("msg", "修改成功");
        }

        return Constants.MSG_URL;
    }

    @RequiresPermissions("sys:articleclass:view")
    @RequestMapping("/editFind")
    public String findForUpdate(@RequestParam int id,Model model){

        model.addAttribute("list",acService.findList());
        model.addAttribute("class",acService.findById(id));
        return "website/class/edit";
    }

    /**
     * 查询子列表
     * @param id 父id
     * @return json
     */
    @RequiresPermissions("sys:articleclass:view")
    @RequestMapping("child")
    public @ResponseBody  List<ArticleClassVo> child(@RequestParam int id,@RequestParam int level){

        //存入deep，配合ajax
        List<ArticleClassVo> classList = acService.findChildren(id);
        for(ArticleClassVo vo : classList){
            vo.setDeep(level);
        }
        return classList;
    }

    /**
     * 修改排序
     * @return
     */
    @RequiresPermissions("sys:articleclass:edit")
    @RequestMapping("/modifySort")
    public @ResponseBody Boolean modifySort(@RequestParam int id,@RequestParam Integer value){

        ArticleClass articleClass = new ArticleClass();
        articleClass.setAcId(id);
        articleClass.setAcSort(value);
        articleClass.setUpdateTime(System.currentTimeMillis());
        acService.update(articleClass);
        return true;
    }

    /**
     * 修改分类名称
     * @param id
     * @param value
     * @return
     */
    @RequiresPermissions("sys:articleclass:edit")
    @RequestMapping("/modifyName")
    public @ResponseBody Boolean modifyname(@RequestParam int id,@RequestParam String value){

        ArticleClass articleClass = new ArticleClass();
        articleClass.setAcId(id);
        articleClass.setAcName(value);
        //判断是否有重复名称
        if(acService.duplicate(articleClass) > 0){
            return false;
        }else{
            //执行修改操作
        	articleClass.setUpdateTime(System.currentTimeMillis());
            acService.update(articleClass);
            return true;
        }
    }

    /**
     * 校验表单
     * @return
     */
    @RequiresPermissions("sys:articleclass:view")
    @RequestMapping("/validate")
    public @ResponseBody String validateForm(@ModelAttribute ArticleClass articleClass){

        //校验重复
        if(acService.duplicate(articleClass) > 0){
            return "false";
        }else{
            return "true";
        }

    }
}
