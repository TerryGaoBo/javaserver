package com.jelly.node.datastore;

import java.text.MessageFormat;

import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dol.cdf.common.ContextConfig;

public class DataSource extends PooledDataSource{
	private static final Logger logger = LoggerFactory.getLogger(DataSource.class);
	
	String jdbcUrl="jdbc:mysql://localhost:3306/balli{0}?autoReconnect=true&autocreate=true";
 
	public void init() {
		int firstServerId = ContextConfig.getFirstServerId();
		String databaseIdx = firstServerId+"";
		if(firstServerId == 1) {
			databaseIdx = "";
		}
		String url = MessageFormat.format(jdbcUrl, databaseIdx);
		this.setUrl(url);
		logger.info("data source url:{}",url);;
	}
}
