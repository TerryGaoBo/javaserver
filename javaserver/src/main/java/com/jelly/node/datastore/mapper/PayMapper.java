package com.jelly.node.datastore.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

public interface PayMapper {
final static String tableName = "pay";
	
	@Insert("INSERT INTO " + tableName + " VALUES (#{guid},#{userId},#{channel},#{status},#{logTime},#{itemId},#{orderId},#{rmb},#{exchange},#{bouns},#{param})")
	public void insert(Pay entity); 
	
	//@Update("UPDATE " + tableName + " SET name=#{name},charId=#{charId},level=#{level},lastLogin=#{lastLogin},vipScore=#{vipScore},paid=#{paid},power=#{power},heroPow=#{heroPow} WHERE guid=#{guid}")
	//public void update(Pay entity);
	
	@Select("SELECT * FROM " + tableName)
	public List<Pay> loadAll();
}
