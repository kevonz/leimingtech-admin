package com.leimingtech.admin.module.website.web;

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

import com.leimingtech.core.common.CommonConstants;
import com.leimingtech.core.common.Constants;
import com.leimingtech.core.entity.base.Article;
import com.leimingtech.service.module.website.service.ArticleClassService;
import com.leimingtech.service.module.website.service.ArticleService;
import com.leimingtech.service.utils.page.Pager;

/**
 * Created by rabook on 2014/11/9.
 */
@Slf4j
@Controller
@RequestMapping("/website/article")
public class ArticleController {

    @Resource
    private ArticleClassService articleClassService;
    @Resource
    private ArticleService articleService;

    @RequiresPermissions("sys:article:view")
    @RequestMapping("/index")
    public String index(Model model,@RequestParam(required=false, value="div",defaultValue="")String div,
            @RequestParam(required=false, value="pageNo",defaultValue="")String pageNoStr,
            @ModelAttribute Article article){

    	 Pager pager = new Pager();
//         int total  = articleService.findCount(article);//获取总条数
         if(!StringUtils.isEmpty(pageNoStr)){
             pager.setPageNo(Integer.parseInt(pageNoStr));
         }
         List<Article> results = articleService.findListForPage(pager,article);//结果集
         pager.setResult(results);
//         pager.setTotalRows(total);
         model.addAttribute("pager", pager);//总数
         model.addAttribute("classList",articleClassService.findAllList());
         model.addAttribute("article", article);//总数
         return "website/article/index";
    }
    

    @RequiresPermissions("sys:article:view")
    @RequestMapping("/forward")
    public String forward(Model model,@RequestParam int id){

        model.addAttribute("list",articleClassService.findAllList());
        if(id != 0){
            model.addAttribute("article",articleService.findById(id));
            return "website/article/update";
        }else{
            return "website/article/save";
        }

    }

    @RequiresPermissions("sys:article:edit")
    @RequestMapping("/edit")
    public String saveOrUpdate(@ModelAttribute Article article,HttpServletRequest request,Model model){

        if(article.getArticleId() == null){
        	article.setCreateTime(System.currentTimeMillis());
            articleService.save(article);
            model.addAttribute("msg", "新增成功");
        }else{
        	article.setUpdateTime(System.currentTimeMillis());
            articleService.update(article);
            model.addAttribute("msg", "修改成功");
        }
        //String referer = request.getHeader("Referer");
        model.addAttribute("referer", CommonConstants.ADMIN_SERVER+"/website/article/index");
        return Constants.MSG_URL;
    }

    /**
     * 删除文章
     * @param ids
     * @return
     */
    @RequiresPermissions("sys:article:edit")
    @RequestMapping("delete")
    public String delete(@RequestParam int[] ids,HttpServletRequest request,Model model){
        String referer = request.getHeader("Referer");
        for(int id : ids){
                articleService.delete(id);
        }
        model.addAttribute("referer", referer);
        model.addAttribute("msg", "删除成功");
        return Constants.MSG_URL;
    }
}
