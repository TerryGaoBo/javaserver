package com.dol.cdf.common.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EntityObjectMapper extends ObjectMapper{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4790241091500340855L;

	public EntityObjectMapper() {
		super();
        this.setVisibilityChecker(this.getVisibilityChecker().with(JsonAutoDetect.Visibility.NONE));
        this.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
//        this.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        this.setSerializationInclusion(Include.NON_DEFAULT);  
	}
}
