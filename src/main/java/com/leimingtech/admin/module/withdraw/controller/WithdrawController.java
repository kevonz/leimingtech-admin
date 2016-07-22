package com.leimingtech.admin.module.withdraw.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.aipg.rtreq.Trans;
import com.leimingtech.core.common.CommonConstants;
import com.leimingtech.core.common.Constants;
import com.leimingtech.core.entity.base.Admin;
import com.leimingtech.core.entity.base.Dictionary;
import com.leimingtech.core.entity.base.Withdraw;
import com.leimingtech.extend.module.payment.module.allinpay.pc.pay.service.AllinpayService;
import com.leimingtech.service.module.dictionary.service.DictionaryService;
import com.leimingtech.service.module.withdraw.service.WithdrawService;
import com.leimingtech.service.utils.page.Pager;
import com.leimingtech.service.utils.sessionkey.CacheUser;
import com.leimingtech.service.utils.sessionkey.CacheUtil;

/**
 *    
 * 项目名称：leimingtech-admin   
 * 类名称：WithdrawController   
 * 类描述：提现管理
 */
@Controller
@RequestMapping("/withdraw")
@Slf4j
public class WithdrawController {
	
	@Autowired
	private WithdrawService withdrawService;
	
	@Resource
	private AllinpayService allinpayService;
	
	@Resource
	private DictionaryService dictionaryService;
	
	/**
	 * 获取列表
	 * @param model
	 * @param pageNoStr
	 * @param name
	 * @return
	 */
	@RequiresPermissions("sys:withdraw:view")
	@RequestMapping("/list")
	public String list(
			Model model,
			@RequestParam(value = "pageNo", required=false, defaultValue = "1") String pageNoStr
			) {
		try {
			Pager pager = new Pager();
			pager.setPageSize(6);
			if (null != pageNoStr && !pageNoStr.equals("")) {
				pageNoStr = pageNoStr.replace("," , "");
				pager.setPageNo(Integer.parseInt(pageNoStr));
			}
			
			Withdraw withdraw = new Withdraw();
			
			pager.setCondition(withdraw);
			List<Dictionary> bankCodeList = dictionaryService.findDictionaryByCode("bankCode");
			List<Withdraw> resultList = withdrawService.getWithdrawList(pager);
			for(Withdraw w:resultList){
				for(Dictionary d:bankCodeList){
					if(d.getDictionaryValue().equals(w.getBankCode())){
						w.setBankCode(d.getDictionaryName());
						break;
					}
				}
			}
	        pager.setResult(resultList);

			model.addAttribute("withdraw", resultList);
			model.addAttribute("pager", pager);
			return "/withdraw/withdrawlist";
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("获取提现列表失败!");
		}
	}
	
	/**
	 * 新增实体
	 * @return
	 */
	@RequiresPermissions("sys:withdraw:edit")
	@RequestMapping("/add")
	public String add(Model model){
		model.addAttribute("bankCodes",dictionaryService.findDictionaryByCode("bankCode"));
		model.addAttribute("accountProps",dictionaryService.findDictionaryByCode("accountProp"));
		return "/withdraw/withdrawadd";
	}
	
	/**
	 * 修改或新增实体
	 * @return
	 */
	@RequiresPermissions("sys:withdraw:edit")
	@RequestMapping("/saveOrUpdate")
	public String saveOrUpdate(
			@ModelAttribute Withdraw withdraw,
			HttpServletRequest request,
			Model model
			){
        model.addAttribute("referer", CommonConstants.ADMIN_SERVER + "/withdraw/list");
        CacheUser user = CacheUtil.getCacheUser();
		Admin admin = user.getAdmin();
		
		//提现
		Trans trans = new Trans();
		trans.setACCOUNT_NAME(withdraw.getAccountName());
		trans.setACCOUNT_NO(withdraw.getAccountNo());
		trans.setACCOUNT_PROP(withdraw.getAccountProp());
		trans.setAMOUNT(withdraw.getAmount());
		trans.setBANK_CODE(withdraw.getBankCode());
		trans.setCURRENCY("CNY");
		String retCode = allinpayService.withdraw(trans);
		if(retCode.equals("0000")){//交易成功
			withdraw.setStatus("1");
		}else{
			withdraw.setStatus("0");
		}
		if(withdraw.getId() == null){
			withdraw.setCreatedby(admin.getAdminName());
			withdrawService.saveWithdraw(withdraw);
			model.addAttribute("msg", "新增成功");
		}else{
			Withdraw findWithdraw = withdrawService.getWithdrawById(withdraw.getId());
			if(null == findWithdraw){
				 log.warn("未找到，Id=" + withdraw.getId());
			} else {
				withdraw.setUpdateby(admin.getAdminName());
				withdrawService.updateWithdraw(withdraw);
				model.addAttribute("msg", "修改成功");
			}
		}
		return Constants.MSG_URL;
	}
	
	/**
	 * 根据id获取
	 * @param model
	 * @param id
	 * @return
	 */
	@RequiresPermissions("sys:withdraw:view")
	@RequestMapping(value = "/findById")
	public String findByid(Model model,
			@RequestParam(required = false, value = "id", defaultValue = "") int id){
		Withdraw withdraw = withdrawService.getWithdrawById(id);
		model.addAttribute("withdraw", withdraw);
		return "/withdraw/withdrawedit";
	}
	
	/**
	 * 删除实体
	 * @param ids
	 * @param model
	 * @return
	 */
	@RequiresPermissions("sys:withdraw:edit")
	@RequestMapping(value = "/delWithdraw")
	public String deleteWithdraw(@RequestParam(value = "ids") String ids,
			Model model, HttpServletRequest request){

		String referer = request.getHeader("Referer");
		model.addAttribute("referer", referer);
		if (StringUtils.isBlank(ids)) {
			model.addAttribute("result", "ID为空");
			model.addAttribute("msg", "删除失败，ID为空");
		}else{
			String[] idArray = StringUtils.split(ids, ",");
			for (String idStr : idArray) {
				withdrawService.deleteWithdraw(Integer.parseInt(idStr));
			}
			model.addAttribute("msg", "删除成功");
		}
		return Constants.MSG_URL;
	}
	
	/**
	 * 获取所有的实体
	 * @return
	 */
	@RequiresPermissions("sys:withdraw:view")
	@RequestMapping(value = "/allWithdraw")
	@ResponseBody
	public List<Withdraw> getAllEntity(){
		List<Withdraw> list = withdrawService.getAllWithdrawList();
		return list;
	}
}
