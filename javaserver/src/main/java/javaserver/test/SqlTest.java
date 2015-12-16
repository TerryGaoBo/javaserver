package javaserver.test;

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

import com.mysql.jdbc.Connection;

public class SqlTest {

	private static final Logger logger = LoggerFactory.getLogger(SqlTest.class);
	
	public static void main(String[] args)
	{
	}
	
	public static final String DBUSER = "admin";
	public static final String DBPASS = "admin";
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
}
