package com.leimingtech.admin.module.admin.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.leimingtech.core.common.Constants;
import com.leimingtech.core.common.Digests;
import com.leimingtech.core.entity.base.Admin;
import com.leimingtech.core.entity.base.Roles;
import com.leimingtech.core.entity.vo.AdminVo;
import com.leimingtech.service.module.admin.service.AdminService;
import com.leimingtech.service.module.admin.service.RoleService;
import com.leimingtech.service.utils.page.Pager;

/**
 *    
 * 项目名称：leimingtech-admin   
 * 类名称：AdminAction   
 * 类描述：会员管理功能实现类
 * 修改备注：   
 * @version V1.0   
 *
 */
@Controller
@RequestMapping("/setting/admin")
@Slf4j
public class AdminAction {

	String message = "success";

	@Autowired
	private AdminService adminService;
	@Resource
	private RoleService roleService;
	
	/** 导航至主操作页面 */
	@RequestMapping("/index")
	public String index() {
		try {
			log.debug("操作成功！");
			return "/setting/admin/index";
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("导航失败!");
		}
	}
	
	/** 导航至主操作页面 */
	@RequestMapping("/add")
	public String add(Model model){
		try {
			log.debug("操作成功！");
			List<Roles> rolelist=roleService.findList();
			model.addAttribute("rolelist",rolelist);
			return "/setting/admin/addAdmin";
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("导航失败!");
		}
	}

	/**
	 * 
	 * @Title: list
	 * @Description: TODO (查询方法)
	 * @param @param model
	 * @param @param div
	 * @param @param pageNoStr
	 * @param @param acctName
	 * @param @param certifyClass
	 * @param @return    设定文件
	 * @return String    返回类型
	 * @throws
	 */
	@RequiresPermissions("sys:admin:view")
	@RequestMapping(value = "/list")
	public String list(
			Model model,
			@RequestParam(required = false, value = "div", defaultValue = "") String div,
			@RequestParam(required = false, value = "pageNo", defaultValue = "") String pageNoStr) {
		Pager pager = new Pager();

		if (null != pageNoStr && !pageNoStr.equals("")) {
			pageNoStr = pageNoStr.replace("," , "");
			pager.setPageNo(Integer.parseInt(pageNoStr));
		}

//		int total = adminService.findAdminCount(pager);// 获取总条数
		List<AdminVo> results = adminService.findAdminList(pager);// 结果集
		model.addAttribute("datas", results);// 结果集
		model.addAttribute("pageNo", pager.getPageNo());// 当前页
		model.addAttribute("pageSize", pager.getPageSize());// 每页显示条数
		model.addAttribute("recordCount", pager.getTotalRows());// 总数
		model.addAttribute("toUrl", "setting/admin/list");// 跳转URL
		model.addAttribute("div", div);// 显示的DIV数据区域
		// 转发请求到FTL页面
		return "/setting/admin/list";

	}
	
	/**
	 * 编辑或修改用户
	 * 
	 * @param
	 * @param model
	 * @return
	 */
	@RequiresPermissions("sys:admin:edit")
	@RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST)
	public String saveOrUpdate(
			@ModelAttribute Admin admin,
			Model model,
			@RequestParam(required = false, value = "div", defaultValue = "") String div) {
		Map<String, String> map = Maps.newHashMap();
		
		if (admin.getAdminId() == null) {
			admin.setAdminPassword(Digests.entryptPassword(admin.getAdminPassword()));
			adminService.save(admin);
		} else {
			Admin dbAdmin = adminService.findAdminById(admin.getAdminId());
			if (dbAdmin == null) {
				map.put(message, "false");
				map.put("msg", "获取对象为空！");
			} else {
				dbAdmin.setRoleid(admin.getRoleid());
				dbAdmin.setAdminGid(admin.getAdminGid());
				dbAdmin.setAdminPassword(Digests.entryptPassword(admin.getAdminPassword()));
				adminService.update(dbAdmin);
				map.put(message, "true");
			} 
		}
		return "redirect:/setting/admin/index";
		
	}
	
	
	/**
	 * 查询单挑记录
	 * @Title: findById
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param model
	 * @param @param id
	 * @param @param div
	 * @param @return    设定文件
	 * @return String    返回类型
	 * @throws
	 */
	@RequiresPermissions("sys:admin:view")
	@RequestMapping(value = "/findById")
	public String findById(
			Model model,
			@RequestParam(required = false, value = "id", defaultValue = "") int id,
			@RequestParam(required = false, value = "div", defaultValue = "") String div) {
			Admin admin = adminService.findAdminById(Integer.valueOf(id));
			List<Roles> rolelist=roleService.findList();
			model.addAttribute("rolelist",rolelist);
			model.addAttribute("admin", admin);
			model.addAttribute("div", div);
			return "/setting/admin/editAdmin";
	}



	
	/**
	 * 
	 * @Title: delAdmin
	 * @Description: TODO (删除会员)
	 * @param @param ids
	 * @param @param model
	 * @param @return  设定文件
	 * @return Map<String,String>  返回类型
	 * @throws
	 */
	/*@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public @ResponseBody
	Map<String, String> delAdmin(@RequestParam(value = "ids") String ids,
			Model model) {
		Map<String, String> map = Maps.newHashMap();
		if (StringUtils.isBlank(ids)) {
			model.addAttribute("result", "ID为空");
			map.put("result", "ID为空");
			map.put(message, "true");
			return map;
		}
		String[] idArray = StringUtils.split(ids, ",");
		for (String idStr : idArray) {
			adminService.delete(Integer.parseInt(idStr));
		}
		map.put("result", "删除成功");
		map.put(message, "true");
		return map;
	}*/
	
	
	/**
	 * 
	 * @Title: delAdmin
	 * @Description: TODO (删除会员)
	 * @param @param ids
	 * @param @param model
	 * @param @return  设定文件
	 * @return Map<String,String>  返回类型
	 * @throws
	 */
	@RequiresPermissions("sys:admin:edit")
    @RequestMapping("/delete")
    public String  delete(@RequestParam int[] ids,HttpServletRequest request,Model model){

        String referer = request.getHeader("Referer");
        for(int id : ids){
        	adminService.delete(id);
        }
        model.addAttribute("referer", referer);
        model.addAttribute("msg", "删除成功");
//        adminLogService.save("删除后台菜单", request);
        return Constants.MSG_URL;
    }
	 
	@RequiresPermissions("sys:admin:edit")
    @RequestMapping(value = "/deleteid", method = RequestMethod.POST)
	public @ResponseBody
	Map<String, String> deleteid(@RequestParam(value = "adminid") String adminid,
			Model model) {
		Map<String, String> map = Maps.newHashMap();
		if (StringUtils.isBlank(adminid)) {
			model.addAttribute("result", "ID为空");
			map.put("result", "ID为空");
			map.put(message, "false");
			return map;
		}
		adminService.delete(Integer.parseInt(adminid));
		map.put("result", "删除成功");
		map.put(message, "true");
//	    adminLogService.save("删除后台菜单", request);
		return map;
	}


}