package com.leimingtech.admin.utils;

import com.google.common.collect.Maps;
import com.leimingtech.core.jackson.JsonUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * @author llf
 * @Package com.leimingtech.admin.utils
 * @Description:弹出窗口页面回调
 * @date 2014/12/5 10:00
 */
public class ShowPageUtils {

    /**
     *
     * @param message 消息内容
     * @param url 提示完后的URL去向
     * @param extraJs 扩展JS
     */
    public static String showDialog(String message,String url,String alertType,String extraJs){

        String paramJs = "";

        if("reload".equals(url)){
            paramJs = "window.location.reload()";
        }else if(StringUtils.isNotBlank(url)){
            paramJs = "window.location.href = '"+url+"'";
        }

        if(StringUtils.isNotBlank(paramJs)){
            paramJs = "function(){"+paramJs+"}";
        }else{
            paramJs = "null";
        }
        StringBuilder extra = new StringBuilder();
        extra.append("<script type=\"text/javascript\" reload=\"1\">").append("showDialog('").
                append(message).append("','").append(alertType).append("',null,").append(paramJs)
            .append(",").append("error".equals(alertType) ? 1 : 0).append(",null,null,null,null,2,null);").append("</script>");
        if(StringUtils.isNotBlank(extraJs) && !"<script>".equals(extraJs.trim().substring(0, 8))){
            extra.append("<script type='text/javascript'' reload='1'>").append("</script>");
        }else{
            extra.append(extraJs);
        }

        StringBuilder returnXml = new StringBuilder();
        returnXml.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        returnXml.append("<root><![CDATA[");
        returnXml.append(message).append(extra)
                .append("]]></root>");
        return returnXml.toString();
    }
}
