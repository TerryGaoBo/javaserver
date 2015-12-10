package game.manager;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;
	
/**
 *  增加，删除，修改，查找
 *  
 */
public class DBManager {

	public DBManager() {
	}
	
	public void testDb()
	{
		Connection conn = null;
		String sql;
		// MySQL的JDBC URL编写方式：jdbc:mysql://主机名称：连接端口/数据库的名称?参数=值
        // 避免中文乱码要指定useUnicode和characterEncoding
        // 执行数据库操作之前要在数据库管理系统上创建一个数据库，名字自己定，
        // 下面语句之前就要先创建javademo数据库
		
		String url = "jdbc:mysql://localhost:3306/gamedb?"
				+ "user=root&useUnicode=true&characterEncoding=UTF8";
		
//		  String url = "jdbc:mysql://localhost:3306/javademo?"
//	                + "user=root&password=root&useUnicode=true&characterEncoding=UTF8";
		try{
			// 之所以要使用下面这条语句，是因为要使用MySQL的驱动，所以我们要把它驱动起来，
            // 可以通过Class.forName把它加载进去，也可以通过初始化来驱动起来，下面三种形式都可以
            Class.forName("com.mysql.jdbc.Driver");// 动态加载mysql驱动
            // or:
            // com.mysql.jdbc.Driver driver = new com.mysql.jdbc.Driver();
            // or：
            // new com.mysql.jdbc.Driver();
 
            System.out.println("成功加载MySQL驱动程序");
			// 一个Connection代表一个数据库连接
			conn = DriverManager.getConnection(url);
//			DriverManager.getConnection(url, user, password)
			// Statement里面带有很多方法，比如executeUpdate可以实现插入，更新和删除等
			Statement stmt = conn.createStatement();

			sql = "create table student(NO char(20),name varchar(20),primary key(NO))";
			int result = stmt.executeUpdate(sql);// executeUpdate语句会返回一个受影响的行数，如果返回-1就没有成功
			if (result != -1) {
				System.out.println("创建数据表成功");
				sql = "insert into student(NO,name) values('2012001','陶伟基')";
				result = stmt.executeUpdate(sql);
				sql = "insert into student(NO,name) values('2012002','周小俊')";
				result = stmt.executeUpdate(sql);
				sql = "select * from student";
				ResultSet rs = stmt.executeQuery(sql);// executeQuery会返回结果的集合，否则返回空值
				System.out.println("学号\t姓名");
				while (rs.next()) {
					System.out.println(rs.getString(1) + "\t" + rs.getString(2));// 入如果返回的是int类型可以用getInt()
				}
			}
			conn.close();
		} catch (SQLException e) {
			System.out.println("MySQL操作错误");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testDB2()
	{
//		// JDBC 驱动器名称和数据库的 URL
//	      static final String JDBC_DRIVER="com.mysql.jdbc.Driver";  
//	      static final String DB_URL="jdbc:mysql://localhost/TEST";
//
//	      //  数据库的凭据
//	      static final String USER = "root";
//	      static final String PASS = "password";
//
//	      // 设置响应内容类型
//	      response.setContentType("text/html");
//	      PrintWriter out = response.getWriter();
//	      String title = "数据库结果";
//	      String docType =
//	        "<!doctype html public \"-//w3c//dtd html 4.0 " +
//	         "transitional//en\">\n";
//	         out.println(docType +
//	         "<html>\n" +
//	         "<head><title>" + title + "</title></head>\n" +
//	         "<body bgcolor=\"#f0f0f0\">\n" +
//	         "<h1 align=\"center\">" + title + "</h1>\n");
//	      try{
//	         // 注册 JDBC 驱动器
//	         Class.forName("com.mysql.jdbc.Driver");
//
//	         // 打开一个连接
//	         conn = DriverManager.getConnection(DB_URL,USER,PASS);
//
//	         // 执行 SQL 查询
//	         stmt = conn.createStatement();
//	         String sql;
//	         sql = "SELECT id, first, last, age FROM Employees";
//	         ResultSet rs = stmt.executeQuery(sql);
//
//	         // 从结果集中提取数据
//	         while(rs.next()){
//	            // 根据列名称检索
//	            int id  = rs.getInt("id");
//	            int age = rs.getInt("age");
//	            String first = rs.getString("first");
//	            String last = rs.getString("last");
//
//	            // 显示值
//	            out.println("ID: " + id + "<br>");
//	            out.println(", Age: " + age + "<br>");
//	            out.println(", First: " + first + "<br>");
//	            out.println(", Last: " + last + "<br>");
//	         }
//	         out.println("</body></html>");
//
//	         // 清理环境
//	         rs.close();
//	         stmt.close();
//	         conn.close();
//	      }catch(SQLException se){
//	         // 处理 JDBC 错误
//	         se.printStackTrace();
//	      }catch(Exception e){
//	         // 处理 Class.forName 错误
//	         e.printStackTrace();
//	      }finally{
//	         // 最后是用于关闭资源的块
//	         try{
//	            if(stmt!=null)
//	               stmt.close();
//	         }catch(SQLException se2){
//	         }// 我们不能做什么
//	         try{
//	            if(conn!=null)
//	            conn.close();
//	         }catch(SQLException se){
//	            se.printStackTrace();
//	         }//end finally try
//	      } //end try
//	   }
	}
	
	// 根据键值得到这个字段的数据
	public Object getDataByTable(String t,String key)
	{
		return null;
	}
}
