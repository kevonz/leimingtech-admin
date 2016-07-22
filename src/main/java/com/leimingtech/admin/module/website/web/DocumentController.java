package com.leimingtech.admin.module.website.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.leimingtech.core.common.Constants;
import com.leimingtech.core.entity.base.Document;
import com.leimingtech.service.module.website.service.DocumentService;
import com.leimingtech.service.utils.page.Pager;

/**
 * @author llf
 * @Package com.leimingtech.admin.module.website.web
 * @Description:
 * @date 2014/11/11 11:37
 */
@Slf4j
@Controller
@RequestMapping("/website/document")
public class DocumentController {

    @Resource
    private DocumentService documentService;

    @RequestMapping("/list")
    public String list(Model model,@RequestParam(required=false, value="div",defaultValue="")String div,
                       @RequestParam(required=false, value="pageNo",defaultValue="")String pageNoStr){

        Pager pager = new Pager();
        List<Document> results = documentService.findListForPage(pager);//结果集
        model.addAttribute("list",results);
        return "website/document/list";
    }

    @RequestMapping("/forward")
    public String forward(Model model,@RequestParam int id){

        if(id != 0){
            model.addAttribute("document",documentService.findById(id));
            return "website/document/edit";
        }else{
            return "website/document/save";
        }

    }

    @RequestMapping("/edit")
    public String saveOrUpdate(@ModelAttribute Document document,HttpServletRequest request,Model model){

        String referer = request.getHeader("Referer");
        model.addAttribute("referer", referer);
        if(document.getDocId() == null){
        	document.setCreateTime(System.currentTimeMillis());
            documentService.save(document);
            model.addAttribute("msg", "新增成功");
        }else{
        	document.setUpdateTime(System.currentTimeMillis());
            documentService.update(document);
            model.addAttribute("msg", "修改成功");
        }

        return Constants.MSG_URL;
    }

    /**
     * 删除文章
     * @param id
     * @return
     */
    @RequestMapping("/delete")
    public @ResponseBody Map<String,String> delete(@RequestParam int id){

        Map<String,String> map = Maps.newHashMap();

        if(id == 0){
            map.put("result", "删除失败");
            map.put("success", "false");
        }else{
            documentService.delete(id);
            map.put("result", "删除成功");
            map.put("success", "true");
        }

        return map;
    }
}
