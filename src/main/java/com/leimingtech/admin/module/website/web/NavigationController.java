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

import com.leimingtech.core.common.Constants;
import com.leimingtech.core.entity.base.Navigation;
import com.leimingtech.service.module.goods.service.GoodsClassService;
import com.leimingtech.service.module.website.service.ArticleClassService;
import com.leimingtech.service.module.website.service.NavigationService;
import com.leimingtech.service.utils.page.Pager;

/**
 * @author llf
 * @Package com.leimingtech.admin.module.website.web
 * @Description:
 * @date 2014/11/11 14:46
 */

@Slf4j
@Controller
@RequestMapping("/website/navigation")
public class NavigationController {

    @Resource
    private NavigationService navigationService;
    @Resource
    private GoodsClassService goodsClassService;
    @Resource
    private ArticleClassService articleClassService;

    /**
     * 列表
     *
     * @param model
     * @param pageNoStr
     * @param navigation
     * @return
     */
    @RequiresPermissions("sys:pagenavigation:view")
    @RequestMapping("/list")
    public String list(Model model,
                       @RequestParam(required = false, value = "pageNo", defaultValue = "") String pageNoStr,
                       @ModelAttribute Navigation navigation) {

        Pager pager = new Pager();
        int total = navigationService.findCount(navigation);//获取总条数
        if (!StringUtils.isEmpty(pageNoStr)) {
            pager.setPageNo(Integer.parseInt(pageNoStr));
        }
        pager.setCondition(navigation);
        List<Navigation> results = navigationService.findListForPage(pager);//结果集
        pager.setTotalRows(total);
        pager.setResult(results);
        model.addAttribute("pager",pager);
        model.addAttribute("navi",navigation);
        return "website/navigation/list";
    }

    /**
     * 跳转
     *
     * @return
     */
    @RequiresPermissions("sys:pagenavigation:view")
    @RequestMapping("/forward")
    public String forward(Model model, @RequestParam int id) {

        model.addAttribute("gClassList", goodsClassService.findAll());
        model.addAttribute("aClassList", articleClassService.findAllList());
        if (id != 0) {
            model.addAttribute("nav", navigationService.findById(id));
            return "website/navigation/edit";
        } else {
            return "website/navigation/save";
        }
    }

    /**
     * 保存或修改
     *
     * @param navigation
     * @return
     */
    @RequiresPermissions("sys:pagenavigation:edit")
    @RequestMapping("/edit")
    public String saveOrUpdate(@ModelAttribute Navigation navigation,Model model,HttpServletRequest request) {

        String referer = request.getHeader("Referer");
        model.addAttribute("referer", referer);
        if (navigation.getNavId() == null) {
            navigationService.save(navigation);
            model.addAttribute("msg", "新增成功");
        } else {
            navigationService.update(navigation);
            model.addAttribute("msg", "修改成功");
        }

        return Constants.MSG_URL;
    }

    /**
     * 删除
     * @return
     */
    @RequiresPermissions("sys:pagenavigation:edit")
    @RequestMapping("/delete")
    public String delete(@RequestParam int[] ids,Model model,HttpServletRequest request) {

        String referer = request.getHeader("Referer");
        model.addAttribute("referer", referer);
        for(int id : ids){
            navigationService.delete(id);
        }
        model.addAttribute("msg", "删除成功");
        return Constants.MSG_URL;
    }
}
