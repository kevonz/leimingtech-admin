package com.leimingtech.admin.module.goods.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.leimingtech.core.entity.GoodsAttribute;
import com.leimingtech.service.module.goods.service.AttributeService;

@Controller
@RequestMapping("/goods/attr")
public class AttrAction {
	@Resource
    private AttributeService attributeService;

    /**
     * 查询单条
     * @param id
     * @return
     */
    @RequestMapping("/find")
    public String findAttr(@RequestParam int id,Model model){

        model.addAttribute("attr", attributeService.findById(id));
        return "goods/type/attrEdit";
    }

    @RequestMapping("/save")
    public String save(@ModelAttribute GoodsAttribute goodsAttribute){

        attributeService.save(goodsAttribute);
        return "redirect:/goods/type/list";
    }
}
