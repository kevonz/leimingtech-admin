package com.leimingtech.admin.module.goods.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.leimingtech.admin.utils.ShowPageUtils;
import com.leimingtech.core.common.Collections3;
import com.leimingtech.core.common.Constants;
import com.leimingtech.core.common.sms.C123SendSmsUtil;
import com.leimingtech.core.entity.GoodsClass;
import com.leimingtech.core.entity.base.Goods;
import com.leimingtech.core.entity.base.RelGoodsRecommend;
import com.leimingtech.core.entity.base.Setting;
import com.leimingtech.core.entity.base.Store;
import com.leimingtech.core.state.goods.GoodsState;
import com.leimingtech.service.module.goods.service.BrandService;
import com.leimingtech.service.module.goods.service.GoodsClassService;
import com.leimingtech.service.module.goods.service.GoodsService;
import com.leimingtech.service.module.goods.service.RelGoodsRecommendService;
import com.leimingtech.service.module.setting.service.SettingService;
import com.leimingtech.service.module.store.service.StoreService;
import com.leimingtech.service.module.tostatic.service.ToStaticService;
import com.leimingtech.service.utils.page.Pager;

@Controller
@RequestMapping("/goods/goodsCommon")
@Slf4j
public class GoodsCommonAction {
	String message = "success";

	@Resource
	private BrandService brandService;

	@Resource
	private GoodsClassService goodsClassService;

    @Resource
    private GoodsService goodsService;

    @Resource
    private SettingService settingService;
    
    @Autowired
    ToStaticService toStaticService;
    
    @Resource
	private RelGoodsRecommendService relGoodsRecommendService;
    
    @Resource
    private StoreService storeService;
    
	/**
	 * 
	 * @Title: list
	 * @Description: (加载数据页面)
	 * @return String 返回类型
	 * @throws
	 */
    @RequiresPermissions("sys:goods:view")
	@RequestMapping("/list")
	public String list(@ModelAttribute Goods goods,Model model,
            @RequestParam(required=false, value="pageNo",defaultValue="")String pageNo) {

			Pager pager = new Pager();
			if (StringUtils.isNotBlank(pageNo)) {
				pager.setPageNo(Integer.parseInt(pageNo));
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
            model.addAttribute("gcommon",goods);
            model.addAttribute("brandList",brandService.findList());
            model.addAttribute("classList",goodsClassService.findList(0));
            model.addAttribute("gcidpath", gcIdPath);
            model.addAttribute("firstLevel", firstLevel);
            model.addAttribute("secondLevel", secondLevel);
            model.addAttribute("threeLevel", threeLevel);
            //图片路径
            model.addAttribute("imgSrc",Constants.SPECIMAGE_PATH);
			// 转发请求到FTL页面
			return "/goods/goodsCommon/list";
	}

	/**
	 * 
	 * @Title: delGoods
	 * @Description: 删除商品
	 * @param @param ids
	 * @param @param model
	 * @param @return 设定文件
	 * @return Map<String,String> 返回类型
	 * @throws
	 */
    @RequiresPermissions("sys:goods:edit")
	@RequestMapping(value = "/delGoods", method = RequestMethod.POST)
	public String delGoods(@RequestParam long[] ids,
			Model model,HttpServletRequest request) {

        String referer = request.getHeader("Referer");

		for (long id : ids) {
			goodsService.deleteGoods(Integer.parseInt(id+""));
			toStaticService.deleteGoodsDetailStaticPage(Integer.parseInt(id+""));
		}
        model.addAttribute("referer", referer);
        model.addAttribute("msg", "删除成功");
        return Constants.MSG_URL;
	}
	
	/**
	 * 
	 * @Title: delGoods
	 * @Description: 删除下架的商品
	 * @param @param ids
	 * @param @param model
	 * @param @return 设定文件
	 * @return Map<String,String> 返回类型
	 * @throws
	 */
    @RequiresPermissions("sys:goods:edit")
	@RequestMapping(value = "/deldownGoods", method = RequestMethod.POST)
	public String deldownGoods(@RequestParam long[] ids,
			Model model,HttpServletRequest request) {

        String referer = request.getHeader("Referer");

		for (long id : ids) {
			goodsService.deleteGoods(Integer.parseInt(id+""));
			toStaticService.deleteGoodsDetailStaticPage(Integer.parseInt(id+""));
		}
        model.addAttribute("referer", referer);
        model.addAttribute("msg", "删除成功");
        return Constants.MSG_URL;
	}

	/**
	 * 
	 * @Title: delGoods
	 * @Description: 违规下架商品
	 * @param @param ids
	 * @param @param model
	 * @param @return 设定文件
	 * @return Map<String,String> 返回类型
	 * @throws
	 */
    @RequiresPermissions("sys:goods:edit")
	@RequestMapping(value = "/wgxjGoods",produces = "text/xml;charset=UTF-8")
	public @ResponseBody String wgxjGoods(@RequestParam String goodsIds,
                                                       @RequestParam String remark) {
		String[] idArray = StringUtils.split(goodsIds, ",");
		for (String idStr : idArray) {
			Integer goodsId = Integer.parseInt(idStr);
            Goods goods = new Goods();
            goods  = goodsService.findGoodById(goodsId);
            goods.setGoodsId(goodsId);
            goods.setGoodsState(GoodsState.GOODS_CLOSE_STATE);
            goods.setGoodsShow(GoodsState.GOODS_OFF_SHOW);
            goods.setGoodsCloseReason(remark);
            try {
				goodsService.updateGoodOutEdit(goods);
				toStaticService.deleteGoodsDetailStaticPage(goodsId);
				// 违规下架商品给店铺发短信通知
				goods  = goodsService.findGoodById(goodsId);
				Integer storeId = goods.getStoreId();
				Store store = storeService.findById(storeId);
				if(null != store){
					String mobile = store.getStoreTel();
					C123SendSmsUtil c123 = new C123SendSmsUtil();
					c123.sendSms(mobile, "您有商品违规下架，商品为" + goods.getGoodsName() + ",理由" + remark);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ShowPageUtils.showDialog("操作成功", "reload", "succ", "");
	}

	/**
	 * 
	 * @Title: findClassByParentId
	 * @Description: 根据商品分类id返回json格式的子类列表
	 * @param @param id
	 * @param @param model
	 * @param @return 设定文件
	 * @return Map<String,String> 返回类型
	 * @throws
	 */
    @RequiresPermissions("sys:goods:edit")
	@RequestMapping(value = "/findClassByParentId", method = RequestMethod.POST)
	public @ResponseBody
	Map<String, String> findClassByParentId(
			@RequestParam(value = "id") String id, Model model) {
		Map<String, String> map = new HashMap<String, String>();

		int classid = Integer.parseInt(id);
		List<GoodsClass> classList = goodsClassService.findList(classid);
		JSONArray array = new JSONArray(classList);
		String ss = array.toString();
		map.put("result", ss);
		map.put(message, "true");
		return map;
	}

	/**
	 * 导航至商品设置页面
	 * 
	 * @Title: setGoods
	 * @Description: TODO(导航至商品设置页面)
	 * @param @return 设定文件
	 * @return String 返回类型
	 * @throws
	 */
    @RequiresPermissions("sys:goods:view")
	@RequestMapping("/setGoods")
	public String setGoods(Model model) {

		try {
            model.addAttribute("goodsVerify",settingService.queryByName("goods_verify").getValue());
			return "/goods/goodsCommon/setGoods";
		} catch (Exception e) {
			log.error("导航失败!", e);
			throw new RuntimeException("导航失败!");
		}
	}
    
    
    @RequiresPermissions("sys:goods:edit")
    @RequestMapping("/saveSetting")
    public String saveSetting(Model model,HttpServletRequest request,String goodsVerify){

        String referer = request.getHeader("Referer");
        Setting setting = new Setting();
        setting.setName("goods_verify");
        setting.setValue(goodsVerify);
        settingService.updateEntity(setting);
        model.addAttribute("referer", referer);
        model.addAttribute("msg", "修改成功");
        return Constants.MSG_URL;
    }
    /**
     * 跳转到违规下架
     * @param id
     * @param model
     * @return
     */
    @RequiresPermissions("sys:goods:view")
    @RequestMapping("/remark")
    public String openRemark(@RequestParam String id,Model model){

        model.addAttribute("goodsIds",id);
        return "goods/goodsCommon/remark";
    }

    /**
     * 违规下架list
     * @Title: list
     * @Description: (加载数据页面)
     * @return String 返回类型
     * @throws
     */
    @RequiresPermissions("sys:goods:view")
    @RequestMapping("/downList")
    public String downList(@ModelAttribute Goods goods,Model model,
                       @RequestParam(required=false, value="pageNo",defaultValue="")String pageNo) {

        Pager pager = new Pager();
        if (StringUtils.isNotBlank(pageNo)) {
            pager.setPageNo(Integer.parseInt(pageNo));
        }
        goods.setGoodsShow(GoodsState.GOODS_OFF_SHOW);
        goods.setGoodsState(GoodsState.GOODS_CLOSE_STATE);
        pager.setCondition(goods);// 实体加载在pager中

        //int total = goodsService.findGoodPagerListCount(pager);// 获取总条数

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
			secondLevel = path[1];
			threeLevel = path[2];
		}

        // 页面查询条件品牌列表
        pager.setResult(results);
        //pager.setTotalRows(total);
        model.addAttribute("pager", pager);//总数
        model.addAttribute("gcommon",goods);
        model.addAttribute("brandList",brandService.findList());
        model.addAttribute("classList",goodsClassService.findList(0));
        model.addAttribute("gcidpath", gcIdPath);
        model.addAttribute("firstLevel", firstLevel);
        model.addAttribute("secondLevel", secondLevel);
        model.addAttribute("threeLevel", threeLevel);
        // 转发请求到FTL页面
        return "/goods/goodsCommon/downList";
    }
//
//    /**
//     *
//     * @Title: list
//     * @Description: (加载数据页面)
//     * @return String 返回类型
//     * @throws
//     */
//    @RequestMapping("/verfiyList")
//    public String verfiyList(@ModelAttribute Goods goods,Model model,
//                           @RequestParam(required=false, value="pageNo",defaultValue="")String pageNo) {
//
//        Pager pager = new Pager();
//        if (StringUtils.isNotBlank(pageNo)) {
//            pager.setPageNo(Integer.parseInt(pageNo));
//        }
////        goodsCommon.setGcIds(this.createGcIds(goodsCommon.getGcId()));
//        goods.setGoodsVerify(1);
//        pager.setCondition(goodsCommon);// 实体加载在pager中
//
//        int total = goodsCommonService.countVerfiyList(pager);// 获取总条数
//
//        List<GoodsCommon> results = goodsCommonService.queryVerfiyList(pager);// 结果集
//
//        // 页面查询条件品牌列表
//        pager.setResult(results);
//        pager.setTotalRows(total);
//        model.addAttribute("pager", pager);//总数
//        model.addAttribute("gcommon",goodsCommon);
//        model.addAttribute("brandList",brandService.findList());
//        model.addAttribute("classList",goodsClassService.findList());
//        // 转发请求到FTL页面
//        return "/goods/goodsCommon/verfiyList";
//    }
//
//    /**
//     * 跳转到审核
//     * @param id
//     * @param model
//     * @return
//     */
//    @RequestMapping("/verfiyRemark")
//    public String openVerfiyRemark(@RequestParam String id,Model model){
//
//        model.addAttribute("commonids",id);
//        return "goods/goodsCommon/verfiy_remark";
//    }

//    /**
//     * 审核
//     * @param commonids
//     * @param verifyState
//     * @param verifyReason
//     * @return
//     */
//    @RequestMapping(value = "/verfiyGoods",produces = "text/xml;charset=UTF-8")
//    public @ResponseBody String verfiyGoods(@RequestParam String commonids,@RequestParam int verifyState,
//                                          @RequestParam String verifyReason) {
//        String[] idArray = StringUtils.split(commonids, ",");
//        for (String idStr : idArray) {
//            GoodsCommon goodsCommon = new GoodsCommon();
//            goodsCommon.setGoodsCommonid(Integer.parseInt(idStr));
//            goodsCommon.setGoodsVerify(verifyState);
//            goodsCommon.setGoodsVerifyremark(verifyReason);
//            goodsCommonService.updateWgxj(goodsCommon);
//        }
//        return ShowPageUtils.showDialog("操作成功", "reload", "succ", "");
//    }

//    /**
//     * goods列表
//     * @param commonids
//     * @return
//     */
//    @RequestMapping(value="/getGoods")
//    public @ResponseBody List<Goods> getGoods(@RequestParam String goodsId){
////        return goodsService.queryGoodsListByCommonids(commonids);
//    }

    /**
     * 根据父节点返回对应子节点字符串
     * @param gcId
     * @return
     */
    private List<String> createGcIds(Integer gcId){
        //如果父节点为null，子节点不选
        if(gcId == null || gcId == 0){
            return null;
        }else{
            List<String> gcIds = Lists.newArrayList();
            List<GoodsClass> list = goodsClassService.findList(gcId);
            gcIds.add(gcId+"");
            gcIds = Collections3.union(gcIds,Collections3.extractToList(list,"gcId"));
            for(GoodsClass vo : list){
                if(vo.getHasChild() > 0){
                    List<GoodsClass> childList = goodsClassService.findList(vo.getGcId());
                    if(Collections3.isNotEmpty(childList)){
                        gcIds = Collections3.union(gcIds,Collections3.extractToList(childList,"gcId"));
                        for(GoodsClass gv : childList){
                            if(gv.getHasChild() > 0){
                                List<GoodsClass> childList1 = goodsClassService.findList(gv.getGcId());
                                if(Collections3.isNotEmpty(childList1)){
                                    gcIds = Collections3.union(gcIds,Collections3.extractToList(childList1,"gcId"));
                                }
                            }
                        }
                    }
                }
            }
            return gcIds;
        }
    }
    
    /**
	 * 
	 * @Title: recommendlist
	 * @Description: (加载数据页面)
	 * @return String 返回类型
	 * @throws
	 */
    @RequiresPermissions("sys:goods:view")
	@RequestMapping("/recommendlist")
	public String recommendlist(@ModelAttribute Goods goods,Model model,
            @RequestParam(required=false, value="pageNo",defaultValue="")String pageNo,
            @RequestParam(required=false, value="reCommendId",defaultValue="")Integer reCommendId) {
		
		    List<RelGoodsRecommend> rellist=relGoodsRecommendService.findgoodsids(reCommendId);
		    String[] goodids=null;
		    String str="";
		    if(rellist.size()!=0){
		    	for(RelGoodsRecommend relGoodsRecommend:rellist){
		    		str+=relGoodsRecommend.getGoodsId()+",";
		    	}
		    	goodids=str.split(",");
		    }
		    goods.setGoodids(goodids);
			Pager pager = new Pager();
			if (StringUtils.isNotBlank(pageNo)) {
				pager.setPageNo(Integer.parseInt(pageNo));
			}
//            goods.setGcIds(this.createGcIds(goods.getGcId()));
			goods.setGoodsShow(GoodsState.GOODS_ON_SHOW);//商品上架状态
			goods.setGoodsState(GoodsState.GOODS_OPEN_STATE);//商品状态审核通过

			pager.setCondition(goods);// 实体加载在pager中
            pager.setPageSize(10);
			//int total = goodsService.findGoodPagerListCount(pager);// 获取总条数

			List<Goods> results = goodsService.findGoodPagerList(pager);// 结果集
			// 页面查询条件品牌列表
            pager.setResult(results);
            //pager.setTotalRows(total);
            model.addAttribute("pager", pager);//总数
            model.addAttribute("goods",goods);
            model.addAttribute("brandList",brandService.findList());
            model.addAttribute("classList",goodsClassService.findList(0));
            //图片路径
            model.addAttribute("imgSrc",Constants.SPECIMAGE_PATH);
			// 转发请求到FTL页面
			return "/goods/goodsCommon/recommendlist";
	}

	
    /**
     * 待审核商品list
     * @Title: list
     * @Description: (加载数据页面)
     * @return String 返回类型
     * @throws
     */
    @RequestMapping("/goodsApply")
    public String goodsApply(@ModelAttribute Goods goods,Model model,
                       @RequestParam(required=false, value="pageNo",defaultValue="")String pageNo) {

        Pager pager = new Pager();
        if (StringUtils.isNotBlank(pageNo)) {
            pager.setPageNo(Integer.parseInt(pageNo));
        }
        //商品状态待审核
        goods.setGoodsState(GoodsState.GOODS_APPLY_PREPARE);
        //商品状态上架（立即发布，定时上架）
        goods.setGoodsShow(GoodsState.GOODS_ON_SHOW);
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
			secondLevel = path[1];
			threeLevel = path[2];
		}

        // 页面查询条件品牌列表
        pager.setResult(results);
        //pager.setTotalRows(total);
        model.addAttribute("pager", pager);//总数
        model.addAttribute("gcommon",goods);
        model.addAttribute("brandList",brandService.findList());
        model.addAttribute("classList",goodsClassService.findList(0));
        model.addAttribute("gcidpath", gcIdPath);
        model.addAttribute("firstLevel", firstLevel);
        model.addAttribute("secondLevel", secondLevel);
        model.addAttribute("threeLevel", threeLevel);
        // 转发请求到FTL页面
        return "/goods/goodsCommon/goodsApply";
    }
    
	/**
	 * 
	 * @Title: offApply
	 * @Description: 商品审核拒绝操作
	 * @param goodsId 商品id
	 * @return Map<String,String> 返回类型
	 * @throws
	 */
	@RequestMapping(value = "/offApply")
	public String offApply(Model model,HttpServletRequest request,
						   @RequestParam(value = "goodsId") Integer goodsId,
						   @RequestParam(required=false, value="pageNo",defaultValue="")String pageNo) {
            try {
            	String referer = request.getHeader("Referer");
            	Goods goods = new Goods();
            	goods  = goodsService.findGoodById(goodsId);
            	goods.setGoodsState(GoodsState.GOODS_APPLY_OFF);
            	goods.setGoodsId(goodsId);
				goodsService.updateGoodOutEdit(goods);
				toStaticService.deleteGoodsDetailStaticPage(Integer.valueOf(goodsId));
				model.addAttribute("referer", referer);
		        model.addAttribute("msg", "拒绝成功");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            return "redirect:/goods/goodsCommon/list";
	}
	
	/**
	 * 
	 * @Title: oNApply
	 * @Description: 商品审核通过操作
	 * @param goodsId 商品id
	 * @return Map<String,String> 返回类型
	 * @throws
	 */
	@RequestMapping(value = "/oNApply")
	public String oNApply(Model model,HttpServletRequest request,
						   @RequestParam(value = "goodsId") Integer goodsId,
						   @RequestParam(required=false, value="pageNo",defaultValue="")String pageNo) {
            try {
            	String referer = request.getHeader("Referer");
            	Goods goods = new Goods();
            	goods  = goodsService.findGoodById(goodsId);
            	goods.setGoodsState(GoodsState.GOODS_OPEN_STATE);
            	goods.setGoodsId(goodsId);
				goodsService.updateGoodOutEdit(goods);
				Integer storeId = goods.getStoreId();
//				toStaticService.goodsDetailToStaticByGoodsId(Integer.valueOf(goodsId),Integer.valueOf(storeId));
				model.addAttribute("referer", referer);
		        model.addAttribute("msg", "审核通过成功");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            return "redirect:/goods/goodsCommon/list";
	}
}
