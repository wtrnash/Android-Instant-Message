package com.communicate.update;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class UpdateUser {
	public void updateUser(String username, String sign, String name, String occupation, String sex, int age, String education, String constellation,
			String location, String nativePlace, String email, String contactWay, String bloodyType){
			
			if(sign == null)
				sign = "";
			try{
	            // 获取数据库链接*/
				GetConnection co = new GetConnection();
				Connection conn = co.getConn();
				String query=("update `us_er` set `signature`='" + sign + "',`name`='" + name +"',`sex`='" + sex + "',`age`='" + age + "', `bloody_type`='"+bloodyType+
						"', `occupation`='" + occupation + "',`education`='" +education+"',`constellation`='" + constellation + "', `native_place`='" +
						nativePlace+"', `email`='" + email+"', `contact_way`='"+ contactWay + "',`location`='" + location + "'where `username`=" + "'" + username +"'");
					
				PreparedStatement stmt=conn.prepareStatement(query);// 执行SQL语句
				stmt.executeUpdate();
				stmt.close();
				conn.close();
			}catch(Exception e){
				e.printStackTrace();
			}
			
		
		}
}
