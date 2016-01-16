package com.dol.cdf.common.config;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class BaseConfigLoadManager implements IConfigLoadManager {

	public static final Logger logger = LoggerFactory.getLogger(BaseConfigLoadManager.class);
	
	/**
	 * 读取文件方法
	 * 
	 * @param configName
	 *            配置文件名称
	 * @param type
	 *            返回值类型
	 * @return
	 */
	protected <T> List<T> readConfigFile(String configName, TypeReference<? extends Collection<?>> type) {
		ObjectMapper mapper = ObjectMapperFactory.createGameConfigObjectMapper();
		try {

			File file = new File(AllGameConfig.getConfigResource(configName));
			return mapper.readValue(file, type);
		} catch (JsonParseException e) {
			logger.error("", e);
		} catch (JsonMappingException e) {
			logger.error("", e);
		} catch (IOException e) {
			logger.error("", e);
		}
		throw new RuntimeException();
	}

}
