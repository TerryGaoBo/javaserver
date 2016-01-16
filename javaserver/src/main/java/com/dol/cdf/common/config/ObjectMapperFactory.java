package com.dol.cdf.common.config;

import java.util.List;

import com.dol.cdf.common.bean.VariousItemEntry;
import com.dol.cdf.common.bean.VariousItemRateEntry;
import com.dol.cdf.common.collect.Range;
import com.dol.cdf.common.config.BeanDeserializerFactory.ConditionGFArrayDeserializer;
import com.dol.cdf.common.config.BeanDeserializerFactory.EffectGFArrayDeserializer;
import com.dol.cdf.common.config.BeanDeserializerFactory.IntArrayDeserializer;
import com.dol.cdf.common.config.BeanDeserializerFactory.ListArrayDeserializer;
import com.dol.cdf.common.config.BeanDeserializerFactory.RangeDeserializer;
import com.dol.cdf.common.config.BeanDeserializerFactory.StringArrayDeserializer;
import com.dol.cdf.common.config.BeanDeserializerFactory.VariousItemEntryDeserializer;
import com.dol.cdf.common.config.BeanDeserializerFactory.VariousItemRateEntryDeserializer;
import com.dol.cdf.common.gamefunction.IConditionGF;
import com.dol.cdf.common.gamefunction.IEffectGF;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class ObjectMapperFactory {

	/**
	 * 创建用于解析json的ObjectMapper
	 * 
	 * @return
	 */
	public static ObjectMapper createGameConfigObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		// json中没有此属性则不会反序列化到java
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		SimpleModule mod = new SimpleModule("gconfig", Version.unknownVersion());
		registerDeserializer(mod);
		objectMapper.registerModule(mod);
		return objectMapper;
	}

	/**
	 * 注册对象从json反序列化的方法
	 * 
	 * @param mod
	 */
	private static void registerDeserializer(SimpleModule mod) {
		mod.addDeserializer(int[].class, new IntArrayDeserializer());
		mod.addDeserializer(String[].class, new StringArrayDeserializer());
		mod.addDeserializer(Range.class, new RangeDeserializer());
		mod.addDeserializer(IConditionGF[].class, new ConditionGFArrayDeserializer());
		mod.addDeserializer(IEffectGF[].class, new EffectGFArrayDeserializer());
		mod.addDeserializer(VariousItemEntry[].class, new VariousItemEntryDeserializer());
		mod.addDeserializer(VariousItemRateEntry[].class, new VariousItemRateEntryDeserializer());
		mod.addDeserializer(List[].class, new ListArrayDeserializer());
	}
	
	public static ObjectMapper createGameCombatObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		objectMapper.disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
		objectMapper.disable(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS);
		objectMapper.setSerializationInclusion(Include.NON_DEFAULT);  
		objectMapper.setSerializationInclusion(Include.NON_EMPTY);  
		objectMapper.setSerializationInclusion(Include.NON_NULL);  
		return objectMapper;
	}

}
