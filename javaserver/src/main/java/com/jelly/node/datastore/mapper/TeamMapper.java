package com.jelly.node.datastore.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface TeamMapper {
	final String tableName = "team";
	
	@Insert("INSERT INTO " + tableName + " VALUES (#{name},#{val}) " + "ON DUPLICATE KEY UPDATE val=#{val}")
	public void insert(@Param("name") String name, @Param("val") byte[] val);
	
	@Insert("INSERT INTO " + tableName + " VALUES (#{name},#{val}) ")
	public void combin(@Param("name") String name, @Param("val") byte[] val);
	
	@Update("UPDATE " + tableName + " SET val=#{val} WHERE name=#{name}")
	public void update(@Param("name") String name, @Param("val") byte[] val);
	
	@Delete("DELETE FROM " + tableName + " WHERE name = #{name}")
	public void delete(@Param("name") String name);
	
	
	@Select("SELECT * FROM " + tableName + " WHERE name = #{name}")
	public TeamEntity getVal(@Param("name") String name);
	
	@Select("SELECT * FROM " + tableName)
	public List<TeamEntity> loadAll();
}
