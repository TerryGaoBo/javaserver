package com.dol.cdf.common.gamefunction.parameter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.dol.cdf.common.StringHelper;
import com.dol.cdf.common.bean.VariousItemEntry;
import com.dol.cdf.common.bean.VariousItemUtil;
import com.dol.cdf.common.gamefunction.operator.GFIOperator;

public class GFIParameterTable {
	
	private Map<String,ParamDefine> paramsMap;
	
	public GFIParameterTable(String desc) {
		paramsMap = new HashMap<String,ParamDefine>();
		
		if(desc.trim().length() == 0)
		{
			return;
		}
		
		String[] params = desc.split(",");
		for(String param : params){
			String[] paramSep = param.split(":");
			if(paramSep.length >=2 && paramSep.length <=3){
				String name = paramSep[0].trim();
				String type = paramSep[1].trim();
				String defParam = null;
				if(paramSep.length == 3){
					defParam = paramSep[2].trim();
				}
				
				paramsMap.put(name, new ParamDefine(name, type, defParam));
			}else{
				throw new RuntimeException("GFI参数初始化错误，格式不正确");
			}
		}
	}
	
	public Object getParamValue(String key, String value){
		ParamDefine paramDefine = paramsMap.get(key);
		if(paramDefine == null){
			throw new RuntimeException("GFI参数创建错误，使用了未定义参数的key：" + key);
		}
		String type = paramDefine.getType();
		return getParamValueOfType(type, value);
	}
	
	private static Object getParamValueOfType(String type, String value){
		if (("int").equalsIgnoreCase(type)) {
			try {
				return Integer.parseInt(value);
			} catch (Exception e) {
				return Integer.parseInt(value,16);
			} 
		} else if (("float").equalsIgnoreCase(type)) {
			return Float.parseFloat(value);
		} else if (("String").equalsIgnoreCase(type)) {
			return value;
		} else if (("operator").equalsIgnoreCase(type)) {
			return GFIOperator.createGfiOperator(value);
		} else if ("intarray".equalsIgnoreCase(type)) {
			String[] values = value.split(";");
			int[] ret = StringHelper.getIntList(values);
			return ret;
		} else if ("stringarray".equalsIgnoreCase(type)) {
			String[] values = value.split(";");
			return values;
		}else if ("various".equalsIgnoreCase(type)) {
			VariousItemEntry[] itemArray = VariousItemUtil.parse1(value);
			return itemArray;
		}else{
			throw new RuntimeException("GFI参数创建错误,使用的非法参数表：" + type);
		}
		
	}

	public static VariousItemEntry[] variousFormat(String value) {
		value = value.substring(1, value.length() - 1);
		String[] stringArray = value.split("><");
		VariousItemEntry[] itemArray = new VariousItemEntry[stringArray.length];
		for (int i = 0; i < stringArray.length; i++) {
			String[] realItem = stringArray[i].split(";");
			String key = realItem[0];
			int count = Integer.parseInt(realItem[1]);
			itemArray[i] = new VariousItemEntry(key, count);
		}
		return itemArray;
	}

	public Collection<ParamDefine> getAllParamsDefine(){
		return paramsMap.values();
	}

	public class ParamDefine{
		private String name;
		private String type;
		private Object defaultValue;
		public ParamDefine(String name, String type, String defaultValue) {
			this.name = name;
			this.type = type;
			if(defaultValue != null){
				this.defaultValue =getParamValueOfType(type, defaultValue);
			}
			
		}
		public String getName() {
			return name;
		}
		public String getType() {
			return type;
		}
		public Object getDefaultValue() {
			return defaultValue;
		}
	}
}
