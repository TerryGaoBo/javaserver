package com.dol.cdf.common.config;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.dol.cdf.common.TimeUtil;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonEntity {
	protected static ObjectMapper mapper=null;
	static{
		mapper = new ObjectMapper();
		mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
	}
	public static Logger logger = Logger.getLogger(JsonEntity.class);
	protected File entityFile = new File(fileName());
	/**
	 * 从json文件中读取数据
	 * 
	 * @return
	 */
	public JsonEntity readValues() {
		if (!entityFile.exists()) {
			writevalues();
		}
		try {
			return mapper.readValue(entityFile, this.getClass());
		} catch (JsonParseException e) {
			logger.error("", e);
		} catch (JsonMappingException e) {
			logger.error("", e);
		} catch (IOException e) {
			logger.error("", e);
		}
		return null;
	}
	public String getFileName(long date){
		String dateSuffix = TimeUtil.formatY4_M2_D2(date);
		String targetFileName = fileName()+"."+dateSuffix;
		return targetFileName;
	}
	
	/**
	 * 从json文件中读取数据---老数据
	 * 
	 * @return
	 */
	public JsonEntity readValues(String  dateSuffix) {
		if(dateSuffix==null||dateSuffix.length()==0){
			return readValues();
		}else{
			try {
				File oldFile = new File(fileName()+"."+dateSuffix);
				if(oldFile!=null&&oldFile.exists()){
					return mapper.readValue(oldFile, this.getClass());
				}
			} catch (JsonParseException e) {
				logger.error("", e);
			} catch (JsonMappingException e) {
				logger.error("", e);
			} catch (IOException e) {
				logger.error("", e);
			}
		}
		return null;
	}
	
	/**
	 * 存储并且备份文件
	 */
	public void writevalues() {
		writevalues(true);
	}
	
	/**
	 * 是否需要备份存储json文件
	 * 
	 * @param needBackup
	 */
	public void writevalues(boolean needBackup) {
		try {
			if(needBackup){
				backUp();
			}
			mapper.writeValue(entityFile, this);
		} catch (JsonGenerationException e) {
			logger.error("", e);
		} catch (JsonMappingException e) {
			logger.error("", e);
		} catch (IOException e) {
			logger.error("", e);
		}
	}
	
	public String path() {
		String prefixProperty = System.getProperty("app.home");
		if(prefixProperty != null){
			String runtime_env = System.getProperty("runtime.env", "other");
			String path = prefixProperty + "/applogs/"+runtime_env+"/";
			File filePath = new File(path);
			if(!filePath.exists()) {
				try {
					FileUtils.forceMkdir(filePath);
				} catch (IOException e) {
					logger.error("",e);
				}
			}
			return path;
		}
		else{
			return "";
		}
	}
	
	public String fileName() {
		return path()+this.getClass().getSimpleName();
	}
	public void backUp(){
		long lastModified = entityFile.lastModified();
		String dateSuffix = TimeUtil.formatY4_M2_D2(lastModified);
		String targetFileName = fileName()+"."+dateSuffix;
//		file.renameTo(new File(targetFileName));
	    File target  = new File(targetFileName);
	    try {
//			String entityStr = FileUtils.readFileToString(entityFile);
			if (entityFile.exists()) {
				JsonNode readValue = mapper.readValue(entityFile, JsonNode.class);
				mapper.writeValue(target, readValue);
			}
		} catch (Exception e) {
			logger.error("back up Json entitty Error",e);
		}
//		try {
//			FileUtils.moveFile(entityFile, target);
//		} catch (IOException e) {
//			logger.error("back up Json entitty Error",e);
//		}
	}
	
	
	
	
}
