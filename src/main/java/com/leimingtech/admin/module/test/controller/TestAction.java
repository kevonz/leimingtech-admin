//package com.leimingtech.admin.module.test.controller;
//
//import java.util.List;
//import java.util.Map;
//
//import lombok.extern.slf4j.Slf4j;
//
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import com.google.common.collect.Maps;
//import com.leimingtech.core.entity.base.Account;
//import com.leimingtech.service.module.test.service.TestService;
//import com.leimingtech.service.utils.page.Pager;
//
///**
// *    
// * 项目名称：leimingtech-admin   
// * 类名称：TestAction   
// * 类描述：   
// * 创建人：liuhao   
// * 创建时间：2014年11月5日 上午12:03:21   
// * 修改人：liuhao   
// * 修改时间：2014年11月5日 上午12:03:21   
// * 修改备注：   
// * @version    
// *
// */
//@Controller
//@RequestMapping("/test")
//@Slf4j
//public class TestAction {
//	
//	String message = "success";
//	
//	
//	@Autowired
//	private TestService testService;
//
//	/**导航至主操作页面*/
//	@RequestMapping("/index")
//	public String index(){
//		try{
//			log.debug("sss");
//			return "/test/index";
//		}catch (Exception e) {
//			e.printStackTrace();
//			throw new RuntimeException("导航失败!");
//		}
//	}
//	
//	/**
//	 * 加载数据页面
//	 * 带分页，自己瞎写的 见谅
//	 * @param acctName 列表头查询条件
//	 * @param certifyClass
//	 * 
//	 */
//	@RequestMapping(value = "/list")
//	public String list(Model model,
//			@RequestParam(required=false, value="div",defaultValue="")String div,
//			@RequestParam(required=false, value="pageNo",defaultValue="")String pageNoStr,
//			@RequestParam(required=false, value="acctName",defaultValue="")String acctName,
//			@RequestParam(required=false, value="certifyClass",defaultValue="")String certifyClass){
//		Pager pager = new Pager();
//		Account account = new Account();
//		/**查询条件，放入实体中，**/
//		if(StringUtils.isNotBlank(acctName)){
//			if (acctName.contains("@")) {//
//				account.setEmail(acctName);
//			} else {
//				account.setRealName(acctName);
//				account.setLoginName(acctName);
//			}
//		}
//		if (StringUtils.isNotBlank(certifyClass)){
//			if(Integer.valueOf(certifyClass)>0)
//				account.setCertifyClass(Integer.valueOf(certifyClass));
//		}
//		
//		if(null != pageNoStr && !pageNoStr.equals("")){
//			pager.setPageNo(Integer.parseInt(pageNoStr));
//		}
//		
//		pager.setCondition(account);//实体加载在pager中
//		
//		int total  = testService.findAcctCount(pager);//获取总条数
//		List<Account> results = testService.findAcctList(pager);//结果集
//		model.addAttribute("datas", results);//结果集
//		model.addAttribute("pageNo", pager.getPageNo());//当前页
//		model.addAttribute("pageSize", pager.getPageSize());//每页显示条数
//		model.addAttribute("recordCount", total);//总数
//		model.addAttribute("toUrl", "test/list");//跳转URL
//		model.addAttribute("div", div);//显示的DIV数据区域
//		//转发请求到FTL页面
//		return "/test/list";
//		
//	}
//	
//	
//	/**
//	 * 查询单条记录
//	 * @param model
//	 * @param id
//	 * @return
//	 */
//	@RequestMapping(value = "/findById")
//	public String findById(Model model,
//			@RequestParam(required=false, value="id",defaultValue="")Long id,
//			@RequestParam(required=false, value="div",defaultValue="")String div){
//		Account account = testService.findAcctById(id);
//		model.addAttribute("account", account);
//		model.addAttribute("div", div);
//		return "/test/editAcct";
//	}
//	
//	
//	
//	 /**
//     * 编辑或修改用户
//     * @param accountVo
//     * @param model
//     * @return
//     */
//    @RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST)
//    public @ResponseBody Map<String, String> saveOrUpdate(@ModelAttribute Account account, Model model,
//    		@RequestParam(required=false, value="div",defaultValue="")String div) {
//    	Map<String, String> map = Maps.newHashMap();
//    	
//        if (account.getId() == null) {
//        	testService.save(account);
//        } else {
//            Account dbAccount = testService.findAcctById(account.getId());
//            if (dbAccount == null) {
//            	map.put(message, "false");
//            	map.put("msg", "获取对象为空！");
//            }else if (StringUtils.isNotBlank(account.getPassword())) {
//            	if (!dbAccount.getPassword().equals(account.getOldpassword())) {
//            		map.put(message, "false");
//            		map.put("msg", "密码错误！");
//                } else {
//                    dbAccount.setPassword(account.getPassword());
//                    dbAccount.setId(account.getId());
//                    dbAccount.setLoginName(account.getLoginName());
//                    dbAccount.setRealName(account.getRealName());
//                    dbAccount.setCellPhone(account.getCellPhone());
//                    dbAccount.setQq(account.getQq());
//                    dbAccount.setEmail(account.getEmail());
//                    testService.update(dbAccount);
//                    map.put(message, "true");
//                }
//            } else {
//                dbAccount.setId(account.getId());
//                dbAccount.setLoginName(account.getLoginName());
//                dbAccount.setRealName(account.getRealName());
//                dbAccount.setCellPhone(account.getCellPhone());
//                dbAccount.setQq(account.getQq());
//                dbAccount.setEmail(account.getEmail());
//                testService.update(dbAccount);
//                map.put(message, "true");
//            }
//        }
//        return map;
//    }
//    
//    
//    /**
//     * 删除用户
//     * @param ids
//     * @return
//     */
//    @RequestMapping(value = "/delDemo", method = RequestMethod.POST)
//    public @ResponseBody Map<String, String> delDemo(@RequestParam(value="ids")String ids, Model model) {
//        
//    	Map<String, String> map = Maps.newHashMap();
//    	
//    	if (StringUtils.isBlank(ids)) {
//            model.addAttribute("result", "ID为空");
//            map.put("result", "ID为空");
//            map.put(message, "true");
//            return map;
//        }
//        String[] idArray = StringUtils.split(ids, ",");
//        for (String idStr : idArray) {
//        	testService.delete(Long.parseLong(idStr));
//        }
//        map.put("result", "删除成功");
//        map.put(message, "true");
//        return map;
//    }
//	
//}