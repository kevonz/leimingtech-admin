package com.leimingtech.admin.utils;
 
import java.beans.PropertyDescriptor;
import java.io.IOException;      
import java.io.InputStream;      
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.DateFormat;  
import java.text.SimpleDateFormat;  
import java.util.ArrayList;
import java.util.Date;        
import java.util.List;

import lombok.extern.slf4j.Slf4j;
     
import org.apache.lucene.index.Fields;
import org.apache.poi.hssf.usermodel.HSSFCell;      
import org.apache.poi.hssf.usermodel.HSSFDateUtil;  
import org.apache.poi.hssf.usermodel.HSSFRow;      
import org.apache.poi.hssf.usermodel.HSSFSheet;      
import org.apache.poi.hssf.usermodel.HSSFWorkbook;      
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

@Slf4j
public class ImportExcelUtils {   
	
	/**    
     * 读取Excel表格表头的内容    
     * @param InputStream    
     * @return String 表头内容的数组    
	 * @throws Exception 
     *     
     */ 
	public static List<?> readExcelTitle(InputStream in, Object obj)throws Exception{    
		POIFSFileSystem fs = new POIFSFileSystem(in);;      
	    HSSFWorkbook wb = new HSSFWorkbook(fs);      
	    HSSFSheet sheet = wb.getSheetAt(0);  
	    HSSFRow row = sheet.getRow(0);
	    //获得object类的属性
	    Class<?> clazz = Class.forName(obj.getClass().getCanonicalName());	    
	    Field[] field = clazz.getDeclaredFields();
        
        //标题总列数      
        int colNum = row.getPhysicalNumberOfCells();//Excal中有多少列
        //判断对象属性和excal的属性的数目是否相同
        if(field.length != colNum){
        	  return null;
        }
        //创建List容器
        List<Object> list = new ArrayList();
        //循环,将Excal中的数据放入到对象中
        for(int i = 0; i < sheet.getLastRowNum(); i++){	         	
        	//得到excal内的一行
        	row = sheet.getRow(i + 1);
        	for(int j = 0; j < colNum; j++){
        		field[j].setAccessible(true);
        		//得到Excal中的一个单元格
            	HSSFCell cell = row.getCell(j);
            	//判断是否和类中的属性的类型相同,如果相同那么将excal中的值赋给obj
            	//PropertyDescriptor pd = new PropertyDescriptor(field[j].getName(), clazz);
            	//System.out.println(field[j].getName());
            	//Method method = pd.getWriteMethod();
            	try{
            		switch(cell.getCellType()) {
            			case HSSFCell.CELL_TYPE_NUMERIC://如果是数字
	                		Double d = cell.getNumericCellValue();       
	                		field[j].set(obj, d.intValue());
	                		break;
	                	case HSSFCell.CELL_TYPE_STRING://如果是字符串
	                		String str = cell.getRichStringCellValue().getString();
	                		field[j].set(obj, str);
	                		break;
	                	case HSSFCell.CELL_TYPE_FORMULA://如果是日期
	                		String time = cell.getCellFormula();
	                		log.debug(time);
	                		field[j].set(obj, Timestamp.valueOf(time));               	
            		}
            	}catch(Exception e){
            		try{
            			if(cell.getCellType() == HSSFCell.CELL_TYPE_STRING){
            				String time = cell.getRichStringCellValue().getString();
            				field[j].set(obj, Timestamp.valueOf(time));
                			continue;
            			}
            		}catch(Exception e2){
            			field[j].set(obj, null);
            			continue;
            			
            		}
            		field[j].set(obj, null);
            	}
        	}
        	list.add(obj);  
        	obj = obj.getClass().newInstance();
        }             
        	return list;      
		}
	
	
	 
	   	 
	
	
}
