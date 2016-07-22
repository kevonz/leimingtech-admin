package com.leimingtech.admin.module.store.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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
import com.leimingtech.core.common.Constants;
import com.leimingtech.core.common.DateUtils;
import com.leimingtech.core.entity.Classs;
import com.leimingtech.core.entity.GoodsClass;
import com.leimingtech.core.entity.base.Store;
import com.leimingtech.core.entity.base.StoreBindClass;
import com.leimingtech.core.entity.base.StoreGrade;
import com.leimingtech.core.entity.vo.StoreVo;
import com.leimingtech.service.module.goods.service.GoodsClassService;
import com.leimingtech.service.module.store.service.ClasssService;
import com.leimingtech.service.module.store.service.StoreBindClassService;
import com.leimingtech.service.module.store.service.StoreGradeService;
import com.leimingtech.service.module.store.service.StoreService;
import com.leimingtech.service.module.store.vo.ClasssVo;
import com.leimingtech.service.utils.page.Pager;

/**
 * 店铺详情
 *    
 * 项目名称：leimingtech-admin   
 * 类名称：StoreDetailAction   
 * 类描述：   
 * 修改备注：   
 * @version    
 *
 */
@Controller
@RequestMapping("/store/manager")
@Slf4j
public class StoreDetailAction {
	
	String message = "success";
	
//	@Resource
//	private StoreDetailService storeDetailService;
	@Resource
	private ClasssService classsService;
	@Resource
	private StoreGradeService storeGradeService;
    @Resource
    private StoreBindClassService storeBindClassService;
	@Resource
    private GoodsClassService goodsClassService;
	@Resource
	private StoreService storeService;
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
	@RequiresPermissions("sys:store:view")
	@RequestMapping(value = "/list")
	public String list(Model model,
			@RequestParam(required=false, value="pageNo",defaultValue="")String pageNo,
			@ModelAttribute Store store){

		Pager pager = new Pager();
		if(StringUtils.isNotBlank(pageNo)){
			pager.setPageNo(Integer.parseInt(pageNo));
		}
		pager.setCondition(store);//实体加载在pager中
		//log.info("获取总条数【total】"+total);
		List<Store> results = storeService.queryList(pager);//结果集
        pager.setResult(results);
        Pager p = new Pager();
        //店铺等级
        p.setCondition(new StoreGrade());
        List<StoreGrade> gradelist=storeGradeService.queryStoreGradeList(p);
        Map<String,String> grademap=new HashMap<String,String>();
        if(gradelist.size()!=0){
        	  for(StoreGrade storegrade:gradelist){
        		  grademap.put(storegrade.getSgId()+"",storegrade.getSgName());
        	  }
        }
        model.addAttribute("grademap", grademap);//店铺等级
        model.addAttribute("gradeList",storeGradeService.queryStoreGradeList(p));
        model.addAttribute("pager", pager);//总数
        model.addAttribute("store",store);
		return "/store/manager/list";
		
	}
	
	@RequiresPermissions("sys:store:view")
	@RequestMapping(value = "/auditList")
	public String auditList(Model model,
			@RequestParam(required=false, value="pageNo",defaultValue="")String pageNo,
			@ModelAttribute Store store){
		Pager pager = new Pager();
		/**查询条件，放入实体中，**/
		if(StringUtils.isNotBlank(pageNo)){
			pager.setPageNo(Integer.parseInt(pageNo));
		}
		store.setStoreState(2);
		pager.setCondition(store);//实体加载在pager中
		Pager p = new Pager();
        //店铺等级
        p.setCondition(new StoreGrade());
        List<StoreGrade> gradelist=storeGradeService.queryStoreGradeList(p);
        Map<String,String> grademap=new HashMap<String,String>();
        if(gradelist.size()!=0){
        	  for(StoreGrade storegrade:gradelist){
        		  grademap.put(storegrade.getSgId()+"",storegrade.getSgName());
        	  }
        }
		int total  = storeService.queryCount(store);
		log.info("获取总条数【total】"+total);
		List<Store> results = storeService.queryList(pager);//结果集
        pager.setTotalRows(total);
        pager.setResult(results);
        
       
        model.addAttribute("pager",pager);
        model.addAttribute("joinIn",store);
        //店铺等级
        model.addAttribute("grademap", grademap);//店铺等级
        model.addAttribute("gradeList",storeGradeService.queryStoreGradeList(p));
		//转发请求到FTL页面
		return "/store/manager/join_list";
		
	}
	
	
	
	 /**
	  * 
	  * @Title: delDemo 
	  * @Description: TODO(删除记录) 
	  * @param @param ids
	  * @param @param model
	  * @param @return    设定文件 
	  * @return Map<String,String>    返回类型 
	  * @throws
	  */
	@RequiresPermissions("sys:store:edit")
    @RequestMapping(value = "/delLog", method = RequestMethod.POST)
    public @ResponseBody Map<String, String> delLog(
    		@RequestParam(value="ids")String ids, Model model) {
        
    	Map<String, String> map = Maps.newHashMap();
    	
    	if (StringUtils.isBlank(ids)) {
            model.addAttribute("result", "ID为空");
            map.put("result", "ID为空");
            map.put(message, "true");
            return map;
        }
        String[] idArray = StringUtils.split(ids, ",");
        for (String idStr : idArray) {
        	//storeDetailService.delete(Long.parseLong(idStr));
        	storeService.delete(Integer.valueOf(idStr));
        }
        map.put("result", "删除成功");
        map.put(message, "true");
        return map;
    }
    
    
	/**
	 * 
	 * @Title: findForEdit 
	 * @Description: TODO(查找店铺注册信息) 
	 * @param @param model
	 * @param @param id
	 * @param @param div
	 * @param @return    设定文件 
	 * @return String    返回类型 
	 * @throws
	 */
	@RequiresPermissions("sys:store:view")
	@RequestMapping(value = "/findById")
	public String findById(Model model,
			@RequestParam(required=false, value="id",defaultValue="")Integer id){
		Store store=storeService.findById(id);
    	model.addAttribute("storeJoinin",store);
//		Map<String,Object> map=storeService.findByMemberId(id);
//		Store store=map.get("")
//        model.addAttribute("name",map.get("name"));
//        if("10".equals(storeJoinin.getJoininState()) || "11".equals(storeJoinin.getJoininState())){
//            model.addAttribute("title","审核");
//        }else{
//            model.addAttribute("title","查看");
//        }
		return "/store/manager/join_detail";
	}
	
	/**
	 * 
	 * @Title: findForEdit 
	 * @Description: TODO(查找店铺及分类以供编辑使用) 
	 * @param @param model
	 * @param @param id
	 * @param @param div
	 * @param @return    设定文件 
	 * @return String    返回类型 
	 * @throws
	 */
	@RequiresPermissions("sys:store:view")
	@RequestMapping(value = "/findForEdit")
	public String findForEdit(Model model,
			@RequestParam(required=false, value="id",defaultValue="")Integer id){
        Pager pager = new Pager();
		//StoreDetail storeDetail = storeDetailService.findByIdUinonGrade(id);
		StoreVo storevo=storeService.findVoById(id);
        pager.setCondition(new Classs());
		List<ClasssVo> classsList = classsService.queryClasssList(pager);
        pager.setCondition(new StoreGrade());
		List<StoreGrade> gradeList = storeGradeService.queryStoreGradeList(pager);
		model.addAttribute("storeDetail", storevo);
		model.addAttribute("classsList", classsList);
		model.addAttribute("gradeList",gradeList);
		return "/store/manager/edit";
	}
	
	@RequiresPermissions("sys:store:edit")
	@RequestMapping("/updateDetail")
	public String updateDetail(@ModelAttribute Store store,
			@RequestParam(required=false, value="endTimet", defaultValue="") String endTimet,
                               Model model,HttpServletRequest request){
        String referer = request.getHeader("Referer");
        //storeDetailService.updateDetail(detail);
        if(StringUtils.isNotEmpty(endTimet)){
        	store.setEndTime((DateUtils.strToLong(endTimet+" "+"16:06:30")));
		}
        storeService.updateStore(store);
        model.addAttribute("referer", referer);
        model.addAttribute("msg", "编辑成功");
		return Constants.MSG_URL;
	}
	
	 /**
     * 修改特别推荐
     * @param
     */
	@RequiresPermissions("sys:store:edit")
    @RequestMapping("/recommond")
    public @ResponseBody boolean updateRecommond(@RequestParam int id,@RequestParam int value){
    	//StoreDetail detail=new StoreDetail();
    	Store store=new Store();
    	store.setStoreId(id);
    	store.setStoreRecommend(value);
       //storeDetailService.updateDetail(detail);
        storeService.updateStore(store);
        return true;
    }


    /**
     * 审核
     * @param storeJoinin
     * @return
     */
	@RequiresPermissions("sys:store:edit")
    @RequestMapping("/verify")
    public String verify(@ModelAttribute Store store ,Model model,HttpServletRequest request){
    	String referer = request.getHeader("Referer");
    	storeService.updateStore(store);
    	model.addAttribute("referer", referer);
    	System.out.println("msg:"+ Constants.MSG_URL);
        return Constants.MSG_URL;
    }

    /**
     * 经营类目列表
     * @param model
     * @param id
     * @return
     */
	@RequiresPermissions("sys:store:view")
    @RequestMapping("/bindClass")
    public String bindClass(Model model,@RequestParam int id){

        List<GoodsClass> goodsClassList=goodsClassService.findList(0);
        model.addAttribute("classList",goodsClassList);
        List<StoreBindClass> list = storeBindClassService.queryBindClassList(id);
        model.addAttribute("bindList",list);
        model.addAttribute("store",storeService.findById(id));
        return "store/manager/bindClassEdit";
    }

    /**
     * 修改佣金比例
     * @param id
     * @param value
     * @return
     */
	@RequiresPermissions("sys:store:edit")
    @RequestMapping("/updateRate")
    public @ResponseBody Map<String,Object> updateRate(@RequestParam int id,@RequestParam double value){

        Map<String,Object> map = Maps.newHashMap();
        StoreBindClass storeBindClass = new StoreBindClass();
        storeBindClass.setBid(id);
        storeBindClass.setCommisRate(value);
        storeBindClassService.updateRate(storeBindClass);
        map.put("result",true);
        return map;
    }

    /**
     * 删除
     * @param id
     * @return
     */
	@RequiresPermissions("sys:store:edit")
    @RequestMapping("/deleteBind")
    public @ResponseBody Map<String,Object> deleteBind(@RequestParam int id){
        Map<String,Object> map = Maps.newHashMap();
        if(id == 0){
            map.put("result",false);
            map.put("message","条目异常");
        }else {
            storeBindClassService.deleteBind(id);
            map.put("result", true);
        }
        return map;
    }

    /**
     * 新增
     * @return
     */
	@RequiresPermissions("sys:store:edit")
    @RequestMapping("/saveBind")
    public String saveBind(@ModelAttribute StoreBindClass storeBindClass,
                           @RequestParam String goodsClass,Model model,HttpServletRequest request){

        String referer = request.getHeader("Referer");
        model.addAttribute("referer", referer);
        String result = storeBindClassService.saveBind(storeBindClass, goodsClass);
        model.addAttribute("msg", result);
        return Constants.MSG_URL;
    }
}