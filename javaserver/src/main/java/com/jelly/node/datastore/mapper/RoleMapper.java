package com.jelly.node.datastore.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface RoleMapper {
	
	final static String tableName = "role";
	
	@Insert("INSERT INTO " + tableName + " VALUES (#{guid},#{net},#{channel},#{userId},#{name},#{charId},#{level},#{firstLogin},#{lastLogin},#{vipScore},#{paid},#{power},#{heroPow},#{gold},#{silver},#{coin},#{exp},#{examLv},#{chapter},#{stage},#{teamName})")
	public void insert(RoleEntity entity); 
	
	@Update("UPDATE " + tableName + " SET name=#{name},charId=#{charId},level=#{level},lastLogin=#{lastLogin},vipScore=#{vipScore},paid=#{paid},power=#{power},heroPow=#{heroPow},gold=#{gold},silver=#{silver},coin=#{coin},exp=#{exp},examLv=#{examLv},chapter=#{chapter},stage=#{stage},teamName=#{teamName} WHERE guid=#{guid}")
	public void update(RoleEntity entity);
	
	@Delete("DELETE FROM " + tableName + " WHERE guid = #{guid}")
	public void delete(String guid);
	
	@Select("SELECT * FROM " + tableName)
	public List<RoleEntity> loadAll();
	
	@Select("SELECT * FROM " + tableName +" ORDER BY chapter desc , stage desc LIMIT 100")
	public List<RoleEntity> loadAdventureTop100();
	
	@Select("SELECT * FROM " + tableName +" ORDER BY examLv desc LIMIT 100")
	public List<RoleEntity> loadExamTop100();
	
	@Select("SELECT * FROM " + tableName +" ORDER BY level desc , exp desc LIMIT 100")
	public List<RoleEntity> loadLevelTop100();
	
	@Select("SELECT * FROM " + tableName +" ORDER BY power desc LIMIT 5000")
	public List<RoleEntity> loadPowerTop50000();
	
	@Select("SELECT * FROM " + tableName +" ORDER BY gold desc LIMIT 100")
	public List<RoleEntity> loadGoldTop100();
	
}