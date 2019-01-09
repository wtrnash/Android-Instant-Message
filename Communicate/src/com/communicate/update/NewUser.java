package com.communicate.update;

import java.sql.Connection;
import java.sql.PreparedStatement;


public class NewUser {
public void createUser(String username, String password, String name, String occupation, String sex, int age, String education, String constellation,
		String location, String nativePlace, String email, String contactWay, String bloodyType){
		
		try{
            // 获取数据库链接*/
			GetConnection co = new GetConnection();
			Connection conn = co.getConn();
			String query=("insert into `us_er`(`username`,`password`,`name`,`sex`,`age`,`bloody_type`,`occupation`,`education`," +
					"`constellation`,`native_place`,`email`, `contact_way`,`location`) values('"
			+username+"','"+password+"','"+name+"','"+ sex + "','"+ age + "','" + bloodyType + "','"+ occupation+ "','"+ education + "','"+ constellation +
			"','" + nativePlace + "','" + email + "','" + contactWay + "','" + location + "')");// 创造SQL语句 
			PreparedStatement stmt=conn.prepareStatement(query);// 执行SQL语句
			stmt.executeUpdate();
			stmt.close();
			conn.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
