package com.leimingtech.admin.module.platformstore;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.leimingtech.core.base.BaseController;
import com.leimingtech.core.common.CommonConstants;
import com.leimingtech.core.common.Constants;
import com.leimingtech.core.common.DateUtils;
import com.leimingtech.core.common.excel.ExportExcelUtils;
import com.leimingtech.core.entity.Area;
import com.leimingtech.core.entity.GoodsAttribute;
import com.leimingtech.core.entity.GoodsClass;
import com.leimingtech.core.entity.GoodsExcel;
import com.leimingtech.core.entity.GoodsSpec;
import com.leimingtech.core.entity.Transport;
import com.leimingtech.core.entity.base.Brand;
import com.leimingtech.core.entity.base.Goods;
import com.leimingtech.core.entity.base.GoodsCombination;
import com.leimingtech.core.entity.vo.GoodsAttrVo;
import com.leimingtech.core.entity.vo.GoodsSpecVo;
import com.leimingtech.core.entity.vo.GoodsTypeVO;
import com.leimingtech.core.entity.vo.SpecVo;
import com.leimingtech.core.jackson.JsonUtils;
import com.leimingtech.core.platform.info.PlatformInfo;
import com.leimingtech.core.state.goods.GoodsState;
import com.leimingtech.service.module.area.service.AreaService;
import com.leimingtech.service.module.goods.service.BrandService;
import com.leimingtech.service.module.goods.service.GoodsClassService;
import com.leimingtech.service.module.goods.service.GoodsCombinationService;
import com.leimingtech.service.module.goods.service.GoodsService;
import com.leimingtech.service.module.goods.service.GoodsSpecService;
import com.leimingtech.service.module.goods.service.GoodsTypeService;
import com.leimingtech.service.module.product.service.ProductService;
import com.leimingtech.service.module.setting.service.SettingService;
import com.leimingtech.service.module.tostatic.service.ToStaticService;
import com.leimingtech.service.module.trade.service.TransportService;
import com.leimingtech.service.utils.goods.GoodsUtils;
import com.leimingtech.service.utils.http.ToStaticSendToFront;
import com.leimingtech.service.utils.page.Pager;

/**
 * action描述:平台关于商品跳转action
 * 创建人：cgl   
 * 创建时间：2015年08月03日16:04:35
 * 平台自营
 */
@Controller
@RequestMapping("/platform")
public class PlatformGoodsAction extends BaseController {
	
	@Autowired
	private GoodsClassService goodsClassService;
	
	@Resource
	private GoodsTypeService goodsTypeService;
	
	@Autowired
	private TransportService transportService;	
	
	@Resource
	private AreaService areaService;
	
	@Resource
	private ProductService productService;
	
	@Resource
	private GoodsService goodsService;
	
	@Resource
	private BrandService brandService;
	
	@Resource
	private GoodsSpecService goodsSpecService;
	
	@Autowired
	private ToStaticService toStaticService;
	
	@Autowired
	private GoodsCombinationService goodsCombinationService;
	
	@Autowired
    private SettingService settingService;
	
	/**
	 * 发布商品前选择分类
	 */
	@RequiresPermissions("sys:platformgoods:view")
	@RequestMapping(value = "/sellIndex")
	public ModelAndView sellIndex(@RequestParam(value = "goodsId", required=false, defaultValue="") String goodsId){
		
		ModelAndView model = new ModelAndView("/platform/goods/pro-sell-index");
		List<GoodsClass> list = goodsClassService.findList(0);
		model.addObject("datas", list);
		model.addObject("goodsId", goodsId);
		
		return model;
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
	@RequiresPermissions("sys:platformgoods:view")
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
	 * 发布商品的详细页面
	 */
	@RequiresPermissions("sys:platformgoods:view")
	@RequestMapping(value = "/sellDetail")
	public String sellDetail(@RequestParam(value = "gcId") String gcId, HttpServletRequest request, Model model){
		try{
			
			model.addAttribute("gcId", gcId);
			//店铺id
			Integer storeId =  PlatformInfo.PLATFORM_STORE_ID;
			
			//商品分类
			GoodsClass goodsClass = goodsClassService.findById(Integer.valueOf(gcId));
			//类型id
			model.addAttribute("typeId", goodsClass.getTypeId());
			//分类全名称
			String catename = goodsClass.getGcName();
			model.addAttribute("catename", catename);
			
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
					model.addAttribute("brands", brands);
				}

				if(goodsTypeVO.getSpecList() != null){
					//获得该类型下的规格
					List<SpecVo> specs = goodsTypeVO.getSpecList();
					//放入model
					model.addAttribute("specs", specs);
				}

				if(goodsTypeVO.getAttributes() != null){
					//获得该类型下的属性
					List<GoodsAttribute> goodsAttributes = goodsTypeVO.getAttributes();
					//放入model
					model.addAttribute("goodsAttributes", goodsAttributes);
				}

			}

			//运费模板
	        Transport transport = transportService.getDefTransportByStoreId(storeId);
			model.addAttribute("transport", transport);
			
	        //一级地区加载
	        List<Area> areas = areaService.queryAll();
	        model.addAttribute("areas", areas);
			
			model.addAttribute("listimgSize", 0);//默认5个图片
			//图片base路径
			model.addAttribute("imageServer", CommonConstants.IMG_SERVER);

		}catch(Exception e){
			e.printStackTrace();
	        String referer = request.getHeader("Referer");
	        model.addAttribute("referer", referer);
	        model.addAttribute("msg", "没有找到该分类下的类型,请重新选择分类");
	        return Constants.MSG_URL;
		}

		return "/platform/goods/pro_sell_detail";
	}
	
	/**
	 * message:0:失败, 1:成功
	 * @param request
	 * @param goods
	 * @return
	 */
	@RequiresPermissions("sys:platformgoods:edit")
	@RequestMapping(value = "/savePro")
	public @ResponseBody String savePro(HttpServletRequest request, Goods goods, 
			@RequestParam(value="prepareUp", required=false, defaultValue="") String prepareUpTime){
		try{
			// 校验数据的正确性
			if (!beanValidatorForJson(goods)){
				return json;
			}
			
			//设置utf-8
			request.setCharacterEncoding("utf-8");
			String goodsSpecJson = request.getParameter("goodsSpecJson");
			//获得当前店铺id
			Integer storeId = Constants.PLATFORM_STORE_ID;
			//获得当前店铺名称
			String storeName = PlatformInfo.PLATFORM_STORE_NAME;
			//设置到goods中
			goods.setStoreId(storeId);
			goods.setStoreName(storeName);
			//上架时间
			if(StringUtils.isNotEmpty(prepareUpTime)){
				goods.setUpdateTime(DateUtils.strToLong(prepareUpTime));
			}
			//调用保存的service的方法,返回状态0为失败1为成功
			Integer goodsId = productService.saveGoods(goods, goodsSpecJson);
			//判断是否成功
			if(goodsId == 0){
				//将失败的信号传入前台
				showErrorJson("商品数据保存失败");
				return json;
			}
			/**生成静态页面*/
			ToStaticSendToFront.onegoodsDetailStatic(goodsId, goods.getStoreId());
			//将成功的信号传导前台
			showSuccessJson("商品数据保存成功");
			return json;
		}catch(Exception e){
			e.printStackTrace();
			//将失败的信号传到前台
			showErrorJson("商品数据保存异常");
			return json;
		}
	}
	
	/**
	 * @throws Exception 
	 * 修改商品
	 * 填写基本信息
	 * @Title: selldetail
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @return 设定文件
	 * @return ModelAndView 返回类型
	 * @throws
	 */
	@RequiresPermissions("sys:platformclass:edit")
	@RequestMapping(value = "/editgoods")
	public ModelAndView editgoods(
			@RequestParam(value = "goodsId") String goodsId,
			@RequestParam(value = "gcId", required = false, defaultValue = "") String gcId
		) throws Exception {
		ModelAndView model = new ModelAndView("/platform/goods/pro_edit_goods");
		//根据goodsid获得goods
		Goods goods = new Goods();
		goods.setGoodsId(Integer.parseInt(goodsId));
		//获得当前店铺id
		Integer storeId = Constants.PLATFORM_STORE_ID;
		goods.setStoreId(storeId);
		goods = goodsService.findOneGoodByCondition(goods);
		//放入model
		model.addObject("goods", goods);
		
		/*
		 * 通过类型id获得类型下的品牌,规格,属性
		 * 首先通过类型id获得goodsTypeVo
		 * 在这个超类中,有3个list,是品牌,规格,属性
		 */
		List<Brand> brands = new ArrayList<Brand>();
		List<SpecVo> specs = new ArrayList<SpecVo>();
		List<GoodsAttribute> goodsAttributes = new ArrayList<GoodsAttribute>();
		Integer typeId = null;
		//分类名称
		String catename;
		if(StringUtils.isNotEmpty(gcId)){
			GoodsClass goodsClass = goodsClassService.findById(Integer.parseInt(gcId));
			catename = goodsClass.getGcName();
			typeId = goodsClass.getTypeId();
		}else{
			gcId = goods.getGcId() + "";
			catename = goods.getGcName();
			typeId = goods.getTypeId();
			//规格
			//通过goodsid在数据库中查出goods_spec
			//得到goodsSpec的list
			List<GoodsSpec> goodsSpecs = goodsSpecService.findListByGoodsId(goods.getGoodsId());
			for(int i = 0; i < goodsSpecs.size(); i++){
				if(goodsSpecs.get(i).getSpecGoodsSpec() != null && !goodsSpecs.get(i).getSpecGoodsSpec().trim().equals("")){
					goodsSpecs.get(i).setSpecValueIdStr(GoodsUtils.getThisGoodsAllSpecValueId(goodsSpecs.get(i).getSpecGoodsSpec()));
				}
			}
			//放入model
			model.addObject("goodsSpecs", goodsSpecs);
			if(goodsSpecs.size() == 1){
				model.addObject("goodsstorenum", goodsSpecs.get(0).getSpecGoodsStorage());
			}
			String goodsAttr = goods.getGoodsAttr();
			List<GoodsAttrVo> attrVoList = Lists.newArrayList();
			if(goodsAttr != null && !goodsAttr.trim().equals("")){
				//得到超类
				attrVoList = GoodsUtils.goodsAttrStrToGoodsAttrVoClass(goodsAttr);
				model.addObject("attrVoList", attrVoList);
			}
			String goodsSpec =  goods.getGoodsSpec();
			/**判断是否有规格属性，如果没有返回的list是null*/
			if(goodsSpec != null && !goodsSpec.trim().equals("")){
				Map<String, List<GoodsSpecVo>> specMap = GoodsUtils.goodsSpecStrToMapList(goodsSpec);
				System.out.println(specMap);
				model.addObject("specMap", specMap);
			}
			//规格颜色的图片
			String goodsColImg = goods.getGoodsColImg();
			if(goodsColImg != null && !goodsColImg.trim().equals("")){
				//得到map
				Map<String, String> colImgMap = GoodsUtils.goodsColImgStrToMap(goodsColImg);
				model.addObject("colImgMap", colImgMap);
			}
		}
		
		GoodsTypeVO goodsTypeVO = goodsTypeService.selectTypeFetchOther(typeId);
		if(null != goodsTypeVO){
			//获得该类型下的品牌
			brands = goodsTypeVO.getBrandList();
			//获得该类型下的规格
			specs = goodsTypeVO.getSpecList();
			//获得该类型下的属性
			goodsAttributes = goodsTypeVO.getAttributes();
		}
		//放入model
		model.addObject("brands", brands);
		//放入model
		model.addObject("specs", specs);
		//放入model
		model.addObject("goodsAttributes", goodsAttributes);
		//放入model
		model.addObject("gcId", gcId);
		//放入model
		model.addObject("catename", catename);
        //一级地区加载
        List<Area> areas = areaService.queryAll();
        model.addObject("areas", areas);
		//商品图片
		String[] imageMore = goods.getGoodsImageMore().split(",");
		List<String> imageList = Arrays.asList(imageMore);
		model.addObject("imageList", imageList);
		//运费模板
		Transport transport = transportService.getDefTransportByStoreId(storeId);
		model.addObject("transport", transport);
		//图片server路径
		String imgServer = CommonConstants.IMG_SERVER;
		model.addObject("imgServer", imgServer);
		//图片目录
		String imgSrc = Constants.SPECIMAGE_PATH;
		model.addObject("imgSrc", imgSrc);
		model.addObject("goodsId", goodsId);
		return model;
	}
	
	/**
	 * 
	 * @Title: savePro 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 * @param @param parentid
	 * @param @return
	 * @param @throws JsonGenerationException
	 * @param @throws JsonMappingException
	 * @param @throws Exception    设定文件 
	 * @return Map<String,String>    返回类型 
	 * @throws
	 */
	@RequiresPermissions("sys:platformgoods:edit")
	@RequestMapping(value = "/updatePro", method = RequestMethod.POST)
	public @ResponseBody String updatePro(HttpServletRequest request, Goods goods,
			@RequestParam(value="prepareUp", required=false, defaultValue="") String prepareUpTime){
		try{
			
			// 校验数据的正确性
			if (!beanValidatorForJson(goods)){
				return json;
			}
			
			//设置utf-8
			request.setCharacterEncoding("utf-8");
			String goodsSpecJson = request.getParameter("goodsSpecJson");
			Integer  goodsShow = Integer.valueOf(request.getParameter("goodsShow")); 
			//获得当前店铺id
			Integer storeId = PlatformInfo.PLATFORM_STORE_ID;
			//获得当前店铺名称
			String storeName = PlatformInfo.PLATFORM_STORE_NAME;
			//设置到goods中
			goods.setStoreId(storeId);
			goods.setStoreName(storeName);
			goods.setGoodsShow(goodsShow);
			//上架时间
			if(StringUtils.isNotEmpty(prepareUpTime)){
				goods.setUpdateTime(DateUtils.strToLong(prepareUpTime));
			}
			//调用保存的service的方法,返回状态0为失败1为成功
			Integer state = productService.updateGoods(goods, goodsSpecJson);
			//判断是否成功
			if(state == 0){
				//将失败的信号传入前台
				showErrorJson("商品数据保存失败");
				return json;
			}
			/**生成静态页面*/
			ToStaticSendToFront.onegoodsDetailStatic(goods.getGoodsId(), goods.getStoreId());
			//将成功的信号传导前台
			showSuccessJson("商品数据保存成功");
			return json;
		}catch(Exception e){
			e.printStackTrace();
			//将失败的信号传到前台
			showErrorJson("商品数据保存异常");
			return json;
		}
	}
	
	/**
	 * 
	 * @Title: list
	 * @Description: 出售中的商品
	 * @return String 返回类型
	 * @throws
	 */
	@RequiresPermissions("sys:platformgoods:view")
	@RequestMapping("/sellList")
	public String sellList(@ModelAttribute Goods goods,Model model,
            @RequestParam(required=false, value="pageNo",defaultValue="")String pageNo,
            @RequestParam(required=false, value="brandId",defaultValue="")String brandId) {

			Pager pager = new Pager();
			if (StringUtils.isNotBlank(pageNo)) {
				pager.setPageNo(Integer.parseInt(pageNo));
			}
    		goods.setStoreId(PlatformInfo.PLATFORM_STORE_ID);
    		goods.setGoodsShow(GoodsState.GOODS_ON_SHOW);//上架
    		goods.setGoodsState(GoodsState.GOODS_OPEN_STATE);//审核通过
    		if(StringUtils.isNotEmpty(brandId)){
    			goods.setBrandId(Integer.valueOf(brandId));
    		}
    		
			pager.setCondition(goods);// 实体加载在pager中
			List<Goods> results = goodsService.findGoodPagerList(pager);// 结果集
			GoodsClass goodsClass = goodsClassService.findById(goods.getGcId());
			String gcIdPath = "";
			String firstLevel = "";
			String secondLevel = "";
			String threeLevel = "";
			if(null != goodsClass){
				gcIdPath = goodsClass.getGcIdpath();
				String[] path = gcIdPath.split(",");
				firstLevel = path[0];
				if(path.length>1) 
				secondLevel = path[1];
				if(path.length>2)
				threeLevel = path[2];
			}
			// 页面查询条件品牌列表
            pager.setResult(results);
            model.addAttribute("pager", pager);//总数
            model.addAttribute("goods",goods);
            model.addAttribute("brandList",brandService.findList());
            model.addAttribute("classList",goodsClassService.findList(0));
            model.addAttribute("gcidpath", gcIdPath);
            model.addAttribute("firstLevel", firstLevel);
            model.addAttribute("secondLevel", secondLevel);
            model.addAttribute("threeLevel", threeLevel);
            //图片路径
            model.addAttribute("imgSrc",Constants.SPECIMAGE_PATH);
			// 转发请求到FTL页面
			return "/platform/goods/sell-list";
	}
	
	/**
	 * 
	 * @Title: list
	 * @Description:仓库中的商品
	 * @return String 返回类型
	 * @throws
	 */
	@RequiresPermissions("sys:platformgoods:view")
	@RequestMapping("/storeList")
	public String storeList(@ModelAttribute Goods goods,Model model,
			@RequestParam(required=false, value="pageNo",defaultValue="")String pageNo) {
		
		Pager pager = new Pager();
		if (StringUtils.isNotBlank(pageNo)) {
			pager.setPageNo(Integer.parseInt(pageNo));
		}
		//获取商品审核状态设置值
		Map<String,String> map = settingService.findByNameResultMap("goods_isApply");
		int goodsApply = Integer.valueOf(map.get("goods_isApply"));
		if(0 == goodsApply){//审核状态关闭
			//商品状态值 30:审核通过,40:违规下架,50:审核未通过,60:待审核
			goods.setGoodsState(GoodsState.GOODS_OPEN_STATE);
		}else{//审核状态开启
			goods.setGoodsState(GoodsState.GOODS_APPLY_PREPARE);
		}
		goods.setStoreId(PlatformInfo.PLATFORM_STORE_ID);
		goods.setGoodsShow(GoodsState.GOODS_STORE_SHOW);//仓库中
		pager.setCondition(goods);// 实体加载在pager中
		
		List<Goods> results = goodsService.findGoodPagerList(pager);// 结果集
		GoodsClass goodsClass = goodsClassService.findById(goods.getGcId());
		String gcIdPath = "";
		String firstLevel = "";
		String secondLevel = "";
		String threeLevel = "";
		if(null != goodsClass){
			gcIdPath = goodsClass.getGcIdpath();
			String[] path = gcIdPath.split(",");
			firstLevel = path[0];
			if(path.length>1) 
			secondLevel = path[1];
			if(path.length>2)
			threeLevel = path[2];
		}
		// 页面查询条件品牌列表
		pager.setResult(results);
		model.addAttribute("pager", pager);//总数
		model.addAttribute("goods",goods);
		model.addAttribute("brandList",brandService.findList());
		model.addAttribute("classList",goodsClassService.findList(0));
		model.addAttribute("gcidpath", gcIdPath);
        model.addAttribute("firstLevel", firstLevel);
        model.addAttribute("secondLevel", secondLevel);
        model.addAttribute("threeLevel", threeLevel);
		//图片路径
		model.addAttribute("imgSrc",Constants.SPECIMAGE_PATH);
		// 转发请求到FTL页面
		return "/platform/goods/store-list";
	}
	
	/**
	 * 上架商品
	 */
	@RequiresPermissions("sys:platformclass:edit")
    @RequestMapping("/upGoods")
    public @ResponseBody Map<String,Object> upGoods(
    		@RequestParam(value="goodsIds",required=true) String goodsIdsStr){
        Map<String,Object> map = Maps.newHashMap();
        try{
        	//循环删除
        	if(!goodsIdsStr.equals("")){
        		String[] goodsIds = goodsIdsStr.split(",");
        		for(int i = 0; i < goodsIds.length; i++){
        			Goods goods = new Goods();
        			goods.setGoodsId(Integer.parseInt(goodsIds[i]));
        			goods.setGoodsShow(GoodsState.GOODS_ON_SHOW);
        			goodsService.updateGoods(goods);
        			/**生成静态页面*/
        			ToStaticSendToFront.onegoodsDetailStatic(goods.getGoodsId(), PlatformInfo.PLATFORM_STORE_ID);
        		}
        	}
            map.put("success",true);
        }catch(Exception e){
            map.put("success",false);
            map.put("result","下架商品失败");
        }
        return map;
    }
	
	/**
	 * 下架商品
	 */
	@RequiresPermissions("sys:platformgoods:view")
    @RequestMapping("/downGoods")
    public @ResponseBody Map<String,Object> downGoods(
    		@RequestParam(value="goodsIds",required=true) String goodsIdsStr){
        Map<String,Object> map = Maps.newHashMap();
        try{
        	//循环删除
        	if(!goodsIdsStr.equals("")){
        		String[] goodsIds = goodsIdsStr.split(",");
        		for(int i = 0; i < goodsIds.length; i++){
        			Goods goods = new Goods();
        			goods.setGoodsState(GoodsState.GOODS_OPEN_STATE);
        			goods.setGoodsId(Integer.parseInt(goodsIds[i]));
        			goods.setGoodsShow(GoodsState.GOODS_OFF_SHOW);
        			goodsService.updateGoodOutEdit(goods);
        		}
        	}
            map.put("success",true);
        }catch(Exception e){
            map.put("success",false);
            map.put("result","下架商品失败");
        }
        return map;
    }
	
	/**
	 * 删除商品
	 */
	@RequiresPermissions("sys:platformgoods:edit")
    @RequestMapping("/deleteGoods")
    public @ResponseBody Map<String,Object> deleteGoods(
    		@RequestParam(value="goodsIds",required=true) String goodsIdsStr){
        Map<String,Object> map = Maps.newHashMap();
        try{
        	//循环删除
        	if(!goodsIdsStr.equals("")){
        		String[] goodsIds = goodsIdsStr.split(",");
        		for(int i = 0; i < goodsIds.length; i++){
        			goodsService.deleteGoods(Integer.parseInt(goodsIds[i]));
        			toStaticService.deleteGoodsDetailStaticPage(Integer.parseInt(goodsIds[i]));
        		}
        	}
            map.put("success",true);
        }catch(Exception e){
            map.put("success",false);
            map.put("result","删除商品失败");
        }
        return map;
    }
    /**
     * 跳转修改组合商品页面
     */
	@RequiresPermissions("sys:platformclass:edit")
    @RequestMapping("/updateCombinationIndex")
    public ModelAndView updateCombinationIndex(Integer storeId,Integer goodsId){
    	
    	ModelAndView modelAndView = new ModelAndView("/platform/goods/pro-combination");
    	
    	/**得到所有店铺下的id*/
    	Pager pager = new Pager();
    	pager.setPageSize(Integer.MAX_VALUE);
    	Goods goods = new Goods();
    	goods.setStoreId(storeId);
    	goods.setGoodsState(GoodsState.GOODS_OPEN_STATE);
    	pager.setCondition(goods);
    	
    	List<Goods> goodsList = goodsService.findGoodPagerList(pager);
    	modelAndView.addObject("goodsList", goodsList);
    	
    	/**创建设置查询条件*/
    	GoodsCombination goodsCombination = new GoodsCombination();
    	
    	/**设置查询条件*/
    	goodsCombination.setGoodsId(goodsId);
    	
    	/**查询*/
    	List<GoodsCombination> list = goodsCombinationService.selectByCondition(goodsCombination);
    	
    	modelAndView.addObject("goodsCombinations", list);
    	
    	modelAndView.addObject("goodsId", goodsId);
    	
    	return modelAndView;
    }
    
    /**
     * 修改组合商品
     */
	@RequiresPermissions("sys:platformclass:edit")
    @ResponseBody
    @RequestMapping("/updateCombination")
    public String updateCombination(GoodsCombination goodsCombination){
    	try{
        	goodsCombinationService.updateGoodsCombination(goodsCombination);
        	return "true";
    	}catch(Exception e){
    		return "false";
    	}
    }
    
    /**
	 * 
	 * @Title: list
	 * @Description:下架的商品
	 * @return String 返回类型
	 * @throws
	 */
	@RequestMapping("/closeShow")
	public String closeShow(@ModelAttribute Goods goods,Model model,
			@RequestParam(required=false, value="pageNo",defaultValue="")String pageNo) {
		
		Pager pager = new Pager();
		if (StringUtils.isNotBlank(pageNo)) {
			pager.setPageNo(Integer.parseInt(pageNo));
		}
		goods.setStoreId(PlatformInfo.PLATFORM_STORE_ID);
		goods.setGoodsShow(GoodsState.GOODS_OFF_SHOW);//下架
		goods.setGoodsState(GoodsState.GOODS_OPEN_STATE);//已通过
		pager.setCondition(goods);// 实体加载在pager中
		
		List<Goods> results = goodsService.findGoodPagerList(pager);// 结果集
		GoodsClass goodsClass = goodsClassService.findById(goods.getGcId());
		String gcIdPath = "";
		String firstLevel = "";
		String secondLevel = "";
		String threeLevel = "";
		if(null != goodsClass){
			gcIdPath = goodsClass.getGcIdpath();
			String[] path = gcIdPath.split(",");
			firstLevel = path[0];
			if(path.length>1) 
			secondLevel = path[1];
			if(path.length>2)
			threeLevel = path[2];
		}
		// 页面查询条件品牌列表
		pager.setResult(results);
		model.addAttribute("pager", pager);//总数
		model.addAttribute("goods",goods);
		model.addAttribute("brandList",brandService.findList());
		model.addAttribute("classList",goodsClassService.findList(0));
		model.addAttribute("gcidpath", gcIdPath);
        model.addAttribute("firstLevel", firstLevel);
        model.addAttribute("secondLevel", secondLevel);
        model.addAttribute("threeLevel", threeLevel);
		//图片路径
		model.addAttribute("imgSrc",Constants.SPECIMAGE_PATH);
		// 转发请求到FTL页面
		return "/platform/goods/store-closeShow";
	}
	
	  /**
		 * 
		 * @Title: list
		 * @Description:违规下架的商品
		 * @return String 返回类型
		 * @throws
		 */
		@RequestMapping("/offShow")
		public String offShow(@ModelAttribute Goods goods,Model model,
				@RequestParam(required=false, value="pageNo",defaultValue="")String pageNo) {
			
			Pager pager = new Pager();
			if (StringUtils.isNotBlank(pageNo)) {
				pager.setPageNo(Integer.parseInt(pageNo));
			}
			goods.setStoreId(PlatformInfo.PLATFORM_STORE_ID);
			goods.setGoodsShow(GoodsState.GOODS_OFF_SHOW);//下架
			goods.setGoodsState(GoodsState.GOODS_CLOSE_STATE);//违规
			pager.setCondition(goods);// 实体加载在pager中
			
			List<Goods> results = goodsService.findGoodPagerList(pager);// 结果集
			GoodsClass goodsClass = goodsClassService.findById(goods.getGcId());
			String gcIdPath = "";
			String firstLevel = "";
			String secondLevel = "";
			String threeLevel = "";
			if(null != goodsClass){
				gcIdPath = goodsClass.getGcIdpath();
				String[] path = gcIdPath.split(",");
				firstLevel = path[0];
				if(path.length>1) 
				secondLevel = path[1];
				if(path.length>2)
				threeLevel = path[2];
			}
			// 页面查询条件品牌列表
			pager.setResult(results);
			model.addAttribute("pager", pager);//总数
			model.addAttribute("goods",goods);
			model.addAttribute("brandList",brandService.findList());
			model.addAttribute("classList",goodsClassService.findList(0));
			model.addAttribute("gcidpath", gcIdPath);
            model.addAttribute("firstLevel", firstLevel);
            model.addAttribute("secondLevel", secondLevel);
            model.addAttribute("threeLevel", threeLevel);
			//图片路径
			model.addAttribute("imgSrc",Constants.SPECIMAGE_PATH);
			// 转发请求到FTL页面
			return "/platform/goods/store-offShow";
		}
	
		
		 /**
		 * 
		 * @Title: list
		 * @Description:待审核的商品
		 * @return String 返回类型
		 * @throws
		 */
		@RequestMapping("/preApply")
		public String preApply(@ModelAttribute Goods goods,Model model,
				@RequestParam(required=false, value="pageNo",defaultValue="")String pageNo) {
			
			Pager pager = new Pager();
			if (StringUtils.isNotBlank(pageNo)) {
				pager.setPageNo(Integer.parseInt(pageNo));
			}
			goods.setStoreId(PlatformInfo.PLATFORM_STORE_ID);
			goods.setGoodsShow(GoodsState.GOODS_ON_SHOW);//上架
			goods.setGoodsState(GoodsState.GOODS_APPLY_PREPARE);//待审核
			pager.setCondition(goods);// 实体加载在pager中
			
			List<Goods> results = goodsService.findGoodPagerList(pager);// 结果集
			GoodsClass goodsClass = goodsClassService.findById(goods.getGcId());
			String gcIdPath = "";
			String firstLevel = "";
			String secondLevel = "";
			String threeLevel = "";
			if(null != goodsClass){
				gcIdPath = goodsClass.getGcIdpath();
				String[] path = gcIdPath.split(",");
				firstLevel = path[0];
				if(path.length>1) 
				secondLevel = path[1];
				if(path.length>2)
				threeLevel = path[2];
			}
			// 页面查询条件品牌列表
			pager.setResult(results);
			model.addAttribute("pager", pager);//总数
			model.addAttribute("goods",goods);
			model.addAttribute("brandList",brandService.findList());
			model.addAttribute("classList",goodsClassService.findList(0));
			model.addAttribute("gcidpath", gcIdPath);
            model.addAttribute("firstLevel", firstLevel);
            model.addAttribute("secondLevel", secondLevel);
            model.addAttribute("threeLevel", threeLevel);
			//图片路径
			model.addAttribute("imgSrc",Constants.SPECIMAGE_PATH);
			// 转发请求到FTL页面
			return "/platform/goods/store-preApply";
		}
		
		 /**
		 * 
		 * @Title: list
		 * @Description:已拒绝的商品
		 * @return String 返回类型
		 * @throws
		 */
		@RequestMapping("/offApply")
		public String offApply(@ModelAttribute Goods goods,Model model,
				@RequestParam(required=false, value="pageNo",defaultValue="")String pageNo) {
			
			Pager pager = new Pager();
			if (StringUtils.isNotBlank(pageNo)) {
				pager.setPageNo(Integer.parseInt(pageNo));
			}
			goods.setStoreId(PlatformInfo.PLATFORM_STORE_ID);
			goods.setGoodsShow(GoodsState.GOODS_ON_SHOW);//上架
			goods.setGoodsState(GoodsState.GOODS_APPLY_OFF);//未通过
			pager.setCondition(goods);// 实体加载在pager中
			
			List<Goods> results = goodsService.findGoodPagerList(pager);// 结果集
			GoodsClass goodsClass = goodsClassService.findById(goods.getGcId());
			String gcIdPath = "";
			String firstLevel = "";
			String secondLevel = "";
			String threeLevel = "";
			if(null != goodsClass){
				gcIdPath = goodsClass.getGcIdpath();
				String[] path = gcIdPath.split(",");
				firstLevel = path[0];
				if(path.length>1) 
				secondLevel = path[1];
				if(path.length>2)
				threeLevel = path[2];
			}
			// 页面查询条件品牌列表
			pager.setResult(results);
			model.addAttribute("pager", pager);//总数
			model.addAttribute("goods",goods);
			model.addAttribute("brandList",brandService.findList());
			model.addAttribute("classList",goodsClassService.findList(0));
			model.addAttribute("gcidpath", gcIdPath);
            model.addAttribute("firstLevel", firstLevel);
            model.addAttribute("secondLevel", secondLevel);
            model.addAttribute("threeLevel", threeLevel);
			//图片路径
			model.addAttribute("imgSrc",Constants.SPECIMAGE_PATH);
			// 转发请求到FTL页面
			return "/platform/goods/store-offApply";
		}
		
		 /**
	     * 导出商品信息
	     */
		@RequestMapping("/loadgoodsbystoreid")
	    public void loadgoodsbystoreid(HttpServletResponse response) throws Exception{
			  //平台自营店铺id
	    	  List<GoodsExcel> goodslist=goodsService.findGoodListbystoreid2(PlatformInfo.PLATFORM_STORE_ID);
	    	  if(goodslist.size()!=0){
	    			    //定义文件的标头
	    			    String[] headers = { "商品ID", "商品名称", "商品分类id", "商品类型,1为全新、2为二手", "商品副标题","商品店铺价格","商品货号","商品推荐 是:1 否:0","商品关键字","商品描述 ","商品上架1下架0" }; 
					    String excelurl= ExportExcelUtils.export(goodslist,CommonConstants.FILE_BASEPATH+Constants.STORE_goodsexcel_URL,headers);
					    response.setContentType("application/x-msdownload");
					    response.setHeader("Content-disposition","attachment; filename="+excelurl);
					    BufferedInputStream bis = null;
					    BufferedOutputStream bos = null;
					    try{
						     bis = new BufferedInputStream(new FileInputStream(CommonConstants.FILE_BASEPATH+Constants.STORE_goodsexcel_URL+excelurl));
						     bos = new BufferedOutputStream(response.getOutputStream());
						     byte[] buff = new byte[2048000];
						     int bytesRead = 0;
						     while(-1 !=(bytesRead = (bis.read(buff, 0, buff.length)))){
					    	 bos.write(buff, 0, buff.length);
					       }
					    }catch(Exception e){
					    	e.printStackTrace();
					    }finally{
					      if(bis != null){
					        bis.close();
					     }
					     if(bos != null){
					        bos.close();
					      }
					    }
					    return;
		         }
		}
}