package com.leimingtech.admin.utils;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.formula.functions.T;

public class ExportExcelUtils {
	
	public static void export(List<?> list,String url) throws Exception{
		export(list,null,url);
	}

	//导出Excal
	public static void export(List<?> list, List<String> propertyNames, String url) throws Exception{
		
		Object obj = list.get(0);
		
		// 第一步，创建一个webbook，对应一个Excel文件  
        HSSFWorkbook wb = new HSSFWorkbook();  
        // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet  
        HSSFSheet sheet = wb.createSheet(obj.getClass().getSimpleName() + "表");  
        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short  
        HSSFRow row = sheet.createRow((int) 0);  
        // 第四步，创建单元格，并设置值表头 设置表头居中  
        HSSFCellStyle style = wb.createCellStyle();  
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式  
        //获取list中的对象所有属性的集合
        Field[] fields = obj.getClass().getDeclaredFields();        
        //获得这个对象属性的数目
      	int num = fields.length;
      	
      	//判断属性名字是不是为空
      	if(propertyNames != null &&  num == propertyNames.size()){      		
      		for(int i = 0; i < num; i++){
            	HSSFCell cell = row.createCell((short) i);  
                cell.setCellValue(propertyNames.get(i));  
                cell.setCellStyle(style); 
            }
      	}
      	else{
            for(int i = 0; i < num; i++){
            	HSSFCell cell = row.createCell((short) i);  
                cell.setCellValue(fields[i].getName());  
                cell.setCellStyle(style); 
            }
      	}
               	
        
        for (int i = 0; i < list.size(); i++){  
        	
        	//拿到当前的对象
        	obj = list.get(i);
        	
        	//拿到全类名,通过反射获取这个类
            Class<?> clazz = Class.forName(obj.getClass().getCanonicalName());
            //得到所有属性的集合
            fields = clazz.getDeclaredFields();       	       	
        	
        	//创建一行
            row = sheet.createRow((int) i + 1);  
            
            //创建单元格，并设置值  
            for(int j = 0; j < fields.length; j++){
            	
            	Field field = fields[j];
            	
            	PropertyDescriptor pd = new PropertyDescriptor(field.getName(), clazz);
            	
            	Method rm = pd.getReadMethod();
            	
            	if (rm.invoke(obj)  instanceof Integer) {
            		Integer number = (Integer) rm.invoke(obj);		
            		row.createCell((short) j).setCellValue(number);
				}
            	if(rm.invoke(obj)  instanceof String){
            		String str = (String) rm.invoke(obj);
            		row.createCell((short) j).setCellValue(str);
            	}
            	
            	if(rm.invoke(obj)  instanceof Timestamp){
            		String str = rm.invoke(obj) + "";
            		row.createCell((short) j).setCellValue(str);
            	}
            	
            }
             
        }  
        // 第六步，将文件存到指定位置  
        try  
        {  
            FileOutputStream fout = new FileOutputStream(url + ".xls");  
            wb.write(fout);  
            fout.close();  
        }  
        catch (Exception e)  
        {  
            e.printStackTrace();  
        }
	}
	
}
