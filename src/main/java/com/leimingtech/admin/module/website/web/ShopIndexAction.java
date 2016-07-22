package com.leimingtech.admin.module.website.web;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.joda.time.DateTime;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.leimingtech.core.common.CommonConstants;
import com.leimingtech.core.common.Constants;
import com.leimingtech.core.entity.GoodsClass;
import com.leimingtech.core.entity.vo.FloorVo;
import com.leimingtech.service.module.goods.service.BrandService;
import com.leimingtech.service.module.goods.service.GoodsClassService;
import com.leimingtech.service.module.website.service.WebCodeService;
import com.leimingtech.service.module.website.vo.BannerVo;
import com.leimingtech.service.module.website.vo.FaceVo;

/**
 * @author llf
 * @Package com.leimingtech.admin.module.website.web
 * @Description:
 * @date 2014/12/15 9:17
 */

@Slf4j
@Controller
@RequestMapping("/website/index")
public class ShopIndexAction {

    @Resource
    private GoodsClassService goodsClassService;
    @Resource
    private WebCodeService webCodeService;
    @Resource
    private BrandService brandService;

    @RequiresPermissions("sys:pagenavigation:view")
    @RequestMapping("/forward")
    public String forward(Model model,@RequestParam Integer id,String type) {
        List<GoodsClass> list = goodsClassService.findList(0);
        for(GoodsClass gc : list){
            gc.setClassList(goodsClassService.findList(gc.getGcId()));
        }
        model.addAttribute("classList", list);
        model.addAttribute("type", type);
        if(id == 0){
            return "website/index/save";
        }else{
            model.addAttribute("codeId",id);
            model.addAttribute("vo",webCodeService.queryById(id));
            return "website/index/edit";
        }
    }

    @RequiresPermissions("sys:pagenavigation:edit")
    @RequestMapping(value = "/upload")
    public ResponseEntity<Map<String, Object>> fileUpload(@RequestParam MultipartFile[] pic
            , HttpServletRequest request, HttpServletResponse response) {

        Map<String, Object> map = Maps.newHashMap();

        try {
            map = com.leimingtech.core.common.FileUtils.fileUpload(pic, CommonConstants.FILE_BASEPATH,Constants.ADV_UPLOAD_URL, request,"images",1);
        } catch (IOException e) {
            log.error("上传文件失败",e.toString());
            map.put("result", "上传文件失败");
            map.put("success", false);
        }
        response.setContentType("text/html;charset=UTF-8");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);
        return new ResponseEntity<Map<String, Object>>(map, headers, HttpStatus.OK);
    }

    @RequiresPermissions("sys:pagenavigation:edit")
    @RequestMapping("/save")
    public String save(@ModelAttribute FloorVo vo,HttpServletRequest request,Model model,
                       @RequestParam(required = false,value = "id", defaultValue ="0") int id){
        if (id == 0){
            //String referer = request.getHeader("Referer");
            model.addAttribute("referer", CommonConstants.ADMIN_SERVER+"/website/index/list");
            webCodeService.save(vo);
            model.addAttribute("msg","保存成功");
          //  return Constants.MSG_URL;
            return "redirect:/website/index/list?type="+vo.getType();
        }else{
            webCodeService.update(vo,id);
            return "redirect:/website/index/list?type="+vo.getType();
        }
    }

    /**
     * 列表查询
     * @param model
     */
    @RequiresPermissions("sys:pagenavigation:view")
    @RequestMapping("/list")
    public String list(Model model,String type){
        model.addAttribute("list",webCodeService.queryByVarname(type));
        model.addAttribute("type",type);
        return "website/index/list";
    }

    /**
     * 删除
     * @param id
     * @param request
     * @param model
     * @return
     */
    @RequiresPermissions("sys:pagenavigation:edit")
    @RequestMapping("/delete")
    public String delete(@RequestParam int id,HttpServletRequest request,Model model){
        String referer = request.getHeader("Referer");
        model.addAttribute("referer", referer);
        webCodeService.delete(id);
        model.addAttribute("msg","删除成功");
        return Constants.MSG_URL;
    }

    @RequiresPermissions("sys:pagenavigation:view")
    @RequestMapping("/forwardBanner")
    public String forwardBanner(){
        return "website/index/bannerSave";
    }

    @RequiresPermissions("sys:pagenavigation:edit")
    @RequestMapping("/saveBanner")
    public String saveBanner(@ModelAttribute FaceVo vo,HttpServletRequest request,Model model){

        String referer = request.getHeader("Referer");
        model.addAttribute("referer", referer);
        String imgPic ;
        String originalFilename ;
        List<BannerVo> bannerList = Lists.newArrayList();
        List<BannerVo> recommendList = Lists.newArrayList();
        int bannerIndex = 0;
        int recommendIndex = 0;
        for(MultipartFile myFile : vo.getBannerFiles()){
            if(!myFile.isEmpty()){
                originalFilename = String.valueOf(new DateTime().getMillis())+
                        myFile.getOriginalFilename().substring( myFile.getOriginalFilename().indexOf("."));
                try {
                    org.apache.commons.io.FileUtils.copyInputStreamToFile(myFile.getInputStream(),
                            new File(CommonConstants.FILE_BASEPATH + Constants.LOGO_UPLOAD_URL, originalFilename));
                } catch (IOException e) {
                    log.error("上传文件失败", e.toString());
                }
                imgPic = Constants.LOGO_UPLOAD_URL + "/" + originalFilename;
                BannerVo bannerVo = new BannerVo();
                bannerVo.setImageUrl(imgPic);
                bannerVo.setLinkUrl(vo.getBannerUrl()[bannerIndex]);
                bannerList.add(bannerVo);
            }
            bannerIndex ++;
        }
        for(MultipartFile myFile : vo.getRecommendFiles()){
            if(!myFile.isEmpty()){
                originalFilename = String.valueOf(new DateTime().getMillis())+
                        myFile.getOriginalFilename().substring( myFile.getOriginalFilename().indexOf("."));
                try {
                    org.apache.commons.io.FileUtils.copyInputStreamToFile(myFile.getInputStream(),
                            new File(CommonConstants.FILE_BASEPATH + Constants.LOGO_UPLOAD_URL, originalFilename));
                } catch (IOException e) {
                    log.error("上传文件失败", e.toString());
                }
                imgPic = Constants.LOGO_UPLOAD_URL + "/" + originalFilename;
                BannerVo bannerVo = new BannerVo();
                bannerVo.setImageUrl(imgPic);
                bannerVo.setLinkUrl(vo.getRecommendUrl()[recommendIndex]);
                recommendList.add(bannerVo);
            }
            recommendIndex ++;
        }
        webCodeService.saveBanner(bannerList,recommendList);
        model.addAttribute("msg","保存成功");
        return Constants.MSG_URL;
    }

    @RequiresPermissions("sys:pagenavigation:view")
    @RequestMapping("/getBrand")
    public String getBrand(Model model) {

        model.addAttribute("brandList", brandService.findBrandGroupByClassId());
        return "website/index/brandSave";
    }

    @RequiresPermissions("sys:pagenavigation:edit")
    @RequestMapping("/saveBrand")
    public String saveBrand(@ModelAttribute FaceVo vo, Model model, HttpServletRequest request) {
        webCodeService.saveBrand(vo.getBrandIds());
        String referer = request.getHeader("Referer");
        model.addAttribute("referer", referer);
        model.addAttribute("msg", "保存成功");
        return Constants.MSG_URL;
    }

}
