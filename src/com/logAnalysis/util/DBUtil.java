package com.logAnalysis.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSourceFactory;

public class DBUtil {
	//创建连接池
    private static DataSource dataSource;
    //加载配置文件,创建连接池
    static{
        try {
            InputStream is = DBUtil.class.getClassLoader().getResourceAsStream("dbcp.properties");
            Properties pro = new Properties();
            pro.load(is);
            dataSource = BasicDataSourceFactory.createDataSource(pro);
        }  catch (Exception e) {
            e.printStackTrace();
        }
    }
    //获得连接池
    public static DataSource getDataSource() {
        return dataSource;
    }
    
    //获得数据库
    
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
    
    public static boolean InsertInto(String sql) {
    	Connection conn = null;
    	Statement stat = null;
    	try {
			conn = DBUtil.getConnection();
			stat = conn.createStatement();
			stat.execute(sql);
		} catch (SQLException e) {
		} finally {
			try {
				if(stat != null) {
					stat.close();
				}
				if(conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
			}
		}
    	return false;
    }
}
