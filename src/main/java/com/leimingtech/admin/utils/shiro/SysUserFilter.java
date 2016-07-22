package com.leimingtech.admin.utils.shiro;

import com.leimingtech.core.entity.base.Account;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.web.filter.PathMatchingFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Created by yansheng on 2014/7/26.
 */
public class SysUserFilter extends PathMatchingFilter {

    @Override
    protected boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {

        Account account = (Account) SecurityUtils.getSubject().getPrincipal();
        request.setAttribute("user_name", account);
        return true;
    }
}
