package com.dol.cdf.tools.modelGenerate.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.dol.cdf.tools.modelGenerate.model.Excel2JavaPropertiesModel;

public class ModelGenUtil {
	public static Logger logger = Logger.getLogger(ModelGenUtil.class);
	
	/**
	 * 首字母转小写
	 * 
	 * @param s
	 * @return
	 */
    public static String toLowerCaseFirstOne(String s)
    {
        if(Character.isLowerCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
    }

	/**
	 * 首字母转大写
	 * 
	 * @param s
	 * @return
	 */
    public static String toUpperCaseFirstOne(String s)
    {
        if(Character.isUpperCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
    }
    
    
	public static void writeJavaFile(String outFilePath,String className,List<Excel2JavaPropertiesModel> models){
		StringBuilder sb=new StringBuilder();
		sb.append("/**").append("\n")
		.append("* auto generated, do not change it !!!!!").append("\n")
		.append("*/").append("\n")
		.append("package com.dol.cdf.common.bean;").append("\n")
		.append("import com.dol.cdf.common.*;").append("\n");
		if (className.equals("Accessory") || className.equals("Item")
				||className.equals("Gem") || className.equals("Material")) {
			sb.append("public class ").append(toUpperCaseFirstOne(className)).append(" implements BaseItem {").append("\n");
		} else {
			sb.append("public class ").append(toUpperCaseFirstOne(className)).append(" {").append("\n");
		}
		for(Excel2JavaPropertiesModel model:models){
			sb.append("\t").append("/**"+model.getDes()+"*/").append("\n")
			.append("\t").append("private "+model.getType()+" "+model.getName()+";").append("\n");
		}
		for(Excel2JavaPropertiesModel model:models){
			//get
			sb.append("\t").append("public "+model.getType()+" "+"get"+toUpperCaseFirstOne(model.getName())+"(){").append("\n")
		    .append("\t").append("\t").append(" return this."+model.getName()+";").append("\n")
		    .append("\t").append("}").append("\n")
		    //set
		    .append("\t").append("public  void set"+toUpperCaseFirstOne(model.getName())+"("+model.getType()+" "+model.getName()+"){").append("\n")
		    .append("\t").append("\t").append("this."+model.getName()+" = "+model.getName()+" ;").append("\n")
			.append("\t").append("}").append("\n");
		}
		sb.append("\t").append("public String toString(){").append("\n")
		.append("\t").append("\t").append("return StringHelper.obj2String(this, null);").append("\n")
		.append("\t").append("}").append("\n");
		
		sb.append("}").append("\n");
		File file=new File(outFilePath+"/"+toUpperCaseFirstOne(className)+".java");
		try {
			if(file.exists()){
				file.delete();
			}
			FileUtils.writeStringToFile(file, sb.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

	public static List<Excel2JavaPropertiesModel> loadExcel(File file) {
		InputStream is = null;// 输入流对象
		String cellStr = null;// 单元格，最终按字符串处理
		List<Excel2JavaPropertiesModel> excelModels = new ArrayList<Excel2JavaPropertiesModel>();
		Excel2JavaPropertiesModel excelModel = null;
		try {
			is = new FileInputStream(file);// 获取文件输入流
			HSSFWorkbook workbook2003 = new HSSFWorkbook(is);// 创建Excel2003文件对象
			HSSFSheet sheet = workbook2003.getSheetAt(0);// 取出第一个工作表，索引是0
			// 开始循环遍历行，表头不处理，从1开始
			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
				HSSFRow row = sheet.getRow(i);// 获取行对象
				if (row == null || row.getCell(0) == null) {// 如果为空，不处理
					return excelModels;
				}
				excelModel=new Excel2JavaPropertiesModel();
				// 循环遍历单元格
				for (int j = 0; j < row.getLastCellNum(); j++) {
					HSSFCell cell = row.getCell(j);// 获取单元格对象
					if (cell == null) {// 单元格为空设置cellStr为空串
						cellStr = "";
					} else if (cell.getCellType() == HSSFCell.CELL_TYPE_BOOLEAN) {// 对布尔值的处理
						cellStr = String.valueOf(cell.getBooleanCellValue());
					} else if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {// 对数字值的处理
						cellStr = cell.getNumericCellValue() + "";
					} else {// 其余按照字符串处理
						cellStr = cell.getStringCellValue();
					}
					// 下面按照数据出现位置封装到bean中
					if(j==0){
						excelModel.setDes(cellStr);
					}else if (j == 1) {
						excelModel.setName(cellStr);
					} else if (j == 2) {
						excelModel.setType(cellStr);
					} 
				}
				if(excelModel.getDes()!=null&&excelModel.getDes().length()>0){
					excelModels.add(excelModel);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {// 关闭文件流
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return excelModels;
	}
}
