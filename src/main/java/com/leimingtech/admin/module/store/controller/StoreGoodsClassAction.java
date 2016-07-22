package com.leimingtech.admin.module.store.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

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
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;
import com.leimingtech.admin.utils.ShowPageUtils;
import com.leimingtech.core.common.Constants;
import com.leimingtech.core.entity.StoreGoodsClass;
import com.leimingtech.core.entity.vo.StoreGoodsClassVo;
import com.leimingtech.service.module.store.service.StoreGoodsClassService;
import com.leimingtech.service.utils.page.Pager;

/**
 * 商户自分类
 * 项目名称：leimingtech-admin 类名称：StoreGoodsClassAction 类描述： 创建人：weiyue 创建时间：2014年12月1日
 * @version
 * 
 */
@Controller
@RequestMapping("/storeGoodsClass")
@Slf4j
public class StoreGoodsClassAction {
	String message = "success";
	@Resource
	private StoreGoodsClassService storeGoodsClassService;

	/**
	 * 店铺自分类页面跳转
	 * 
	 * @Title: index
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @return 设定文件
	 * @return ModelAndView 返回类型
	 * @throws
	 */
	@RequiresPermissions("sys:platformclass:view")
	@RequestMapping(value = "/index")
	public String index(Model model) {
		StoreGoodsClassVo storeGoodsClassVo = new StoreGoodsClassVo();
		storeGoodsClassVo.setStoreId(Constants.PLATFORM_STORE_ID);
		StoreGoodsClass storeGoodsClass1 = new StoreGoodsClass();
		storeGoodsClass1.setStoreId(Constants.PLATFORM_STORE_ID);
		storeGoodsClass1.setStcParentId(0);
		List<StoreGoodsClass> storeGoodsClassListT=storeGoodsClassService.findList(storeGoodsClass1);
		model.addAttribute("storeGoodsClassListT", storeGoodsClassListT);
		return "/platform/storeclass/store-class";
	}

	/**
	 * 店铺自分类
	 * 
	 * @Title: findAll
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @return 设定文件
	 * @return ModelAndView 返回类型
	 * @throws
	 */
	@RequiresPermissions("sys:platformclass:view")
	@RequestMapping(value = "/findAll")
	public ModelAndView findAll() {
		ModelAndView model = new ModelAndView("/platform/storeclass/store-class-list");
		StoreGoodsClassVo storeGoodsClassVo = new StoreGoodsClassVo();
		storeGoodsClassVo.setStoreId(Constants.PLATFORM_STORE_ID);
		List<StoreGoodsClassVo> list = storeGoodsClassService
				.queryClasssList(storeGoodsClassVo);
		model.addObject("datas", list);
		return model;
	}

	/**
	 * 店铺自分类
	 * 
	 * @Title: delete
	 * @Description: 删除方法
	 * @param @return 设定文件
	 * @return ModelAndView 返回类型
	 * @throws
	 */
	@RequiresPermissions("sys:platformclass:edit")
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public @ResponseBody
	Map<String, String> delete(@RequestParam(value = "ids") String ids,
			Model model) {
		Map<String, String> map = Maps.newHashMap();
		// 先验证如数信息是否正确
		if (StringUtils.isBlank(ids)) {
			model.addAttribute("result", "ID为空");
			map.put("result", "ID为空");
			map.put("message", "true");
			return map;
		}
		String[] idArray = StringUtils.split(ids, ",");
		for (String idStr : idArray) {
			List<StoreGoodsClass>  storclasslist=storeGoodsClassService.findChild(Integer.valueOf(idStr));
	    	if(storclasslist.size()>0){
	    		map.put("result", "请删除子节点内容");
				map.put("message", "false");
				return map;
	    	}else{
	    		storeGoodsClassService.deleteByPrimaryKey(Integer.valueOf(idStr));
	    		map.put("result", "删除成功");
				map.put("message","true");
	    	}
		}
		return map;

	}

	/**
	 * 店铺自分类页面跳转
	 * 
	 * @Title: editIndex
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @return 设定文件
	 * @return ModelAndView 返回类型
	 * @throws
	 */
	@RequiresPermissions("sys:platformclass:edit")
	@RequestMapping(value = "/editIndex")
	public ModelAndView editIndex(
			@RequestParam(required = false, value = "stcId", defaultValue = "0") Integer stcId
			) {
//	    Integer scid=CacheUtils.getCacheUser().getStore().getStoreId();
//	    System.out.println("scid:"+scid);
        List<StoreGoodsClass> list = storeGoodsClassService.findParentList(0);
        StoreGoodsClass storeGoodsClass=storeGoodsClassService.selectByPrimaryKey(stcId);
		ModelAndView model = new ModelAndView("/platform/storeclass/store-class-edit");
		model.addObject("datas",list);
		model.addObject("storeGoodsClass",storeGoodsClass);
		model.addObject("stcId", stcId);
		return model;
	}
	
	/**
	 * 店铺自分类页面跳转
	 * 
	 * @Title: editIndex
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @return 设定文件
	 * @return ModelAndView 返回类型
	 * @throws
	 */
	@RequiresPermissions("sys:platformclass:edit")
	@RequestMapping(value = "/addIndex")
	public ModelAndView addIndex(
			@RequestParam(required = false, value = "stcParentId", defaultValue = "0") Integer stcParentId) {
	    //Integer scid=CacheUtils.getCacheUser().getStore().getStoreId();
        List<StoreGoodsClass> list = storeGoodsClassService.findParentList(0);
		ModelAndView model = new ModelAndView("/platform/storeclass/store-class-add");
		model.addObject("selectId", stcParentId);
		model.addObject("datas", list);
		return model;
	}

	/**
	 * 编辑或修改店铺自分类
	 * @return
	 */
	@RequiresPermissions("sys:platformclass:edit")
	@RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST)
	public @ResponseBody
	Map<String, String> saveOrUpdate(
			@RequestParam(required = false, value = "stcId", defaultValue = "0") Integer stcId,
			@RequestParam(required = false, value = "stcParentId", defaultValue = "0") Integer stcParentId,
			@RequestParam(required = false, value = "stcName") String stcName,
			@RequestParam(required = false, value = "stcSort", defaultValue = "0") Integer stcSort,
			@RequestParam(required = false, value = "stcState", defaultValue = "true") Boolean stcState) {
		Map<String, String> map = Maps.newHashMap();
		StoreGoodsClass storeGoodsClass = new StoreGoodsClass();
		storeGoodsClass.setStcId(stcId);
		storeGoodsClass.setStcParentId(stcParentId);
		storeGoodsClass.setStcName(stcName);
		storeGoodsClass.setStcSort(stcSort);
		storeGoodsClass.setStcState(stcState);
        storeGoodsClass.setStoreId(Constants.PLATFORM_STORE_ID);
		if (storeGoodsClass.getStcId() == 0) {
			storeGoodsClass.setCheckState(1);//平台自营默认为审核通过
			storeGoodsClassService.save(storeGoodsClass);
			map.put(message, "true");
		} else {
			 storeGoodsClassService.update(storeGoodsClass);
			 map.put(message, "true");
		}
		// 转发请求到FTL页面
		return map;
	}
	
	 /**
     * 修改特别推荐
     * @param
     */
	@RequiresPermissions("sys:platformclass:edit")
    @RequestMapping("/reupdatestate")
    public @ResponseBody boolean reupdatestate(@RequestParam int stcId,@RequestParam Boolean value){
    	StoreGoodsClass storeGoodsClass = new StoreGoodsClass();
    	storeGoodsClass.setStcId(stcId);
    	storeGoodsClass.setStcState(value);
    	storeGoodsClassService.update(storeGoodsClass);
        return true;
    }
    /**
     * 验证分类名称是否存在
     * @param
     */
    @RequestMapping("/valideclassname")
    public @ResponseBody boolean reupdatestate(@RequestParam String stcName){
    	StoreGoodsClass storeGoodsClass=storeGoodsClassService.findbystcName(stcName);
    	if(storeGoodsClass!=null){
    		 return false;
    	}else{
    		 return true;
    	}
    }
    
    /**
     * 验证父节点下是否存有子节点
     * @param
     */
    @RequestMapping("/validebyparentid")
    public @ResponseBody boolean validebyparentid(@RequestParam Integer stid){
    	List<StoreGoodsClass>  storclasslist=storeGoodsClassService.findChild(stid);
    	if(storclasslist.size()>0){
    		 return false;
    	}else{
    		 return true;
    	}
    }
    
    /**
	 * 
	 * @Title: list 
	 * @Description: TODO(带分页查询list) 
	 * @param @param model
	 * @param @param div
	 * @param @param pageNoStr
	 * @param @param traceMembername
	 * @param @param starttime
	 * @param @param endtime
	 * @param @return    设定文件 
	 * @return String    返回类型 
	 * @throws
	 */
    @RequiresPermissions("sys:platformclass:view")
	@RequestMapping(value = "/list")
	public String list(Model model,
			@RequestParam(required=false, value="pageNo",defaultValue="")String pageNo,
			@ModelAttribute StoreGoodsClass storegoodsClass){
		Pager pager = new Pager();
		if(StringUtils.isNotBlank(pageNo)){
			pager.setPageNo(Integer.parseInt(pageNo));
		}
		storegoodsClass.setCheckState(0);//后台列表查看的都是待审核状态 0待审核，1审核通过，2审核未通过
		pager.setCondition(storegoodsClass);//实体加载在pager中
		pager.setPageSize(5);
		List<StoreGoodsClass> results = storeGoodsClassService.queryList(pager);//结果集
        pager.setResult(results);
        model.addAttribute("pager", pager);//总数
        model.addAttribute("storegoodsClass",storegoodsClass);
		return "/store/storegoodsclass/list";
	}
	
	/**
     * 审核
     * @param id
     * @param model
     * @return
     */
    @RequiresPermissions("sys:platformclass:view")
    @RequestMapping("/remark")
    public String openRemark(@RequestParam Integer stid,Model model){
    	StoreGoodsClass storeGoodsClass=storeGoodsClassService.selectByPrimaryKey(stid);
        model.addAttribute("storeGoodsClass",storeGoodsClass);
        return "/store/storegoodsclass/remark";
    }
    
    @RequiresPermissions("sys:platformclass:edit")
    @RequestMapping(value = "/updateStoregoodclass",produces = "text/xml;charset=UTF-8")
	public @ResponseBody String updateStoregoodclass(
			@ModelAttribute StoreGoodsClass storeGoodsClass) {
		try {
			storeGoodsClassService.updateByPrimaryKeySelective(storeGoodsClass);
		} catch (Exception e) {
			log.debug("店铺自定义审核失败", e);
		}
		return ShowPageUtils.showDialog("操作成功", "reload", "succ", "");
	}
}
