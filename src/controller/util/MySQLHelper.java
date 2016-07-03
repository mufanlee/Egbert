package controller.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MySQLHelper {
	
	private static Logger log = LoggerFactory.getLogger(MySQLHelper.class);
	//获取数据库连接
	public static Connection getConnection()
	{
		Connection conn = null;  
        try
        {  
        	String dbDriver = "com.mysql.jdbc.Driver";// 数据库驱动
        	String dbUrl = "jdbc:mysql://192.168.83.128:3306/acl";// 数据库
        	String dbUser = "lipeng";// 用户名
        	String dbPasswd = "123";// 密码
            Class.forName(dbDriver);// 加载数据库驱动
            if (conn == null) {
            	conn = DriverManager.getConnection(dbUrl,dbUser,dbPasswd);
			}
        }  
        catch (ClassNotFoundException e)  
        {
        	log.error("Can't find the Driver: {}",e.getMessage());
        	return conn;
            //e.printStackTrace();  
        }  
        catch (SQLException e)  
        {  
        	log.error("Can't connect to DabaBase: {}",e.getMessage());
        	return conn;
            //e.printStackTrace();  
        }  
        return conn;
	}
	
	//增删改
	public static int executeNonQuery(String sql) 
	{
        int result = 0;
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.createStatement();
            result = stmt.executeUpdate(sql);
        } catch (SQLException err) {
            //err.printStackTrace();
        	log.error("ExecuteNoQuery error: {}", err.getMessage());
            free(null, stmt, conn);
            return -1;
        } finally {
            free(null, stmt, conn);
        }
        return result;
    }
	
	//增删改
	public static int executeNonQuery(String sql, Object... obj) 
	{
        int result = 0;
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            for (int i = 0; i < obj.length; i++) {
                pstmt.setObject(i + 1, obj[i]);
            }
            result = pstmt.executeUpdate();
        } catch (SQLException err) {
            //err.printStackTrace();
            log.error("ExecuteNoQuery error: {}", err.getMessage());
            free(null, pstmt, conn);
            return -1;
        } finally {
            free(null, pstmt, conn);
        }
        return result;
    }
	
	//查询
	public static ResultSet executeQuery(String sql) 
	{
	    Connection conn = null;
	    Statement stmt = null;
	    ResultSet rs = null;
	    try {
	         conn = getConnection();
	         stmt = conn.createStatement();
	         rs = stmt.executeQuery(sql);
	    } catch (SQLException err) {
	         //err.printStackTrace();
	    	log.error("ExecuteQuery error: {}", err.getMessage());
	        free(rs, stmt, conn);
	        return null;
	    }
	    return rs;
	 }
	 
	//查询
	public static ResultSet executeQuery(String sql, Object... obj) 
	{
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            for (int i = 0; i < obj.length; i++) {
                pstmt.setObject(i + 1, obj[i]);
            }           
            rs = pstmt.executeQuery();
        } catch (SQLException err) {
        	log.error("ExecuteQuery error: {}", err.getMessage());
            //err.printStackTrace();
            free(rs, pstmt, conn);
            return null;
        }
        return rs;
    }
	
	//判断记录是否存在
	public static Boolean isExist(String sql) 
	{
        ResultSet rs = null;
        try {
            rs = executeQuery(sql);
            rs.last();
            int count = rs.getRow();
            if (count > 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException err) {
        	log.error("ExecuteQuery error: {}", err.getMessage());
            //err.printStackTrace();
            free(rs);
            return false;
        } finally {
            free(rs);
        }
    }
	
	//判断记录是否存在
	public static Boolean isExist(String sql, Object... obj) 
	{
        ResultSet rs = null;
        try {
            rs = executeQuery(sql, obj);
            rs.last();
            int count = rs.getRow();
            if (count > 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException err) {
        	log.error("ExecuteQuery error: {}", err.getMessage());
            //err.printStackTrace();
            free(rs);
            return false;
        } finally {
            free(rs);
        }
    }
	
	//获取查询记录的总行数
	public static int getCount(String sql) {
        int result = 0;
        ResultSet rs = null;
        try {
            rs = executeQuery(sql);
            rs.last();
            result = rs.getRow();
        } catch (SQLException err) {
            free(rs);
            log.error("ExecuteQuery error: {}", err.getMessage());
            //err.printStackTrace();
            return 0;
        } finally {
            free(rs);
        }
        return result;
    }

	//获取查询记录的总行数
    public static int getCount(String sql, Object... obj) {
        int result = 0;
        ResultSet rs = null;
        try {
            rs = executeQuery(sql, obj);
            rs.last();
            result = rs.getRow();
        } catch (SQLException err) {
        	free(rs);
        	log.error("ExecuteQuery error: {}", err.getMessage());
            //err.printStackTrace();
            return 0;
        } finally {
            free(rs);
        }
        return result;
    }

    //释放ResultSet资源
    public static void free(ResultSet rs) {
        try {
            if (rs != null)
                rs.close();
        } catch (SQLException err) {
        	log.error("Free ResultSet error: {}", err.getMessage());
        	return;
            //err.printStackTrace();
        }
    }

    //释放Statement资源
    public static void free(Statement st) {
        try {
            if (st != null)
                st.close();
        } catch (SQLException err) {
        	log.error("Free Statement error: {}", err.getMessage());
        	return;
            //err.printStackTrace();
        }
    }

    //释放Connection资源
    public static void free(Connection conn) {
        try {
            if (conn != null)
                conn.close();
        } catch (SQLException err) {
        	log.error("Free Connection error: {}", err.getMessage());
        	return;
            //err.printStackTrace();
        }
    }
    
    //释放所有数据资源
    public static void free(ResultSet rs, Statement st, Connection conn) {
        free(rs);
        free(st);
        free(conn);
    }
}
