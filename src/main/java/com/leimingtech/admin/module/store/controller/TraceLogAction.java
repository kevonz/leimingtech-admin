package com.leimingtech.admin.module.store.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
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
import com.leimingtech.core.entity.base.StoreSnsComment;
import com.leimingtech.core.entity.base.TraceLog;
import com.leimingtech.service.module.store.service.StoreSnsCommentService;
import com.leimingtech.service.module.store.service.TraceLogService;
import com.leimingtech.service.utils.page.Pager;

/**
 * 项目名称：leimingtech-admin
 * 类名称：TraceLogAction
 * 类描述：
 * 创建人：yanghui
 * 创建时间：2014年11月7日 下午2:00:53
 * 修改人：yanghui
 * 修改时间：2014年11月7日 下午2:00:53
 * 修改备注：
 */
@Controller
@RequestMapping("/store/tracelog")
@Slf4j
public class TraceLogAction {

    String message = "success";

    @Autowired
    private TraceLogService traceLogService;
    @Autowired
    private StoreSnsCommentService commentService;

    /**
     * @param @param  model
     * @param @param  div
     * @param @param  pageNoStr
     * @param @param  traceMembername
     * @param @param  starttime
     * @param @param  endtime
     * @param @return 设定文件
     * @return String    返回类型
     * @throws
     * @Title: list
     * @Description: TODO(带分页查询list)
     */
    @RequiresPermissions("sys:storeynamic:view")
    @RequestMapping(value = "/list")
    public String list(Model model,
                       @RequestParam(required = false, value = "pageNo", defaultValue = "") String pageNo,
                       @ModelAttribute TraceLog traceLog) {
        Pager pager = new Pager();
//		if(searchType.equals("0")){
        /**查询条件，放入实体中，**/
        if (StringUtils.isNotBlank(pageNo)) {
            pager.setPageNo(Integer.parseInt(pageNo));
        }
        pager.setCondition(traceLog);//实体加载在pager中

        int total = traceLogService.countTraceLog(pager);//获取总条数
        log.info("获取总条数【total】" + total);
        List<TraceLog> results = traceLogService.queryTraceLogList(pager);//结果集
        pager.setTotalRows(total);
        pager.setResult(results);
        model.addAttribute("traceLog", traceLog);
        model.addAttribute("pager", pager);
        //转发请求到FTL页面
        return "/store/strace/list";
    }


    /**
     * @param @param  ids
     * @param @param  model
     * @param @return 设定文件
     * @return Map<String,String>    返回类型
     * @throws
     * @Title: delDemo
     * @Description: TODO(删除动态)
     */
    @RequiresPermissions("sys:storeynamic:edit")
    @RequestMapping(value = "/delLog", method = RequestMethod.POST)
    public String delLog(
            @RequestParam(value = "ids") String ids, Model model,HttpServletRequest request) {

        String referer = request.getHeader("Referer");
        model.addAttribute("referer", referer);
        if (StringUtils.isBlank(ids)) {
            model.addAttribute("msg", "删除失败");
        }
        String[] idArray = StringUtils.split(ids, ",");
        for (String idStr : idArray) {
            traceLogService.delete(Integer.parseInt(idStr));
        }
        model.addAttribute("msg", "删除成功");
        return Constants.MSG_URL;
    }

    /**
     * @param @param  ids
     * @param @param  model
     * @param @return 设定文件
     * @return Map<String,String>    返回类型
     * @throws
     * @Title: delComment
     * @Description: TODO(删除评论)
     */
    @RequiresPermissions("sys:storeynamic:edit")
    @RequestMapping(value = "/delComment", method = RequestMethod.POST)
    public
    @ResponseBody
    Map<String, String> delComment(
            @RequestParam(value = "ids") String ids, Model model) {

        Map<String, String> map = Maps.newHashMap();

        if (StringUtils.isBlank(ids)) {
            model.addAttribute("result", "ID为空");
            map.put("result", "ID为空");
            map.put(message, "true");
            return map;
        }
        String[] idArray = StringUtils.split(ids, ",");
        for (String idStr : idArray) {
            commentService.delete(Integer.parseInt(idStr));
        }
        map.put("result", "删除成功");
        map.put(message, "true");
        return map;
    }

    /**
     * @param @param  ids
     * @param @param  state
     * @param @param  model
     * @param @return 设定文件
     * @return Map<String,String>    返回类型
     * @throws
     * @Title: updateState
     * @Description: TODO(更新动态显示状态)
     */
    @RequiresPermissions("sys:storeynamic:edit")
    @RequestMapping("/updateState")
    public String updateState(
            @RequestParam(value = "ids") String ids,
            @RequestParam(value = "state") String state, Model model
            ,HttpServletRequest request) {

        String referer = request.getHeader("Referer");
        model.addAttribute("referer", referer);
        if (StringUtils.isBlank(ids)) {
            model.addAttribute("msg", "编辑失败");
        }
        String[] idArray = StringUtils.split(ids, ",");
        for (String idStr : idArray) {
            traceLogService.updateStateById(Integer.parseInt(idStr), Integer.parseInt(state));
        }
        model.addAttribute("msg", "编辑成功");
        return Constants.MSG_URL;
    }

    /**
     * @param @param  ids
     * @param @param  state
     * @param @param  model
     * @param @return 设定文件
     * @return Map<String,String>    返回类型
     * @throws
     * @Title: updateCommentState
     * @Description: TODO(更新评论显示状态)
     */
    @RequiresPermissions("sys:storeynamic:edit")
    @RequestMapping(value = "/updateCommentState", method = RequestMethod.POST)
    public
    @ResponseBody
    Map<String, String> updateCommentState(
            @RequestParam(value = "ids") String ids, @RequestParam(value = "state") String state, Model model) {

        Map<String, String> map = Maps.newHashMap();

        if (StringUtils.isBlank(ids)) {
            model.addAttribute("result", "ID为空");
            map.put("result", "ID为空");
            map.put(message, "true");
            return map;
        }
        String[] idArray = StringUtils.split(ids, ",");
        for (String idStr : idArray) {
            commentService.updateStateById(Integer.parseInt(idStr), Integer.parseInt(state));
        }
        map.put("result", "删除成功");
        map.put(message, "true");
        return map;
    }

    /**
     * @param @param  model
     * @param @param  id
     * @param @param  div
     * @param @return 设定文件
     * @return String    返回类型
     * @throws
     * @Title: findById
     * @Description: TODO(查询单条记录)
     */
    @RequiresPermissions("sys:storeynamic:view")
    @RequestMapping(value = "/findById")
    public String findById(Model model,
                           @RequestParam(required = false, value = "id", defaultValue = "") Integer id,
                           @RequestParam(required = false, value = "div", defaultValue = "") String div) {
        TraceLog traceLog = traceLogService.findLogById(id);
        model.addAttribute("traceLog", traceLog);
        return "/store/tracelogDetail";
    }

    /**
     * @return
     */
    @RequiresPermissions("sys:storeynamic:view")
    @RequestMapping("/commentList")
    public String commentList(Model model,
                              @ModelAttribute StoreSnsComment comment,
                              @RequestParam(required = false, value = "pageNo", defaultValue = "") String pageNo) {

        Pager pager = new Pager();
        /**查询条件，放入实体中，**/
        if (StringUtils.isNotBlank(pageNo)) {
            pager.setPageNo(Integer.parseInt(pageNo));
        }
        pager.setCondition(comment);//实体加载在pager中

        int total = commentService.countComment(pager);//获取总条数
        log.info("获取总条数【total】" + total);
        List<StoreSnsComment> results = commentService.queryCommentList(pager);//结果集
        pager.setTotalRows(total);
        pager.setResult(results);
        model.addAttribute("comment", comment);
        model.addAttribute("pager", pager);
        //转发请求到FTL页面
        return "/store/storeSnsCommentList";
    }
}