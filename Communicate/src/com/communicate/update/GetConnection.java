package com.communicate.update;

import java.sql.Connection;
import java.sql.DriverManager;

public class GetConnection {
	
	private String JDriver="com.mysql.jdbc.Driver";
	private String url="jdbc:mysql://localhost:3306/make_friends?useUnicode=true&characterEncoding=utf-8";
	private String user="root";
	private String password="123456";
	
	public Connection getConn(){
		try{
			Class.forName(JDriver);// 动态导入数据库的驱动
			Connection conn=DriverManager.getConnection(url, user, password);// 获取数据库链接
			System.out.println("getconnection");
			return conn;
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
}
