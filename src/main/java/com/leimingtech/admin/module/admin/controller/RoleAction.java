package com.leimingtech.admin.module.admin.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.leimingtech.core.common.CommonConstants;
import com.leimingtech.core.common.Constants;
import com.leimingtech.core.entity.base.Roles;
import com.leimingtech.service.module.admin.service.RoleMenuService;
import com.leimingtech.service.module.admin.service.RoleService;
import com.leimingtech.service.module.dictionary.service.DictionaryService;
import com.leimingtech.service.utils.page.Pager;

@Controller
@RequestMapping("/role")
@Slf4j
public class RoleAction {

	String message = "success";

	@Resource
	private RoleService roleService;
	@Resource
	private RoleMenuService roleMenuService;
	@Resource
	private DictionaryService dictionaryService;
	/**
	 * 导航至主操作页面
	 * 
	 * @Title: index
	 * @Description: TODO(查询所有商品列表)
	 * @param @return 设定文件
	 * @return String 返回类型
	 * @throws
	 */
	/** 导航至主操作页面 */
	@RequestMapping("/index")
	public String index() {
		try {
			log.debug("操作成功！");
			return "/admin/role/list";
		} catch (Exception e) {
		    log.error("导航失败!",e);
			throw new RuntimeException("导航失败!");
		}
	}
   
	@RequiresPermissions("sys:role:view")
	@RequestMapping("/list")
	public String list(Model model,
			@ModelAttribute Roles role,
			@RequestParam(required=false, value="pageNo",defaultValue="") String pageNoStr 
			) {
		
		Pager pager = new Pager();
		if (null != pageNoStr && !pageNoStr.equals("")) {
			pageNoStr = pageNoStr.replace("," , "");
			pager.setPageNo(Integer.parseInt(pageNoStr));
		}
		pager.setCondition(role);// 实体加载在pager中

		int total = roleService.countShopRole(role);// 获取总条数
		List results = roleService.findShopRoleList(pager);// 结果集
		pager.setTotalRows(total);
        pager.setResult(results);
        model.addAttribute("pager",pager);
		// 转发请求到FTL页面
		return "/admin/role/list";

	}

	/** 导航至主添加服装页面 */
	@RequiresPermissions("sys:role:edit")
	@RequestMapping("/add")
	public String add(Model model) {
		model.addAttribute("roleAlias",dictionaryService.findDictionaryByCode("roleAlias"));
		return "admin/role/addRole";
	}

	/**
	 * 编辑或修改用户
	 * @param model
	 * @return
	 */
	@RequiresPermissions("sys:role:edit")
	@RequestMapping(value = "/saveOrUpdate")
	public String saveOrUpdate(
			@ModelAttribute Roles role,
			HttpServletRequest request,
			HttpServletResponse response,
			Model model,
			@RequestParam(required = false, value = "div", defaultValue = "") String div) {
        model.addAttribute("referer", CommonConstants.ADMIN_SERVER+"/role/list");
		if (role.getId() == null) {
//			role.setName(role.getName());
//          role.setCreatetime(role.getCreatetime());
//          role.setRoleAlias(role.getRoleAlias());
			roleService.saveShopRole(role);
            model.addAttribute("msg", "保存成功");
		}else {
			Roles shopRole = roleService.findShopRoleById(role.getId());
			if (shopRole == null) {
                log.warn("没有该角色，Id="+role.getId());
            } else {
				if (StringUtils.isNotBlank(role.getName())) {
					shopRole.setName(role.getName());
				}
				shopRole.setDescription(role.getDescription());
				shopRole.setRoleAlias(role.getRoleAlias());
				roleService.updateState(shopRole);
                model.addAttribute("msg", "修改成功");
			} 
		}
		return Constants.MSG_URL;		
	}
	
	/**
	 * 删除角色
	 * @param
	 * @param model
	 * @param request
	 * @return
	 */
	@RequiresPermissions("sys:role:edit")
	@RequestMapping(value = "/delShopRole")
	public String delShopRole(
			@RequestParam Integer[] ids,
			Model model,
			HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        for(Integer id : ids){
        	roleService.deleteShopRole(id);  
        }	
        model.addAttribute("referer", referer);
        model.addAttribute("msg", "删除成功");
        return Constants.MSG_URL;
	}
	
	/**
	 * 查询一条记录的详细信息
	 * 
	 * @param id
	 * @return
	 */
	@RequiresPermissions("sys:role:view")
	@RequestMapping("/findShopRoleById")
	public String findTestClosesById(@RequestParam(value = "id") int id,
			Model model) {
		try {
			Roles role = roleService.findShopRoleById(id);
			model.addAttribute("role", role);
			return "admin/role/editRole";
		} catch (Exception e) {
			log.error("没有该条记录!",e);
			throw new RuntimeException("没有该条记录!");
		}
	}
	
	/**
	 * 后台校验
	 * @param role
	 * @return
	 */
    @RequestMapping("/validate")
    public @ResponseBody String validateFrom(@ModelAttribute Roles role){
//        Pager pager = new Pager();
//        pager.setCondition(role);
        //校验重复
        if(roleService.findCount(role) > 0){
            return "false";
        }else{
            return "true";
        }
    }
    
    @RequiresPermissions("sys:role:view")
    @RequestMapping("/memberrolelist")
	public String memberrolelist(Model model,
			@ModelAttribute Roles role,
			@RequestParam(required=false, value="pageNo",defaultValue="") String pageNoStr, 
			HttpServletRequest request) {
		
		Pager pager = new Pager();
		if (null != pageNoStr && !pageNoStr.equals("")) {
			pageNoStr = pageNoStr.replace("," , "");
			pager.setPageNo(Integer.parseInt(pageNoStr));
		}
		pager.setCondition(role);// 实体加载在pager中

		int total = roleService.countShopRole(role);// 获取总条数
		List<Roles>  results = roleService.findShopRoleList(pager);// 结果集
		String adminid=request.getParameter("adminid");
		String roleids=request.getParameter("roleids");
		List<Roles> rolelist=new ArrayList<Roles>();
		if(roleids!=null&&!"".equals(roleids)){
			String[] rolestr=roleids.split(",");
			for(int i=0;i<rolestr.length;i++){
				Roles rolet=new Roles();
				rolet.setId(Integer.valueOf(rolestr[i]));
				rolelist.add(rolet);
			}
		}
		log.debug(results.toString());
		pager.setTotalRows(total);
        pager.setResult(results);
        model.addAttribute("pager",pager);
        model.addAttribute("adminid",adminid);
        model.addAttribute("rolelist",rolelist);
		// 转发请求到FTL页面
		return "/admin/role/memberrolelist";

	}
 
	
	 /**
	 * 删除角色
	 * @param
	 * @param model
	 * @param
	 * @return
	 */
    @RequiresPermissions("sys:role:edit")
	 @RequestMapping(value = "/deleteid", method = RequestMethod.POST)
		public @ResponseBody
		Map<String, String> deleteid(@RequestParam(value = "roleid") String roleid,
				Model model) {
			Map<String, String> map = Maps.newHashMap();
			if (StringUtils.isBlank(roleid)) {
				model.addAttribute("result", "ID为空");
				map.put("result", "ID为空");
				map.put(message, "false");
				return map;
			}
			roleService.deleteShopRole(Integer.valueOf(roleid));  
			roleMenuService.deleteRoleMenu(Integer.valueOf(roleid));//同时删除该角色下对应的菜单
			map.put("result", "删除成功");
			map.put(message, "true");
			return map;
		}
}
