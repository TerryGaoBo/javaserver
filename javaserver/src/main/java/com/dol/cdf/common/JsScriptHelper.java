package com.dol.cdf.common;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsScriptHelper {
	
	private static final Logger logger = LoggerFactory.getLogger(JsScriptHelper.class);
	
	private static final ScriptEngineManager manager = new ScriptEngineManager();
	
	public static Object executeScriptFile(String path, Map<String,Object> params){
		ScriptEngine engine = manager.getEngineByName("JavaScript");
		if(params != null)
		{
			for(Map.Entry<String, Object> pair : params.entrySet())
			{
				engine.put(pair.getKey(), pair.getValue());
			}
		}
		
		InputStreamReader reader = null;
		try {
			reader = new InputStreamReader(new FileInputStream(path),"UTF-8");
			try {
				return engine.eval(reader);
			} catch (ScriptException e) {
				logger.error("",e);
			}
		} catch (UnsupportedEncodingException e) {
			logger.error("",e);
		} catch (FileNotFoundException e) {
			logger.error("",e);
		}
		finally{
			if(reader !=null)
			{
				try {
					reader.close();
				} catch (IOException e) {
					logger.error("",e);
				}
			}
		}
		return null;
	}
	
	/**
	 * 执行一个脚本文件
	 * 
	 * @param path		脚本文件的路径
	 * @param params	执行参数
	 * @return			脚本执行结果的返回值
	 */
	public static Object executeScriptFile1(String path, java.util.Map<String, Object> params) throws Exception{
		ScriptEngine engine = manager.getEngineByName("JavaScript");
		if(params != null){
			for(Map.Entry<String, Object> pair : params.entrySet()){
				engine.put(pair.getKey(), pair.getValue());
			}
		}
		InputStreamReader reader = null;

		reader = new InputStreamReader(new FileInputStream(path), "UTF-8");
		return engine.eval(reader);
		
	}
}
