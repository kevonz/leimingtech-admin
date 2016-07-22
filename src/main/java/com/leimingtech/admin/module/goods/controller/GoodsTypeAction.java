package com.leimingtech.admin.module.goods.controller;

import java.util.HashMap;
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
import com.leimingtech.core.entity.GoodsType;
import com.leimingtech.core.entity.vo.SpecVo;
import com.leimingtech.service.module.goods.service.AttributeService;
import com.leimingtech.service.module.goods.service.BrandService;
import com.leimingtech.service.module.goods.service.GoodsClassService;
import com.leimingtech.service.module.goods.service.GoodsTypeService;
import com.leimingtech.service.module.goods.service.SpecService;
import com.leimingtech.service.module.goods.vo.GoodsTypeVo;
import com.leimingtech.service.utils.page.Pager;

@Controller
@RequestMapping("/goods/type")
public class GoodsTypeAction {
	@Resource
    private GoodsTypeService goodsTypeService;
    @Resource
    private SpecService specService;
    @Resource
    private BrandService brandService;
    @Resource
    private GoodsClassService goodsClassService;
    @Resource
    private AttributeService attributeService;
    String message = "success";

    /**
     * 查询列表
     * @param model
     * @return
     */
    @RequiresPermissions("sys:type:view")
    @RequestMapping("/list")
    public String list(Model model,
                       @RequestParam(required=false, value="pageNo",defaultValue="")String pageNo){

        Pager pager = new Pager();
        //int total  = goodsTypeService.findCount(pager);//获取总条数
        if(!StringUtils.isEmpty(pageNo)){
            pager.setPageNo(Integer.parseInt(pageNo));
        }
        List<GoodsType> list=goodsTypeService.findPageList(pager);
        pager.setResult(list);
        //pager.setTotalRows(total);
        model.addAttribute("pager", pager);//总数
        return "goods/type/list";
    }

    /**
     * 删除
     * @param
     * @return
     */
    @RequiresPermissions("sys:type:edit")
    @RequestMapping("/delete")
    public String delete(@RequestParam int[] ids,HttpServletRequest request,Model model){
        String referer = request.getHeader("Referer");
        for(int id : ids){
            goodsTypeService.delete(id);
        }
        model.addAttribute("referer", referer);
        model.addAttribute("msg", "删除成功");
        return Constants.MSG_URL;
    }

    /**
     *
     * @param id
     * @return
     */
    @RequiresPermissions("sys:type:view")
    @RequestMapping("/forwardtree")
    public String forwardtree(@RequestParam int id,Model model,
                          @ModelAttribute SpecVo specVo,@ModelAttribute GoodsType goodsType){
        model.addAttribute("specList",specService.findAllList(specVo));
        model.addAttribute("brandList",brandService.findBrandGroupByClassId());
        model.addAttribute("typeList",goodsTypeService.findList2(0));
        if( id == 0){
            return "goods/type/save";
        }else{
            model.addAttribute("type",goodsTypeService.selectTypeFetchOther(id));
            return "goods/type/edit";
        }
    }
    
    /**
    *
    * @param id
    * @return
    */
   @RequiresPermissions("sys:type:view")
   @RequestMapping("/forward")
   public String forward(@RequestParam int id,Model model,
                         @ModelAttribute SpecVo specVo){
       model.addAttribute("specList",specService.findAllList(specVo));
       model.addAttribute("brandList",brandService.findBrandGroupByClassId());
       if( id == 0){
           return "goods/type/save";
       }else{
           model.addAttribute("type",goodsTypeService.selectTypeFetchOther(id));
           return "goods/type/edit";
       }
   }

    
    /**
     * 保存
     * @return
     */
//    @RequestMapping("/savetree")
//    public String savetree(@ModelAttribute GoodsTypeVo vo,Model model,HttpServletRequest request){
//        //String referer = request.getHeader("Referer");
//        model.addAttribute("referer", "list");
//        if( vo.getGoodsType().getTypeId() != null){
//            goodsTypeService.updateGoodsType(vo);
//            model.addAttribute("msg", "修改成功");
//        }else{
//            goodsTypeService.saveGoodsType(vo);
//            GoodsType goodstype=new GoodsType();
//            //查找父级id
//            goodstype.setTypeId(vo.getGoodsType().getTypeId());
//            if(vo.getGoodsType().getParentId()==0){
//            	goodstype.setIdPath(vo.getGoodsType().getTypeId()+",");
//            }else{
//            	GoodsType goodsTypep=goodsTypeService.findById(vo.getGoodsType().getParentId());
//            	if(goodsTypep!=null){
//            		goodstype.setIdPath(goodsTypep.getIdPath()+vo.getGoodsType().getTypeId()+",");
//            	}
//            }
//            goodsTypeService.update(goodstype);
//            model.addAttribute("msg", "新增成功");
//        }
//        return Constants.MSG_URL;
//    }
   @RequiresPermissions("sys:type:edit")
    @RequestMapping("/save")
    public String save(@ModelAttribute GoodsTypeVo vo,Model model,HttpServletRequest request){
        model.addAttribute("referer", "list");
        if( vo.getGoodsType().getTypeId() != null){
            goodsTypeService.updateGoodsType(vo);
            model.addAttribute("msg", "修改成功");
        }else{
            goodsTypeService.saveGoodsType(vo);
            model.addAttribute("msg", "新增成功");
        }
        return Constants.MSG_URL;
    }


    /**
     * 修改排序
     * @return
     */
    @RequiresPermissions("sys:type:edit")
    @RequestMapping("/modifySort")
    public @ResponseBody Boolean modifySort(@RequestParam int id,@RequestParam Integer value){

        GoodsType type = new GoodsType();
        type.setTypeId(id);
        type.setTypeSort(value);
        goodsTypeService.updateType(type);
        return true;
    }
    
    /**
     * 根据属性id删除属性和属性值
     * @param attrId
     * @return
     */
    @RequiresPermissions("sys:type:edit")
    @RequestMapping("/deleteAttr")
    @ResponseBody
    public Map<String,Object> deleteAttr(@RequestParam int attrId){
    	Map<String,Object> map = new HashMap<String, Object>();
    	try{
    		attributeService.deleteAttrById(attrId);
    		map.put("success", true);
    	}catch (Exception e) {
    		map.put("success", false);
    		e.printStackTrace();
    	}
		return map;
    }
    
   /* *//**
     * 列表
     * @param goodsType
     * @param model
     * @return
     *//*
    @RequestMapping("list")
    public String list(@ModelAttribute GoodsType goodsType,Model model,
                       @RequestParam(required=false, value="div",defaultValue="")String div) {
        List<GoodsType> list = goodsTypeService.findList2(0);
        model.addAttribute("list", list);//结果集
        return "/goods/type/list";
    }*/
    /**
     * 查询子列表
     * @param id 父id
     * @return json
     */
//    @RequestMapping("child")
//    public @ResponseBody  List<GoodsType> child(@RequestParam int id,@RequestParam int level){
//        //存入deep，配合ajax
//        List<GoodsType> typeList = goodsTypeService.findChild(id);
//        for(GoodsType vo : typeList){
//            vo.setDeep(level);
//        }
//        return typeList;
//    }
   
    @RequiresPermissions("sys:type:edit")
    @RequestMapping(value = "/deleteid", method = RequestMethod.POST)
 	public @ResponseBody
 	Map<String, String> deleteid(@RequestParam(value = "typeId") Integer typeId,
 			Model model) {
 		Map<String, String> map = Maps.newHashMap();
 		if (typeId==null) {
 			model.addAttribute("result", "ID为空");
 			map.put("result", "ID为空");
 			map.put(message, "true");
 			return map;
 		}
 		goodsTypeService.delete(typeId);
 		map.put("result", "删除成功");
 		map.put(message, "true");
 		return map;
 	}
    
    /**
     * 校验菜单下是否有子菜单
     * @return
     */
    @RequiresPermissions("sys:type:edit")
    @RequestMapping("/validateparentid")
    public @ResponseBody Boolean validateparentid(@RequestParam int typeId){
        //校验重复
    	List<GoodsType> goodsTypelist=goodsTypeService.findList2(typeId);
        if(goodsTypelist.size()> 0){
            return false;
        }else{
            return true;
        }
    }
}
