package com.leimingtech.admin.module.points.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.leimingtech.core.common.DateUtils;
import com.leimingtech.core.entity.Area;
import com.leimingtech.core.entity.GoodsAttribute;
import com.leimingtech.core.entity.GoodsClass;
import com.leimingtech.core.entity.GoodsSpec;
import com.leimingtech.core.entity.base.Brand;
import com.leimingtech.core.entity.base.Goods;
import com.leimingtech.core.entity.base.PointsGoods;
import com.leimingtech.core.entity.base.Spec;
import com.leimingtech.core.entity.base.Store;
import com.leimingtech.core.entity.base.StoreGrade;
import com.leimingtech.core.entity.vo.GoodsTypeVO;
import com.leimingtech.core.entity.vo.SpecVo;
import com.leimingtech.core.jackson.JsonUtils;
import com.leimingtech.core.state.goods.GoodsState;
import com.leimingtech.service.module.area.service.AreaService;
import com.leimingtech.service.module.goods.service.BrandService;
import com.leimingtech.service.module.goods.service.GoodsClassService;
import com.leimingtech.service.module.goods.service.GoodsService;
import com.leimingtech.service.module.goods.service.GoodsSpecService;
import com.leimingtech.service.module.goods.service.GoodsTypeService;
import com.leimingtech.service.module.points.service.PointsGoodsService;
import com.leimingtech.service.module.store.service.StoreGoodsClassService;
import com.leimingtech.service.module.store.service.StoreGradeService;
import com.leimingtech.service.module.store.service.StoreService;
import com.leimingtech.service.utils.page.Pager;

/**
 * 积分商城action
 * @author cgl
 * 2015年08月26日16:43:10
 */
@Controller
@RequestMapping("/points_")
public class PointsAction {

	@Autowired
	PointsGoodsService pointsGoodsService;
	
	@Autowired
	private StoreService storeService;
	
//	@Autowired
//	private StoreDetailService storeDetailService;
//	
	@Autowired
	private StoreGradeService storeGradeService;
	
	@Autowired
	private GoodsService goodsService;
	
	@Autowired
	private BrandService brandService;
	
	@Autowired
	private GoodsClassService goodsClassService;
	
	@Autowired
	private GoodsSpecService goodsSpecService;
	
	@Autowired
	private StoreGoodsClassService storeGoodsClassService;
	
	@Resource
	private GoodsTypeService goodsTypeService;
	
	@Autowired
	private AreaService areaService;
	/**
	 * 跳转至管理积分商品主页面
	 */
	@RequestMapping("/all/list")
	public String allList(PointsGoods pointsGoodsCondition, Model model, @RequestParam(required=false, value="pageNo",defaultValue="")String pageNo){
		
		/**创建查询条件*/
		
		Pager pager = new Pager();
		
		if(StringUtils.isNotBlank(pageNo)){
			pager.setPageNo(Integer.parseInt(pageNo));
		}
		
		pager.setCondition(pointsGoodsCondition);
		
		//int total = pointsGoodsService.findPointsGoodsPagerListCount(pager);

		List<PointsGoods> results = pointsGoodsService.findPointsGoodsPagerList(pager);// 结果集

		// 页面查询条件品牌列表
        pager.setResult(results);
        //pager.setTotalRows(total);
        model.addAttribute("pager", pager);//总数
        model.addAttribute("pointsGoods",pointsGoodsCondition);
        model.addAttribute("brandList",brandService.findList());
        model.addAttribute("classList",goodsClassService.findList(0));
        model.addAttribute("toURL", "/points/sell/list");
		
		return "/points/all-list";
	}
	
	/**
	 * 跳转至管理积分商品上架中主页面
	 */
	@RequestMapping("/sell/list")
	public String sellList(PointsGoods pointsGoodsCondition, Model model, @RequestParam(required=false, value="pageNo",defaultValue="")String pageNo){
		
		/**创建查询条件*/
		
		Pager pager = new Pager();
		
		pointsGoodsCondition.setPointsGoodsShow(GoodsState.GOODS_ON_SHOW);
		
		if(StringUtils.isNotBlank(pageNo)){
			pager.setPageNo(Integer.parseInt(pageNo));
		}
		
		pager.setCondition(pointsGoodsCondition);
		
		//int total = pointsGoodsService.findPointsGoodsPagerListCount(pager);
		
		List<PointsGoods> results = pointsGoodsService.findPointsGoodsPagerList(pager);// 结果集
		
		// 页面查询条件品牌列表
		pager.setResult(results);
		//pager.setTotalRows(total);
		model.addAttribute("pager", pager);//总数
		model.addAttribute("pointsGoods",pointsGoodsCondition);
		model.addAttribute("brandList",brandService.findList());
		model.addAttribute("classList",goodsClassService.findList(0));
		model.addAttribute("toURL", "/points/sell/list");
		
		return "/points/sell-list";
	}
	
	/**
	 * 跳转至管理积分商品下架中主页面
	 */
	@RequestMapping("/store/list")
	public String storeList(PointsGoods pointsGoodsCondition, Model model, @RequestParam(required=false, value="pageNo",defaultValue="")String pageNo){
		
		/**创建查询条件*/
		
		Pager pager = new Pager();
		
		pointsGoodsCondition.setPointsGoodsShow(GoodsState.GOODS_OFF_SHOW);
		
		if(StringUtils.isNotBlank(pageNo)){
			pager.setPageNo(Integer.parseInt(pageNo));
		}
		
		pager.setCondition(pointsGoodsCondition);
		
		//int total = pointsGoodsService.findPointsGoodsPagerListCount(pager);
		
		List<PointsGoods> results = pointsGoodsService.findPointsGoodsPagerList(pager);// 结果集
		
		// 页面查询条件品牌列表
		pager.setResult(results);
		//pager.setTotalRows(total);
		model.addAttribute("pager", pager);//总数
		model.addAttribute("pointsGoods",pointsGoodsCondition);
		model.addAttribute("brandList",brandService.findList());
		model.addAttribute("classList",goodsClassService.findList(0));
		model.addAttribute("toURL", "/points/sell/list");
		
		return "/points/store-list";
	}
	
	
	/**
	 * 跳转至发布积分商品的主页面
	 */
	@RequestMapping("/pro/index")
	public String productIndex(
			Model model,
			@RequestParam(required=false, value="pageNo",defaultValue="")String pageNo,
			@ModelAttribute Store store){

		Pager pager = new Pager();
		if(StringUtils.isNotBlank(pageNo)){
			pager.setPageNo(Integer.parseInt(pageNo));
		}
		pager.setCondition(store);//实体加载在pager中
//		int total  = storeDetailService.countStoreDetail(pager);//获取总条数
//		List<StoreDetail> results = storeDetailService.queryStoreDetailList(pager);//结果集
//		int total =storeService.queryCount(store);
	    List<Store> results =storeService.queryList(pager);
        pager.setResult(results);
//        pager.setTotalRows(total);
        Pager p = new Pager();
        //店铺等级
        p.setCondition(new StoreGrade());
        model.addAttribute("gradeList",storeGradeService.queryStoreGradeList(p));
        model.addAttribute("pager", pager);//总数
        model.addAttribute("store",store);
		model.addAttribute("toURL", "/points/pro/index");
		return "/points/pro-index";
	}
	
	/**
	 * 选择店铺下的商品
	 */
	@RequestMapping("/pro/selectGoods")
	public String selectGoods(Goods goodsCondition, Model model, @RequestParam(required=false, value="pageNo",defaultValue="")String pageNo){
		
		/**创建查询条件*/
		
		Pager pager = new Pager();
		
		goodsCondition.setGoodsShow(GoodsState.GOODS_ON_SHOW);
		
		if(StringUtils.isNotBlank(pageNo)){
			pager.setPageNo(Integer.parseInt(pageNo));
		}
		
		pager.setCondition(goodsCondition);
		
		//int total = goodsService.findGoodPagerListCount(pager);// 获取总条数

		List<Goods> results = goodsService.findGoodPagerList(pager);// 结果集

		// 页面查询条件品牌列表
        pager.setResult(results);
        //pager.setTotalRows(total);
        model.addAttribute("pager", pager);//总数
        model.addAttribute("goods",goodsCondition);
        model.addAttribute("brandList",brandService.findList());
        model.addAttribute("classList",goodsClassService.findList(0));
        model.addAttribute("toURL", "/points/pro/selectGoods");
        
		/**获得店铺信息*/
		Store store = storeService.findById(goodsCondition.getStoreId());
		model.addAttribute("store", store);
		
		return "/points/pro-select-goods";
	}
	
	
	/**
	 * 选择规格生成相应的积分商品
	 */
	@RequestMapping("/pro/selectSpec")
	public String selectSpec(Model model, Integer goodsId){
		
		Goods goods = goodsService.findGoodById(goodsId);
		
		model.addAttribute("goods", goods);
		
		/**获得商品的规格*/
		Map<String, Object> goodsspec = goodsService.getGoodsSpec(goodsId);
		
		if(goodsspec == null){
			
			return "redirect:/points/pro/detail?goodsSpecId=" + goods.getSpecId() + "&goodsId=" + goodsId;
		}
		
		model.addAttribute("goodsSpecObj", goodsspec);
		
		return "/points/pro-select-spec";
	}
	
	/**
	 * 选择规格生成相应的积分商品
	 */
	@RequestMapping("/pro/detail")
	public String detail(Model model, 
			@RequestParam (value="goodsSpecId", required=true) Integer goodsSpecId,
			@RequestParam (value="goodsId", required=true) Integer goodsId){
		
		Goods goods = goodsService.findGoodById(goodsId);
		
		model.addAttribute("goods", goods);
		
		GoodsSpec goodsSpec = goodsSpecService.findByGoodsSpecId(goodsSpecId);
		
		model.addAttribute("goodsSpec", goodsSpec);
		
		PointsGoods pointsGoodsCondition = new PointsGoods();
		
		pointsGoodsCondition.setGoodsSpecId(goodsSpecId);
		
		/**根据规格商品id查找积分商品*/
		//PointsGoods pointsGoods = pointsGoodsService.findOneGoodByCondition(pointsGoodsCondition);
		
		return "/points/pro-detail";
	}
	
	/**
	 * 生成积分商品
	 */
	@ResponseBody
	@RequestMapping("/pro/save")
	public Map<String, String> save(Model model, Integer goodsId, @ModelAttribute PointsGoods pointsGoods, String pointsGoodsStarttimeStr, String pointsGoodsEndtimeStr){
		Map<String, String> map = new HashMap<String, String>();
		try{
			
			if(StringUtils.isNotEmpty(pointsGoodsStarttimeStr)){
				pointsGoods.setPointsGoodsStarttime(DateUtils.strToLong(pointsGoodsStarttimeStr));
			}
			
			if(StringUtils.isNotEmpty(pointsGoodsEndtimeStr)){
				pointsGoods.setPointsGoodsEndtime(DateUtils.strToLong(pointsGoodsEndtimeStr));
			}
			
			pointsGoodsService.savePointsGoods(pointsGoods, goodsId);
			
		}catch(Exception e){
			e.printStackTrace();
			map.put("message", "2");
			return map;
		}
		
		map.put("message", "1");
		
		return map;
	}
	
	/**
	 * 选择规格生成相应的积分商品
	 */
	@RequestMapping("/pro/editDetail")
	public String editDetail(Model model, 
			@RequestParam (value="pointsGoodsId", required=true) Integer pointsGoodsId){
		
		PointsGoods pointsGoods = pointsGoodsService.findPointsGoodById(pointsGoodsId);
		
		model.addAttribute("pointsGoods", pointsGoods);
		
		return "/points/pro-edit-detail";
	}
	
	
	/**
	 * 生成积分商品
	 */
	@ResponseBody
	@RequestMapping("/pro/update")
	public Map<String, String> update(Model model, Integer goodsId, @ModelAttribute PointsGoods pointsGoods, String pointsGoodsStarttimeStr, String pointsGoodsEndtimeStr){
		Map<String, String> map = new HashMap<String, String>();
		try{
			
			if(StringUtils.isNotEmpty(pointsGoodsStarttimeStr)){
				pointsGoods.setPointsGoodsStarttime(DateUtils.strToLong(pointsGoodsStarttimeStr));
			}
			
			if(StringUtils.isNotEmpty(pointsGoodsEndtimeStr)){
				pointsGoods.setPointsGoodsEndtime(DateUtils.strToLong(pointsGoodsEndtimeStr));
			}
			
			pointsGoodsService.updatePointsGoods(pointsGoods);
			
		}catch(Exception e){
			e.printStackTrace();
			map.put("message", "2");
			return map;
		}
		
		map.put("message", "1");
		
		return map;
	}

	/**
	 * 选择规格生成相应的积分商品
	 */
	@RequestMapping("/pointsProduct/addOrEdit")
	public String addOrEdit(Model model, 
			@RequestParam (value="pointsGoodsId", required=true) Integer pointsGoodsId){
		if(pointsGoodsId==0){
			List<GoodsClass> list = goodsClassService.findList(0);
			model.addAttribute("datas", list);
			return "/points/pro-add-index";
		}else{
			return null;
		}
	}
	
	/**
	 * 
	 * @Title: findChildClass
	 * @Description: TODO(获取到子分类)
	 * @param @param id
	 * @param @param model
	 * @param @return
	 * @param @throws JsonGenerationException
	 * @param @throws JsonMappingException
	 * @param @throws Exception 设定文件
	 * @return Map<String,String> 返回类型
	 * @throws
	 */
	@RequestMapping(value = "/findChildClass")
	public @ResponseBody
	Map<String, String> findChildClass(@RequestParam(value = "id") String id,
			Model model) throws JsonGenerationException, JsonMappingException,
			Exception {
		Map<String, String> map = new HashMap<String, String>();

		List<GoodsClass> classList = goodsClassService.findList(Integer.parseInt(id));
		String json = "null";
		if (classList != null && classList.size() > 0) {
			json = JsonUtils.toJsonStr(classList);
		}
		map.put("result", json);
		map.put("success", "true");
		return map;
	}
	
	
	/**
	 * @throws Exception 
	 * 发布商品
	 * 填写基本信息
	 * @Title: selldetail
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @return 设定文件
	 * @return ModelAndView 返回类型
	 * @throws
	 */
	@RequestMapping(value = "/selldetail")
	public ModelAndView selldetail(@RequestParam(value = "gcId") String gcId) throws Exception {
		ModelAndView model = new ModelAndView("/points/pro-add");
		model.addObject("gcId", gcId);
		//店铺id
		//Integer storeId =  CacheUtils.getCacheUser().getStore().getStoreId();
		
		//商品分类
		GoodsClass goodsClass = goodsClassService.findById(Integer.valueOf(gcId));
		//类型id
		model.addObject("typeId", goodsClass.getTypeId());
		//分类全名称
		String catename = goodsClass.getGcName();
		model.addObject("catename", catename);
		
		//本店商品分类
		//StoreGoodsClassVo storeGoodsClassVo = new StoreGoodsClassVo();
		//storeGoodsClassVo.setStoreId(storeId);
		//List<StoreGoodsClassVo> goodsClassVos = storeGoodsClassService.queryClasssList(storeGoodsClassVo);
		/*
		 * 将这个list的结构改为map
		 * 此map的结构为:String, list
		 * 键为:	分类的父id
		 * 值为:List<StoreGoodsClassVo>
		 */
		/*Map<String, List<StoreGoodsClassVo>> StoreGoodsClassVoMap = new HashMap<String, List<StoreGoodsClassVo>>();
		for(int i = 0; i < goodsClassVos.size(); i++){
			//得到当前这个实体类
			StoreGoodsClassVo sgc = goodsClassVos.get(i);
			//获得父级id
			String parentId = sgc.getParentId()+"";
			//是否已经包含这个key
			if(StoreGoodsClassVoMap.containsKey(parentId)){
				//如果包含这个key,则取出他的list值,并且add当前这个对象
				List<StoreGoodsClassVo> list = StoreGoodsClassVoMap.get(parentId);
				list.add(sgc);
				StoreGoodsClassVoMap.put(parentId, list);
			}else{
				//否则新建一个key,新建一个list,并put进去
				List<StoreGoodsClassVo> list = new ArrayList<StoreGoodsClassVo>();
				list.add(sgc);
				StoreGoodsClassVoMap.put(parentId, list);
			}
		}
		model.addObject("StoreGoodsClassVoMap", StoreGoodsClassVoMap);*/
		
		//获得类型id
		Integer typeId = goodsClass.getTypeId();
		
		/*
		 * 通过类型id获得类型下的品牌,规格,属性
		 * 首先通过类型id获得goodsTypeVo
		 * 在这个超类中,有3个list,是品牌,规格,属性
		 */
		GoodsTypeVO goodsTypeVO = goodsTypeService.selectTypeFetchOther(typeId);
		
		if(goodsTypeVO != null){
				
			if(goodsTypeVO.getBrandList() != null){
				//获得该类型下的品牌
				List<Brand> brands = goodsTypeVO.getBrandList();
				//放入model
				model.addObject("brands", brands);
			}
			
			if(goodsTypeVO.getSpecList() != null){
				//获得该类型下的规格
				List<SpecVo> specs = goodsTypeVO.getSpecList();
				//放入model
				model.addObject("specs", specs);
			}
			
			if(goodsTypeVO.getAttributes() != null){
				//获得该类型下的属性
				List<GoodsAttribute> goodsAttributes = goodsTypeVO.getAttributes();
				//放入model
				model.addObject("goodsAttributes", goodsAttributes);
			}
		}
		
		//运费模板
        //Transport transport = transportService.getDefTransportByStoreId(storeId);
		//model.addObject("transport", transport);
		
        //一级地区加载
        List<Area> areas = areaService.queryAll();
        model.addObject("areas", areas);
		
		model.addObject("listimgSize", 0);//默认5个图片
		//图片base路径
		//model.addObject("imageServer", CommonConstants.IMG_SERVER);


		return model;
	}
}
