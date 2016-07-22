package com.leimingtech.admin.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import com.leimingtech.admin.utils.shiro.AccountAuthorizationRealm.Principal;
import com.leimingtech.core.common.SpringContextUtil;
import com.leimingtech.core.common.StringUtils;
import com.leimingtech.core.entity.MenuClass;
import com.leimingtech.core.entity.base.Admin;
import com.leimingtech.core.entity.base.Roles;
import com.leimingtech.service.module.admin.dao.AdminDao;
import com.leimingtech.service.module.admin.dao.MenuClassDao;
import com.leimingtech.service.module.admin.dao.RoleDao;


/**
 * 用户工具类
 * @author wanghao
 * @version 2015-10-23
 */
public class UserUtils {

	private static AdminDao adminDao = SpringContextUtil.getBean(AdminDao.class);
	private static RoleDao roleDao = SpringContextUtil.getBean(RoleDao.class);
	private static MenuClassDao menuDao = SpringContextUtil.getBean(MenuClassDao.class);
	
	public static final String USER_CACHE = "userCache";
	public static final String USER_CACHE_ID_ = "id_";
	public static final String USER_CACHE_LOGIN_NAME_ = "ln";

	public static final String CACHE_ROLE_LIST = "roleList";
	public static final String CACHE_MENU_LIST = "menuList";
	

	
	/**
	 * 根据ID获取用户
	 * @param id
	 * @return 取不到返回null
	 */
	public static Admin get(Integer id){
		Admin user = (Admin)getCache(USER_CACHE+ USER_CACHE_ID_+ id);
		if (user ==  null){
			user = adminDao.findAdminById(id);
			if (user == null){
				return null;
			}
			putCache(USER_CACHE+ USER_CACHE_ID_+ user.getAdminId(), user);
			putCache(USER_CACHE+ USER_CACHE_LOGIN_NAME_+ user.getAdminName(), user);
		}
		return user;
	}
	
	/**
	 * 根据登录名获取用户
	 * @param loginName
	 * @return 取不到返回null
	 */
	public static Admin getByLoginName(String loginName){
		Admin user = (Admin)getCache(USER_CACHE+ USER_CACHE_LOGIN_NAME_+ loginName);
		if (user == null){
			user = adminDao.findByAdminName(loginName);
			if (user == null){
				return null;
			}
			putCache(USER_CACHE+ USER_CACHE_ID_ + user.getAdminId(), user);
			putCache(USER_CACHE+ USER_CACHE_LOGIN_NAME_ + user.getAdminName(), user);
		}
		return user;
	}
	
	/**
	 * 清除当前用户缓存
	 */
	public static void clearCache(){
		removeCache(CACHE_ROLE_LIST);
		removeCache(CACHE_MENU_LIST);
		UserUtils.clearCache(getUser());
	}
	
	/**
	 * 清除指定用户缓存
	 * @param user
	 */
	public static void clearCache(Admin user){
		removeCache(USER_CACHE+ USER_CACHE_ID_ + user.getAdminId());
		removeCache(USER_CACHE+ USER_CACHE_LOGIN_NAME_ + user.getAdminName());
	}
	
	/**
	 * 获取当前用户
	 * @return 取不到返回 new Admin()
	 */
	public static Admin getUser(){
		Principal principal = getPrincipal();
		if (principal!=null){
			Admin user = get(Integer.valueOf(principal.getId()));
			if (user != null){
				return user;
			}
			return new Admin();
		}
		// 如果没有登录，则返回实例化空的User对象。
		return new Admin();
	}

	/**
	 * 获取当前用户角色列表
	 * @return
	 */
	public static List<Roles> getRoleList(){
		List<Roles> roleList = (List<Roles>)getCache(CACHE_ROLE_LIST);
		if (roleList == null){
			Admin user = getUser();
			if (user.getAdminIsSuper()!=null&&user.getAdminIsSuper().equals(1)){
				roleList = roleDao.findList();
			}else{
				String roleStr = user.getRoleid();
				if(StringUtils.isNotEmpty(roleStr)){
					roleList = new ArrayList<Roles>();
					for (String str : roleStr.split(",")) {
						Roles role = roleDao.findShopRoleById(Integer.valueOf(str));
						roleList.add(role);
					}
				}
			}
			putCache(CACHE_ROLE_LIST, roleList);
		}
		return roleList;
	}
	
	/**
	 * 获取当前用户授权菜单
	 * @return
	 */
	public static List<MenuClass> getMenuList(){
		List<MenuClass> menuList = (List<MenuClass>)getCache(CACHE_MENU_LIST);
		if (menuList == null){
			Admin user = getUser();
			if (user.getAdminIsSuper()!=null&&user.getAdminIsSuper().equals(1)){
				menuList = menuDao.findAll();
			}else{
				String roleStr = user.getRoleid();
				menuList = menuDao.findByRoleids(roleStr.endsWith(",")?roleStr.substring(0, roleStr.length()-1):roleStr,null);
			}
			putCache(CACHE_MENU_LIST, menuList);
		}
		return menuList;
	}
	
	/**
	 * 获取当前用户授权可显示菜单
	 * @return
	 */
	public static List<MenuClass> getShowMenuList(){
		List<MenuClass> menuList = (List<MenuClass>)getCache(CACHE_MENU_LIST);
		if (menuList == null){
			Admin user = getUser();
			if (user.getAdminIsSuper()!=null&&user.getAdminIsSuper().equals(1)){
				menuList = menuDao.findList(1);//isshow 是1的时候表示菜单  0的时候是功能
			}else{
				String roleStr = user.getRoleid();
				menuList = menuDao.findByRoleids(roleStr.endsWith(",")?roleStr.substring(0, roleStr.length()-1):roleStr,1);
			}
			putCache(CACHE_MENU_LIST, menuList);
		}
		return menuList;
	}
	
	/**
	 * 获取授权主要对象
	 */
	public static Subject getSubject(){
		return SecurityUtils.getSubject();
	}
	
	/**
	 * 获取当前登录者对象
	 */
	public static Principal getPrincipal(){
		try{
			Subject subject = SecurityUtils.getSubject();
			Principal principal = (Principal)subject.getPrincipal();
			if (principal != null){
				return principal;
			}
		}catch (UnavailableSecurityManagerException e) {
			
		}catch (InvalidSessionException e){
			
		}
		return null;
	}
	
	public static Session getSession(){
		try{
			Subject subject = SecurityUtils.getSubject();
			Session session = subject.getSession(false);
			if (session == null){
				session = subject.getSession();
			}
			if (session != null){
				return session;
			}
//			subject.logout();
		}catch (InvalidSessionException e){
			
		}
		return null;
	}
	
	// ============== User Cache ==============
	
	public static Object getCache(String key) {
		return getCache(key, null);
	}
	
	public static Object getCache(String key, Object defaultValue) {
//		Object obj = getCacheMap().get(key);
		Object obj = getSession().getAttribute(key);
		return obj==null?defaultValue:obj;
	}

	public static void putCache(String key, Object value) {
//		getCacheMap().put(key, value);
		getSession().setAttribute(key, value);
	}

	public static void removeCache(String key) {
//		getCacheMap().remove(key);
		getSession().removeAttribute(key);
	}
	
//	public static Map<String, Object> getCacheMap(){
//		Principal principal = getPrincipal();
//		if(principal!=null){
//			return principal.getCacheMap();
//		}
//		return new HashMap<String, Object>();
//	}
	
}
