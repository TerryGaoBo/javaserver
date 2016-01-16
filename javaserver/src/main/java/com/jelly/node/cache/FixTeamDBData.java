package com.jelly.node.cache;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dol.cdf.common.DynamicJsonProperty;
import com.google.common.collect.Lists;
import com.jelly.node.datastore.mapper.TeamEntity;
import com.jelly.node.datastore.mapper.TeamMapper;
import com.jelly.team.Team;
import com.mysql.jdbc.Connection;

public class FixTeamDBData {
	
	private static final Logger logger = LoggerFactory.getLogger(FixTeamDBData.class);
	
	public static final String DBUSER = "root";  

    public static final String DBPASS = "dre@mJ$11y";
//	public static final String DBPASS = "";
    
    public static void main(String[] args) {
    }
    
    public static Connection getConnection(int sid) {
    	Connection conn = null;
    	try {
			String DBURL = "jdbc:mysql://localhost:3306/balli"; 
			if (sid != 1) {
				DBURL += sid;
			}
//			System.out.println(sid + " : " + DBURL);
			logger.info("sid={}",DBURL);
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = (Connection) DriverManager.getConnection(DBURL,DBUSER,DBPASS); //2、连接数据库  
			return conn;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
//    	logger.error("----------------连接" + sid + "服失败！");
    	return conn;
    }
    
    /**
     * 查询某个服的军团数据
     * @param conn
     * @return
     */
    public static List<TeamEntity> getTeams(int sid, Connection conn) {
		List<TeamEntity> list = new ArrayList<TeamEntity>();
		try {
			String sql = "select * from balli";
			if (sid != 1) {
				sql += sid;
			}
			sql += ".team";
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String name = rs.getString("name");
				byte[] val = rs.getBytes("val");
				TeamEntity t = new TeamEntity();
				t.setName(name);
				t.setVal(val);
				list.add(t);
			}
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
		} catch (Exception e) {
//			logger.error("查询{}服team出错", sid);
			e.printStackTrace();
		}
		return list;
	}
    
    public static void deleteTeamDataDB(TeamMapper mapper)
    {
//    	List<String> s = Lists.newArrayList();
//    	s.add("Duang-");
//    	s.add("NARUTO-");
//    	for(String ks : s){
//    		mapper.delete(ks);
//    	}
    	
    	/// android
//    	s.add("NARUTO-");
//    	s.add("lol-");
//    	s.add("love^宏-");
//    	s.add("Uchiha-");
//    	s.add("king-");
//    	s.add("UCIHA-");
//    	s.add("Indo-");
//    	s.add("滴"); // 这三个军团没有团战，已经不存在，团长在数据库中也没存在了，这三个军团没什么人
//    	s.add("斗破苍穹");
//    	s.add("。。。刷");
//    	s.add("六道：家族");
//    	s.add("PoY");
//    	for(String ks : s){
//    		mapper.delete(ks);
//    	}
    	
//    	try{
//    		Connection conn = FixTeamDBData.getConnection(1);
//    		List<TeamEntity> list = FixTeamDBData.getTeams(1, conn);
//    		List<String> tempList = Lists.newArrayList();
//    		for(TeamEntity ent : list){
////    			tempList.add(ent.getName());
//    			
//    			String dbname=ent.getName();
//    			String oldname="✘✘✘✘✘";
//    			if(dbname.equals(oldname)){
//    				dbname = "xxxxx~";
//    				logger.info("delelellelelel");
//    				Team t = DynamicJsonProperty.jackson.readValue(ent.getVal(), Team.class);
//    				t.setName(dbname);
//    				ent.setName(dbname);
//    				FixTeamDBData.updateTeamName(conn,oldname,dbname);
//					mapper.update(dbname, DynamicJsonProperty.jackson.writeValueAsBytes(t));
//    				break;
//    			}
//    			
//    		}
//    		logger.info("fix team data is done!");
//    		conn.close();
//    	}catch(Exception e){
//    		logger.error("修改数据报错！！e={}", e);
//			e.printStackTrace();
//    	}
    }
    
    public static void fixTeamData(int sid,TeamMapper mapper)
    {
    	try{
    		Connection conn = FixTeamDBData.getConnection(sid);
    		List<TeamEntity> list = FixTeamDBData.getTeams(sid, conn);
    		List<String> tempList = Lists.newArrayList();
    		for(TeamEntity ent : list){
    			tempList.add(ent.getName());
    		}
    		for (TeamEntity ent : list) {
    			Team t = DynamicJsonProperty.jackson.readValue(ent.getVal(), Team.class);
    			if(ent.getName().equals(t.getName())){
    				
    				String oldname = ent.getName();
    				String newname = ent.getName();
    				newname = newname.trim();
    				newname = newname.replaceAll("　", "");
    				newname = newname.replaceAll(" ", "");
    				newname = newname.replaceAll("'", "");
    				
    				oldname = oldname.replace("'", "\\'");
    				
    				if(newname.equals("")){
    					newname = "$$$";
    				}
    				
    				if(!newname.equals(oldname)){
    					while(tempList.contains(newname)){
        					newname = newname+"-";
        				}
    					tempList.add(newname);
    					logger.info("相同-dbname={},oldname={},newname={}",ent.getName(),oldname,newname);
    					ent.setName(newname);
    					t.setName(newname);
    					FixTeamDBData.updateTeamName(conn,oldname,newname);
    					mapper.update(newname, DynamicJsonProperty.jackson.writeValueAsBytes(t));
    				}
    				
    			}else{
    				
    				String oldName=ent.getName();
    				String newName=t.getName();
    				newName = newName.trim();
    				newName = newName.replaceAll("　", "");
    				newName = newName.replaceAll(" ", "");
    				newName = newName.replaceAll("'", "");
    				
    				oldName = oldName.replace("'", "\\'");
    				
    				if(newName.equals("")){
    					newName = "$$$";
    				}
    				
    				while(tempList.contains(newName)){
    					newName = newName+"-";
    				}
    				tempList.add(newName);
    				
    				logger.info("不同-dbname={},oldname={},newname={}",ent.getName(),oldName,newName);
    				t.setName(newName);
    				ent.setName(newName);
    				FixTeamDBData.updateTeamName(conn, oldName, newName);
    				mapper.update(newName, DynamicJsonProperty.jackson.writeValueAsBytes(t));
    			}
    		}
    		logger.info("fix team data is done!");
    		conn.close();
    	}catch(Exception e){
    		logger.error("修改数据报错！！e={}", e);
			e.printStackTrace();
    	}
    }
    
    public static void updatePlayerTeamName(String guid, Connection conn, String newName) {
    	try {
 			String sql = "update balli.role set balli.role.teamName='"+ newName 
 					+ "' where balli.role.guid=" + guid;
 			logger.info("修改role表里的军团名 " + sql);
 			System.out.println("修改role表里的军团名 " + sql);
 			PreparedStatement ps = conn.prepareStatement(sql);
 			ps.execute(sql);
 			if (ps != null)
 				ps.close();
 		} catch (Exception e) {
 			logger.error("修改{}--role.teamName出错 e={}", guid, e);
 			e.printStackTrace();
 		}
    }
    
    public static void updateTeamName(Connection conn,String oldname,String newname){
    	try{
    		String sql = "update balli.team set balli.team.name='"+newname
    				+ "' where balli.team.name='"+oldname+"'";
    		logger.info("修改team表里的军团名 " + sql);
    		PreparedStatement ps = conn.prepareStatement(sql);
    		ps.execute(sql);
 			if (ps != null)
 				ps.close();
    	}catch (Exception e) {
 			logger.error("修改{}--team.name出错 e={}", oldname, e);
 			e.printStackTrace();
 		}
    }
}
