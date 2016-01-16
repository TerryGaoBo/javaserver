package com.dol.cdf.common;

import io.nadron.app.Player;

import com.dol.cdf.common.config.AllGameConfig;
import com.dol.cdf.common.config.EntityObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class DynamicJsonProperty {
	
	public static final ObjectMapper jackson = new EntityObjectMapper(); 
	
	protected static final AllGameConfig config = AllGameConfig.getInstance();
	/**
	 * 与客户端通讯使用的json
	 */
	protected ObjectNode json;

	
	 public DynamicJsonProperty() {
		 json = jackson.createObjectNode();
	}

	public void addChange(String key, String value) {
		json.put(key, value);
	}
	 
	public void addChange(String key, Integer value) {
		json.put(key, value);
	}
	
	public void addChange(int key, Integer value) {
		json.put(key+"", value);
	}
	
	public void addChange(String key, int value) {
		json.put(key, value);
	}
	
	public void addChange(String key, JsonNode value) {
		json.put(key, value);
	}
	public void addChange(int key, JsonNode value) {
		json.put(key+"", value);
	}

	public void appendChange(int key, JsonNode value) {
		json.withArray(key+"").add(value);
	}

	public void appendChangeMap(String jsonKey, String mapKey, JsonNode mapValue){
		json.with(jsonKey).put(mapKey, mapValue);
	}
	
	public void appendChangeMap(String jsonKey, Integer mapKey, Object mapValue){
		json.with(jsonKey).put(mapKey+"", convertToJsonNode(mapValue));
	}
	public void appendChangeMap(Integer jsonKey, Integer mapKey, Object mapValue){
		json.with(jsonKey+"").put(mapKey+"", convertToJsonNode(mapValue));
	}
	
	public void appendChangeMap(String jsonKey, Integer mapKey, JsonNode mapValue){
		json.with(jsonKey).put(mapKey+"", mapValue);
	}
	
	/**
	 * 清理动态的通讯信息内容
	 */
	public void clearChanged() {
		json.removeAll();
	}

	/**
	 * 
	 *
	 * @return
	 */
	abstract public String getKey();

	public static ArrayNode convertToArrayNode(Object obj){
		return (ArrayNode)jackson.convertValue(obj, new TypeReference<ArrayNode>() {
		});
	}
	
	public static ObjectNode convertToObjectNode(Object obj){
		return (ObjectNode)jackson.convertValue(obj, new TypeReference<ObjectNode>() {
		});
	}
	
	public static JsonNode convertToJsonNode(Object obj){
		return (JsonNode)jackson.convertValue(obj, new TypeReference<JsonNode>() {
		});
	}
	
	
	
	/**
	 * 是否更改过
	 *
	 * @return
	 */
	public boolean hasChanged() {
		return json.size() > 0;
	}

	/**
	 * 所有的属性，子类包含客户端需要的属性则覆盖此方法,用于玩家登录，发送给客户端的数据
	 *
	 * @param obj
	 */
	abstract public JsonNode toWholeJson();
	
	/**
	 * 用于建筑打开的时候，发送给客户端的数据
	 * @return
	 */
	public JsonNode toOpenJson(Player player) {
		return null;
	}
	

	public ObjectNode getJsonValue() {
		return json;
	}


}
