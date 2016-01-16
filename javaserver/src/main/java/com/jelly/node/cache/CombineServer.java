package com.jelly.node.cache;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dol.cdf.common.DynamicJsonProperty;
import com.jelly.node.datastore.mapper.TeamEntity;
import com.jelly.node.datastore.mapper.TeamMapper;
import com.jelly.team.Team;
import com.jelly.team.TeamManager;
import com.mysql.jdbc.Connection;


public class CombineServer {
	private static final Logger logger = LoggerFactory.getLogger(CombineServer.class);
	
	public static final String DBUSER = "root";  

    public static final String DBPASS = "dre@mJ$11y";
    
    public static Connection getConnection(int sid) {
    	Connection conn = null;
    	try {
			String DBURL = "jdbc:mysql://localhost:3306/balli"; 
			if (sid != 1) {
				DBURL += sid;
			}
			System.out.println(sid + " : " + DBURL);
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
    	logger.error("----------------连接" + sid + "服失败！");
    	return conn;
    }
    
    static List<String> specialNames = new ArrayList<String>();
    static {
    	specialNames.add("NARUTO");
    	specialNames.add("lol");
    	specialNames.add("love^宏");
    	specialNames.add("UCIHA");
    	specialNames.add("Uchiha");
    	specialNames.add("king");
    	specialNames.add("Duang");
    	specialNames.add("Indo");
    }
    
    /**
     * 合并军团
     * 如果军团名字重复，加后缀：-服务器id
     * 同时修改role表里的teamName字段
     * @param maxSid
     * @param mapper
     */
    public static void combinTeam(int maxSid, TeamMapper mapper) {
    	Set<String> existedTeamName;
		int index;
		try {
			Connection conn = CombineServer.getConnection(1);
			existedTeamName = CombineServer.getServer1AllTeamName(conn);
			index = 0;
			for (int i=2; i<= maxSid; i++) {
				List<TeamEntity> list = CombineServer.getTeams(i, conn);
				logger.info("开始合并{}服", i);
				for (TeamEntity t : list) {
					String oldName = t.getName().trim();
					if (specialNames.contains(oldName)) {
						oldName += "-";
					}
					
					if (existedTeamName.contains(oldName)) {
						String newName = oldName + "-" + i;
						if (existedTeamName.contains(newName)) {
							newName = oldName + "--" + i;
						}
						if (existedTeamName.contains(newName)) {
							newName = oldName + "---" + i;
						}
						if (existedTeamName.contains(newName)) {
							newName = oldName + "----" + i;
						}
						Team team = DynamicJsonProperty.jackson.readValue(t.getVal(), Team.class);
						team.setName(newName);
						mapper.combin(newName, DynamicJsonProperty.jackson.writeValueAsBytes(team));
						existedTeamName.add(newName);
						logger.info("重复名字 sid={},{} --> {}", i, t.getName(), newName);
						CombineServer.updatePlayerTeamName(i, conn, newName, t.getName());
						
						index++;
					} else {
						existedTeamName.add(oldName);
						mapper.combin(oldName, t.getVal());
					}
					
				}
				logger.info("========= {}服军团合并完成 size={}, 重复={}", i, list.size(), index);
			}
			logger.info("========= 所有服军团合并完成 size={}, 重复={}", existedTeamName.size(), index);
			conn.close();
		} catch (Exception e) {
			logger.error("合并军团数据出错！！e={}", e);
			e.printStackTrace();
		}
    	
    }
    
    /**
     * 合服前统计所有表数据总数
     * @param conn
     * @return
     */
    public static void getAllTableCount(int maxSid)  {
    	Connection conn = CombineServer.getConnection(1);
    	int roleSize = CombineServer.getAllRoleCount(maxSid, conn);
    	int allkeysSize =  CombineServer.getAllKeysCount(maxSid, conn);
    	int paySize = CombineServer.getAllPayCount(maxSid, conn);
    	int teamSize = CombineServer.getAllTeamCount(maxSid, conn);
    	try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	logger.info("=========合服前所有服角色数量：{}", roleSize);
    	logger.info("=========合服前所有服allkeys数量：{}", allkeysSize);
    	logger.info("=========合服前所有服充值数量：{}", paySize);
    	logger.info("=========合服前所有服军团数量：{}", teamSize);
    }
    
    /**
     * 查询role表总数
     * @param conn
     * @return
     */
    public static int getAllRoleCount(int maxSid, Connection conn) {
		int amount = 0;
		for (int i=1; i<= maxSid; i++) {
    		int count = 0;
    		try {
    			String sql = "select count(*) amount from balli";
    			if (i != 1) {
    				sql += i;
    			}
    			sql += ".role";
    			PreparedStatement ps = conn.prepareStatement(sql);
    			ResultSet rs = ps.executeQuery();
    			while (rs.next()) {
    				count = rs.getInt("amount");
    				logger.info("{}服 roleSize={}", i, count);
    			}
    			if (rs != null)
    				rs.close();
    			if (ps != null)
    				ps.close();
    		} catch (Exception e) {
    			logger.error("查询{}服角色数量出错", i);
    			e.printStackTrace();
    		}
    		amount += count;
		}
		logger.info("合服前所有服角色数量：{}", amount);
		return amount;
	}
    
    /**
     * 查询allkeys表总数
     * @param conn
     * @return
     */
    public static int getAllKeysCount(int maxSid, Connection conn) {
		int amount = 0;
		for (int i=1; i<= maxSid; i++) {
    		int count = 0;
    		try {
    			String sql = "select count(*) amount from balli";
    			if (i != 1) {
    				sql += i;
    			}
    			sql += ".allkeys";
    			PreparedStatement ps = conn.prepareStatement(sql);
    			ResultSet rs = ps.executeQuery();
    			while (rs.next()) {
    				count = rs.getInt("amount");
    				logger.info("{}服 allkeysSize={}", i, count);
    			}
    			if (rs != null)
    				rs.close();
    			if (ps != null)
    				ps.close();
    		} catch (Exception e) {
    			logger.error("查询{}服allkeys数量出错", i);
    			e.printStackTrace();
    		}
    		amount += count;
		}
		logger.info("合服前所有服allkeys数量：{}", amount);
		return amount;
	}
    
    /**
     * 查询team表总数
     * @param conn
     * @return
     */
    public static int getAllTeamCount(int maxSid, Connection conn) {
		int amount = 0;
		for (int i=1; i<= maxSid; i++) {
    		int count = 0;
    		try {
    			String sql = "select count(*) amount from balli";
    			if (i != 1) {
    				sql += i;
    			}
    			sql += ".team";
    			PreparedStatement ps = conn.prepareStatement(sql);
    			ResultSet rs = ps.executeQuery();
    			while (rs.next()) {
    				count = rs.getInt("amount");
    				logger.info("{}服 teamSize={}", i, count);
    			}
    			if (rs != null)
    				rs.close();
    			if (ps != null)
    				ps.close();
    		} catch (Exception e) {
    			logger.error("查询{}服team数量出错", i);
    			e.printStackTrace();
    		}
    		amount += count;
		}
		logger.info("合服前所有服军团数量：{}", amount);
		return amount;
	}
    
    /**
     * 查询pay表总数
     * @param conn
     * @return
     */
    public static int getAllPayCount(int maxSid, Connection conn) {
		int amount = 0;
		for (int i=1; i<= maxSid; i++) {
    		int count = 0;
    		try {
    			String sql = "select count(*) amount from balli";
    			if (i != 1) {
    				sql += i;
    			}
    			sql += ".pay";
    			PreparedStatement ps = conn.prepareStatement(sql);
    			ResultSet rs = ps.executeQuery();
    			while (rs.next()) {
    				count = rs.getInt("amount");
    				logger.info("{}服 paySize={}", i, count);
    			}
    			if (rs != null)
    				rs.close();
    			if (ps != null)
    				ps.close();
    		} catch (Exception e) {
    			logger.error("查询{}服pay数量出错", i);
    			e.printStackTrace();
    		}
    		amount += count;
		}
		logger.info("合服前所有服充值数量：{}", amount);
		return amount;
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
			logger.error("查询{}服team出错", sid);
			e.printStackTrace();
		}
		return list;
	}
    
    public static List<TeamEntity> updatePlayerTeamName(int sid, Connection conn, String newName, String oldName) {
 		List<TeamEntity> list = new ArrayList<TeamEntity>();
 		try {
 			String sql = "update balli.role set balli.role.teamName='"+ newName 
 					+ "' where balli.role.net=" + sid + " and balli.role.teamName='" + oldName + "'";
 			logger.info("修改role表里的军团名 " + sql);
 			PreparedStatement ps = conn.prepareStatement(sql);
 			ps.execute(sql);
 			if (ps != null)
 				ps.close();
 		} catch (Exception e) {
 			logger.error("修改{}服role.teamName出错 e={}", sid, e);
 			e.printStackTrace();
 		}
 		return list;
 	}
    
    public static Set<String> getServer1AllTeamName(Connection conn) {
    	Set<String> names = new HashSet<String>();
    	try {
			String sql = "select name from balli.team";
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String name = rs.getString("name");
				names.add(name);
			}
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
		} catch (Exception e) {
			logger.error("查询{}服team name出错", 1);
			e.printStackTrace();
		}
    	
    	return names;
    }
 
    
    public static void main(String[] args) {
//    	CombineServer.getAllTableCount(172);
    	
    }

}
