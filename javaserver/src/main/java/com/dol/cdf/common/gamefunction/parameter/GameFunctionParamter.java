package com.dol.cdf.common.gamefunction.parameter;

import java.util.HashMap;
import java.util.Map;

import com.dol.cdf.common.gamefunction.parameter.GFIParameterTable.ParamDefine;

public class GameFunctionParamter implements IGameFunctionParameter {
	private Map<String,Object> paramters;

	public GameFunctionParamter(String paramDesc, GFIParameterTable paramTable) {
		paramters = new HashMap<String,Object>();
		// 参数表位空则使用都是空参数
		if(paramTable == null){
			return;
		}
		
		for(ParamDefine paramDefine : paramTable.getAllParamsDefine()){
			paramters.put(paramDefine.getName(), paramDefine.getDefaultValue());
		}
		
		// 分隔实际参数
		if(paramDesc.trim().length() != 0){
			String[] params = paramDesc.split(",");
			
			// 对每个param，添加到map中
			for(String param : params){
				int sp = param.indexOf("=");
				if(sp > 0){
					String key = param.substring(0,sp);
					String value = param.substring(sp + 1).trim();
					paramters.put(key, paramTable.getParamValue(key, value));
				}else{
					throw new RuntimeException("GFI参数创建错误，格式不正确：" + paramDesc);
				}
			}
		}
		
		for(String key : paramters.keySet()){
			if(paramters.get(key) == null){
				throw new RuntimeException("GFI参数创建错误，有参数没有缺省值，必须填写：" + key);
			}
		}
		
	}
	
	
	@Override
	public Object getParamter(String key){
		return paramters.get(key);
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((paramters == null) ? 0 : paramters.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GameFunctionParamter other = (GameFunctionParamter) obj;
		if (paramters == null) {
			if (other.paramters != null)
				return false;
		} else if (!paramters.equals(other.paramters))
			return false;
		return true;
	}
	
	
}
