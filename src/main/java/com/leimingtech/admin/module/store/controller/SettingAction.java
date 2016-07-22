package com.leimingtech.admin.module.store.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.leimingtech.core.common.Constants;
import com.leimingtech.core.entity.base.Store;
import com.leimingtech.service.module.setting.service.SettingService;
import com.leimingtech.service.module.store.service.StoreService;
import com.leimingtech.service.module.store.vo.SettingVo;
import com.leimingtech.service.utils.page.Pager;

/**
 * 项目名称：leimingtech-admin
 * 类名称：ClasssAction
 * 类描述：
 * 创建人：weiyue
 * 创建时间：2014年11月5日 下午10:59:16
 * 修改人：weiyue
 * 修改时间：2014年11月5日 下午10:59:16
 * 修改备注：
 */
@Controller
@RequestMapping("/store/setting")
public class SettingAction {

    String message = "success";
    @Resource
    private SettingService settingService;
    @Resource
    private  StoreService storeService;


    /**
     * @param @return /store/setting/index.flt
     * @return String    返回类型
     * @throws
     * @Title: index
     * @Description: TODO(菜单调用的方法)
     */
    @RequiresPermissions("sys:storedomain:view")
    @RequestMapping("/index")
    public String index(Model model) {
        //生成需要查询的map
        Map<String, String> map = new HashMap<String, String>();
        map.put("enabledSubdomain", "enabled_subdomain");
        map.put("subdomainEdit", "subdomain_edit");
        map.put("subdomainLength", "subdomain_length");
        map.put("subdomainReserved", "subdomain_reserved");
        map.put("subdomainTimes", "subdomain_times");
        SettingVo sv = settingService.queryClasssMap(map);
        model.addAttribute("SettingVo", sv);//父节点结果集
        return "/store/setting/index";
    }

    /**
     * @param @return /store/setting/index.flt
     * @return String    返回类型
     * @throws
     * @Title: edit 编辑用
     * @Description: TODO(菜单调用的方法)
     */
    @RequiresPermissions("sys:storedomain:view")
    @RequestMapping("/edit")
    public String edit(@ModelAttribute SettingVo settingVo,
                       Model model, HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        model.addAttribute("referer", referer);
        settingService.update(settingVo);
        model.addAttribute("msg", "修改成功");
        return Constants.MSG_URL;
    }

    /**
     * @param @return /store/setting/index.flt
     * @return String    返回类型
     * @throws
     * @Title: findList 查询商户的二级域名列表
     * @Description: TODO(菜单调用的方法)
     */
    @RequiresPermissions("sys:storedomain:view")
    @RequestMapping("/findList")
    public String findList(Model model,
                           @RequestParam(required = false, value = "pageNo", defaultValue = "") String pageNo,
                           @ModelAttribute Store store) {

        Pager pager = new Pager();
        if (StringUtils.isNotBlank(pageNo)) {
            pager.setPageNo(Integer.parseInt(pageNo));
        }
        pager.setCondition(store);//实体加载在pager中
       // int total = settingService.countStoreDetail(pager);//获取总条数
        int total =storeService.queryCount(store);
        //List<Store> results = settingService.queryStoreDetailList(pager);//结果集
        List<Store> results =storeService.queryList(pager);
        pager.setTotalRows(total);
        pager.setResult(results);
        model.addAttribute("pager", pager);
        model.addAttribute("store", store);
        //转发请求到FTL页面
        return "/store/setting/findList";

    }

    /**
     * @param @param  model
     * @param @param  id
     * @param @param  div
     * @param @return 设定文件
     * @return String    返回类型
     * @throws
     * @Title: findDomainEdit
     * @Description: TODO(查找店铺二级域名以供编辑使用)
     */
    @RequiresPermissions("sys:storedomain:view")
    @RequestMapping(value = "/findDomainEdit")
    public String findForEdit(Model model,
                              @RequestParam(required = false, value = "id", defaultValue = "") Integer id) {

       // StoreDetail storeDetail = settingService.findById(id);
        Store store=storeService.findById(id);
        model.addAttribute("storeDetail", store);

        return "/store/setting/DomainEdit";
    }

    /**
     * @param @param  model
     * @param @param  id
     * @param @param  div
     * @param @return 设定文件
     * @return String    返回类型
     * @throws
     * @Title: updateDomain
     * @Description: TODO(修改店铺二级域名使用)
     */
    @RequiresPermissions("sys:storedomain:edit")
    @RequestMapping("/updateDomain")
    public String updateDomain(@ModelAttribute Store store, Model model
            , HttpServletRequest request) {
        String referer = request.getHeader("Referer");
//        if (store.getStoreId() == null) {
//            model.addAttribute("referer", referer);
//            model.addAttribute("msg", "编辑失败");
//        } else {
//           // StoreDetail dbDetail = settingService.findById(detail.getStoreId());
//        	  Store store=storeService.findById(store.gets);
//            if (store == null) {
//                model.addAttribute("referer", referer);
//                model.addAttribute("msg", "编辑失败");
//            } else {
//            	store.setStoreDomain(store.getStoreDomain());
//            	store.setStoreDomainTimes(store.getStoreDomainTimes());
//                settingService.updateDomain(dbDetail);
//                model.addAttribute("referer", referer);
//                model.addAttribute("msg", "编辑成功");
//            }
//        }
        return Constants.MSG_URL;
    }
}