package com.leimingtech.admin.module.admin.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.leimingtech.core.common.Constants;
import com.leimingtech.core.entity.base.RoleMenu;
import com.leimingtech.service.module.admin.service.AdminService;
import com.leimingtech.service.module.admin.service.RoleMenuService;
import com.leimingtech.service.utils.page.Pager;

@Controller
@RequestMapping("/roleMenu")
@Slf4j
public class RoleMenuAction {

	String message = "success";

	@Resource
	private RoleMenuService roleMenuService;
	@Resource
	private AdminService adminService;
	@Autowired  
    private  HttpServletRequest request;  
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
   
	@RequestMapping("/list")
	public String list(Model model,
			@ModelAttribute RoleMenu roleMenu,
			@RequestParam(required=false, value="pageNo",defaultValue="") String pageNoStr 
			) {
		
		Pager pager = new Pager();
		if (null != pageNoStr && !pageNoStr.equals("")) {
			pageNoStr = pageNoStr.replace("," , "");
			pager.setPageNo(Integer.parseInt(pageNoStr));
		}
		pager.setCondition(roleMenu);// 实体加载在pager中

		int total = roleMenuService.countRoleMenu(roleMenu);// 获取总条数
		List  results = roleMenuService.findRoleMenuList(pager);// 结果集
		log.debug(results.toString());
		pager.setTotalRows(total);
        pager.setResult(results);
        model.addAttribute("pager",pager);
		// 转发请求到FTL页面
		return "/admin/role/list";

	}

	/** 导航至主添加服装页面 */
	@RequestMapping("/add")
	public String add() {
		return "admin/role/addRole";
	}

	/**
	 * 编辑或修改用户
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/saveOrUpdate")
	public String saveOrUpdate(
			@ModelAttribute RoleMenu roleMenu,
			HttpServletResponse response,
			Model model,
			@RequestParam(required = false, value = "div", defaultValue = "") String div) {
        String referer = request.getHeader("Referer");
        model.addAttribute("referer", referer);
        String roleid=request.getParameter("roleid");
        //获得当前用户的注册信息
        if(roleid!=null&&!"".equals(roleid)){
	        String[] rolestrid=roleid.split(",");
	        for(String roleidt:rolestrid){
	        	roleMenuService.deleteRoleMenu(Integer.valueOf(roleidt));
	        }
        }
        String menuids=request.getParameter("menuids");
        if(menuids!=null&&!"".equals(menuids)){
        String[] str=menuids.split(",");
				for(String smenu:str){
					roleMenu.setRoleId(Integer.valueOf(roleid));
					roleMenu.setMenuId(Integer.valueOf(smenu));
					roleMenuService.saveRoleMenu(roleMenu);
		   }			
            model.addAttribute("msg", "保存成功");
//            adminLogService.save("保存角色与菜单的关联信息", request);
		}
//        else {
//			RoleMenu UPRoleMenu = roleMenuService.findRoleMenuById(roleMenu.getRoleId());
//			if (UPRoleMenu == null) {
//                log.warn("没有该角色，Id="+roleMenu.getRoleId());
//            } else {
//				UPRoleMenu.setRoleId(roleMenu.getRoleId());
//				UPRoleMenu.setMenuId(roleMenu.getMenuId());
//				roleMenuService.updateState(UPRoleMenu);
//                model.addAttribute("msg", "修改成功");
//			} 
//		}
		return Constants.MSG_URL;		
	}
	
	/**
	 * 删除角色
	 * @param ids
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/delRoleMenu")
	public String delShopRole(
			@RequestParam Integer[] ids,
			Model model,
			HttpServletRequest request) {
		

        String referer = request.getHeader("Referer");
        
        for(Integer id : ids){
        	roleMenuService.deleteRoleMenu(id);  
        }	
        model.addAttribute("referer", referer);
        model.addAttribute("msg", "删除成功");
//        adminLogService.save("删除角色与菜单的关联信息", request);
        return Constants.MSG_URL;
	}
	
	/**
	 * 查询一条记录的详细信息
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/findRoleMenuById")
	public String findTestClosesById(@RequestParam(value = "id") int id,
			Model model) {
		try {
			RoleMenu role = roleMenuService.findRoleMenuById(id);
			model.addAttribute("role", role);
			return "admin/role/editRoleMenu";
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
    public @ResponseBody String validateFrom(@ModelAttribute RoleMenu role){

//        Pager pager = new Pager();
//        pager.setCondition(role);
        //校验重复
        if(roleMenuService.findCount(role) > 0){
            return "false";
        }else{
            return "true";
        }
    }
    
    
   
}
