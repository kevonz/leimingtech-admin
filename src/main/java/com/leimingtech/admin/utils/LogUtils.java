
package com.leimingtech.admin.utils;

import javax.servlet.http.HttpServletRequest;

import com.leimingtech.core.common.Exceptions;
import com.leimingtech.core.common.IpUtil;
import com.leimingtech.core.common.SpringContextUtil;
import com.leimingtech.core.common.StringUtils;
import com.leimingtech.core.entity.base.SysLog;
import com.leimingtech.service.module.log.service.SysLogService;
import com.leimingtech.service.utils.sessionkey.CacheUser;
import com.leimingtech.service.utils.sessionkey.CacheUtil;

/**
 * 日志工具类
 * @author linjm
 * @version 2014-11-7
 */
public class LogUtils {
	
	private static SysLogService logService = SpringContextUtil.getBean(SysLogService.class);
	
	/**
	 * 保存日志
	 */
	public static void saveLog(HttpServletRequest request, String title){
		saveLog(request, null, null, title);
	}
	
	/**
	 * 保存日志
	 */
	public static void saveLog(HttpServletRequest request, Object handler, Exception ex, String title){
		CacheUser user = CacheUtil.getCacheUser();
		if (user != null && user.getAdmin() != null){
			SysLog log = new SysLog();
			log.setTitle(title);
			log.setCreateBy(user.getAdmin().getAdminName());
			log.setType(ex == null ? SysLog.TYPE_ACCESS : SysLog.TYPE_EXCEPTION);
			log.setRemoteAddr(IpUtil.getIpAddr(request));
			log.setUserAgent(request.getHeader("user-agent"));
			log.setRequestUri(StringUtils.isNotEmpty(request.getRequestURI())? request.getRequestURI() : request.getPathTranslated());
			log.setParams(request.getParameterMap()+"");
			log.setMethod(request.getMethod());
			log.setCreateTime(System.currentTimeMillis());
			// 异步保存日志
			new SaveLogThread(log, handler, ex).start();
		}
	}

	/**
	 * 保存日志线程
	 */
	public static class SaveLogThread extends Thread{
		
		private SysLog log;
		private Object handler;
		private Exception ex;
		
		public SaveLogThread(SysLog log, Object handler, Exception ex){
			super(SaveLogThread.class.getSimpleName());
			this.log = log;
			this.handler = handler;
			this.ex = ex;
		}
		
		@Override
		public void run() {
			// 获取日志标题
//			if (StringUtils.isBlank(log.getTitle())){
//				String permission = "";
//				if (handler instanceof HandlerMethod){
//					Method m = ((HandlerMethod)handler).getMethod();
//					RequiresPermissions rp = m.getAnnotation(RequiresPermissions.class);
//					permission = (rp != null ? StringUtils.join(rp.value(), ",") : "");
//				}
//				log.setTitle(getMenuNamePath(log.getRequestUri(), permission));
//			}
			// 如果有异常，设置异常信息
			log.setException(Exceptions.getStackTraceAsString(ex));
			// 如果无标题并无异常日志，则不保存信息
//			if (StringUtils.isBlank(log.getTitle()) && StringUtils.isBlank(log.getException())){
//				return;
//			}
			// 保存日志信息
			logService.save(log);
		}
	}
	
	
}
