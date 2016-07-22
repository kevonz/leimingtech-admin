package com.leimingtech.admin.module.store.controller;

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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.leimingtech.core.common.Constants;
import com.leimingtech.core.entity.Classs;
import com.leimingtech.core.entity.GoodsClass;
import com.leimingtech.service.module.store.service.ClasssService;

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
@RequestMapping("/store/classs")
public class ClasssAction {

    String message = "success";

    @Resource
    private ClasssService classsService;

    /**
     * @return String    返回类型
     * @throws
     * @Title: addIndex
     * @Description: TODO(进入增加的方法)
     */
    @RequiresPermissions("sys:store:view")
    @RequestMapping("/addIndex")
    public String addIndex(@ModelAttribute Classs classs, Model model) {

        model.addAttribute("parentId",classs.getParentId());
        classs.setParentId(0);
        List<Classs> results = classsService.queryClasssChildrenList(classs);
        model.addAttribute("ParentList", results);//父节点结果集
        return "/store/class/save";
    }


    /**
     * @param @return /store/calss/edit.flt
     * @return String    返回类型
     * @throws
     * @Title: updateIndex
     * @Description: TODO(进入增加的方法)
     */
    @RequiresPermissions("sys:store:view")
    @RequestMapping("/updateIndex")
    public String updateIndex(@ModelAttribute Classs classs, Model model) {

        model.addAttribute("classsResult", classsService.queryById(classs.getId()));
        return "/store/class/edit";
    }

    /**
     * 加载数据页面
     * 不带分页
     */
    @RequiresPermissions("sys:store:view")
    @RequestMapping(value = "/list")
    public String list(Model model) {

        List<Classs> results = classsService.queryClasssParentList();//结果集
        model.addAttribute("list", results);//结果集
        //转发请求到FTL页面
        return "/store/class/list";

    }


    /**
     * 编辑或修改用户
     *
     * @param classs
     * @param model
     * @return
     */
    @RequiresPermissions("sys:store:edit")
    @RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST)
    public String saveOrUpdate(@ModelAttribute Classs classs, Model model, HttpServletRequest request) {

        String referer = request.getHeader("Referer");
        model.addAttribute("referer", referer);
        if (classs.getId() == null) {
            classsService.save(classs);
            model.addAttribute("msg", "新增成功");
        } else {
            classsService.update(classs);
            model.addAttribute("msg", "修改成功");
        }
        //转发请求到FTL页面
        return Constants.MSG_URL;
    }


    /**
     * 删除用户
     *
     * @param ids
     * @param model
     * @return
     */
    @RequiresPermissions("sys:store:edit")
    @RequestMapping("/delete")
    public String delete(@RequestParam(value = "ids") long[] ids, Model model,HttpServletRequest request) {

        String referer = request.getHeader("Referer");
        model.addAttribute("referer", referer);
        for (long id : ids) {
            classsService.delete(id);
        }
        model.addAttribute("msg", "删除成功");
        return Constants.MSG_URL;
    }

    /**
     * 查询子列表
     *
     * @param id 父id
     * @return json
     */
    @RequiresPermissions("sys:store:view")
    @RequestMapping("child")
    public
    @ResponseBody
    List<Classs> child(@RequestParam int id, @RequestParam int level) {

        Classs classs = new Classs();
        classs.setParentId(id);
        //存入deep，配合ajax
        List<Classs> classList = classsService.queryClasssChildrenList(classs);
        for (Classs c : classList) {
            c.setDeep(level);
        }
        return classList;
    }

    /**
     * 修改排序
     *
     * @return
     */
    @RequiresPermissions("sys:store:edit")
    @RequestMapping("/modifySort")
    public
    @ResponseBody
    Boolean modifySort(@RequestParam int id, @RequestParam Integer value) {

        Classs c = new Classs();
        c.setId(id);
        c.setSort(value);
        classsService.update(c);
        return true;
    }

    /**
     * 修改分类名称
     *
     * @param id
     * @param value
     * @return
     */
    @RequiresPermissions("sys:store:edit")
    @RequestMapping("/modifyName")
    public
    @ResponseBody
    Boolean modifyName(@RequestParam int id, @RequestParam String value) {

        Classs c = new Classs();
        c.setId(id);
        c.setName(value);
        //判断是否有重复名称
        if (classsService.findCount(c) > 0) {
            return false;
        } else {
            //执行修改操作
            classsService.update(c);
            return true;
        }
    }

    /**
     * 校验重复
     *
     * @return
     */
    @RequiresPermissions("sys:store:view")
    @RequestMapping("/validate")
    public
    @ResponseBody
    boolean validate(@ModelAttribute Classs classs) {

        if (classsService.findCount(classs) > 0) {
            return false;
        } else {
            return true;
        }
    }
    
    /**
     * 校验菜单下是否有子菜单
     * @return
     */
    @RequiresPermissions("sys:store:view")
    @RequestMapping("/validateparentid")
    public @ResponseBody Boolean validateparentid(@RequestParam int id){
        //校验重复
    	List<Classs> claslist=classsService.findList(id);
        if(claslist.size()> 0){
            return false;
        }else{
            return true;
        }
    }
    
    @RequiresPermissions("sys:store:edit")
    @RequestMapping(value = "/deleteid", method = RequestMethod.POST)
    public @ResponseBody Map<String, String> deleteid(@RequestParam(value = "classid") String classid,
 			Model model) {
 		Map<String, String> map = Maps.newHashMap();
 		if (StringUtils.isBlank(classid)) {
 			model.addAttribute("result", "ID为空");
 			map.put("result", "ID为空");
 			map.put(message, "true");
 			return map;
 		}
 		classsService.delete(Long.valueOf(classid));
 		map.put("result", "删除成功");
 		map.put(message, "true");
 		return map;
 	}
}