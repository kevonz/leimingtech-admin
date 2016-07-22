package com.leimingtech.admin.module.menu.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.leimingtech.core.common.Constants;
import com.leimingtech.core.entity.base.FrontMenu;
import com.leimingtech.service.module.menu.service.FrontMenuService;

/**
 * 前台菜单配置action
 * @author liuk
 */
@Controller
@RequestMapping("/frontMenu")
public class FrontMenuAction {
	
	String message = "success";
	
	@Resource
    private FrontMenuService frontMenuService;
	/**
     * 加载数据页面
     * 不带分页
     */
    @RequestMapping(value = "/list")
    public String list(Model model) {

    	List<FrontMenu> results = frontMenuService.selectParentFrontMenu();//结果集
        model.addAttribute("list", results);//结果集
        //转发请求到FTL页面
        return "/frontMenu/list";
    }
    
    @RequestMapping(value = "/add")
    public String add(Model model,@RequestParam("id") int id) {
        //转发请求到FTL页面
    	System.out.println(id);
    	List<FrontMenu> list = frontMenuService.selectParentFrontMenu();
    	model.addAttribute("list",list);
    	model.addAttribute("pid", id);
        return "/frontMenu/add";
    }
    
    @RequestMapping(value="/save", method = RequestMethod.POST)
    public String save(
    	@ModelAttribute FrontMenu frontMenu,
    	Model model,HttpServletRequest request){
    	if(frontMenu.getParentId()==0){
    		frontMenu.setLevel(1);
    		frontMenu.setPath("0");
    	}else{
    		frontMenu.setLevel(2);
    		frontMenu.setPath("0,"+frontMenu.getParentId());
    	}
    	
    	frontMenuService.save(frontMenu);
    	//adminLogService.save("保存font菜单", request);
    	List<FrontMenu> results = frontMenuService.selectParentFrontMenu();//结果集
        model.addAttribute("list", results);//结果集
        //转发请求到FTL页面
        return "/frontMenu/list";
    }
    
    /**
     * 删除菜单
     *
     * @param ids
     * @param model
     * @return
     */
    @RequestMapping("/delete")
    public String delete(@RequestParam(value = "ids") int[] ids, Model model,HttpServletRequest request) {

        String referer = request.getHeader("Referer");
        model.addAttribute("referer", referer);
        for (int id : ids) {
        	frontMenuService.delete(Integer.valueOf(id));	
        }
        model.addAttribute("msg", "删除成功");
        //adminLogService.save("删除font菜单", request);
        return Constants.MSG_URL;
    }
    
    /**
     * 根据id删除
     * @param id
     * @param model
     * @return
     */
    @RequestMapping(value = "/deleteid", method = RequestMethod.POST)
	public @ResponseBody
	Map<String, String> deleteid(@RequestParam(value = "id") String id,
			Model model) {
		Map<String, String> map = Maps.newHashMap();
		if (StringUtils.isBlank(id)) {
			model.addAttribute("result", "ID为空");
			map.put("result", "ID为空");
			map.put(message, "true");
			return map;
		}
		frontMenuService.delete(Integer.valueOf(id));
		map.put("result", "删除成功");
		map.put(message, "true");
		return map;
	}
    
    /**
     * 校验菜单下是否有子菜单
     * @return
     */
    @RequestMapping(value="/validate", method = RequestMethod.POST)
    public @ResponseBody Boolean validateparentid(@RequestParam("id") int id){
        //校验重复
    	Integer pidcount=frontMenuService.findParentIdCount(id);
        if(pidcount> 0){
            return false;
        }else{
            return true;
        }
    }
    
    /**
     * 查询子列表
     *
     * @param id 父id
     * @return json
     */
    @RequestMapping("child")
    public
    @ResponseBody
    List<FrontMenu> child(@RequestParam int id, @RequestParam int level) {

    	FrontMenu frontMenu = new FrontMenu();
    	frontMenu.setParentId(id);
        //存入deep，配合ajax
        List<FrontMenu> classList = frontMenuService.selectChildrenFrontMenu(frontMenu);
        for (FrontMenu fm : classList) {
        	fm.setLevel(level);
        }
        return classList;
    }
    
    /**
     * 查询所有菜单
     *
     * @param id 父id
     * @return json
     */
    @RequestMapping("/allMenu")
    public
    @ResponseBody
    List<FrontMenu> allMenu(@RequestParam int id, @RequestParam int level) {
        //存入deep，配合ajax
        List<FrontMenu> classList = frontMenuService.selectAllFm();
        return classList;
    }
    
    /**
     * 修改url地址
     *
     * @param id 父id
     * @return json
     */
    @RequestMapping("/editUrl")
    public
    @ResponseBody
    Boolean editUrl(@RequestParam int id, @RequestParam String value,HttpServletRequest request) {
    	FrontMenu frontMenu = new FrontMenu();
    	frontMenu.setId(id);
    	frontMenu.setUrl(value);
    	frontMenuService.update(frontMenu);
    	//adminLogService.save("修改url地址单", request);
        return true;
    }
    
    /**
     * 修改排序
     *
     * @param id 父id
     * @return json
     */
    @RequestMapping("/editSort")
    public
    @ResponseBody
    Boolean editSort(@RequestParam("id") int id, @RequestParam("value") int value,HttpServletRequest request) {
    	FrontMenu frontMenu = new FrontMenu();
    	frontMenu.setId(id);
    	frontMenu.setSort(value);
    	frontMenuService.update(frontMenu);
    	//adminLogService.save("修改前台菜单排序", request);
        return true;
    }
    
}
