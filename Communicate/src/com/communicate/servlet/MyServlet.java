package com.communicate.servlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import com.communicate.entity.PeopleItem;
import com.communicate.update.GetUser;
import com.communicate.update.GetUserByAge;
import com.communicate.update.GetUserByBloodyType;
import com.communicate.update.GetUserByConstellation;
import com.communicate.update.GetUserByEducation;
import com.communicate.update.GetUserByLocation;
import com.communicate.update.GetUserByNativePlace;
import com.communicate.update.GetUserByOccupation;
import com.communicate.update.GetUserBySex;
import com.communicate.update.NewUser;
import com.communicate.update.UpdateUser;

import sun.misc.BASE64Decoder;



/**
 * Servlet implementation class MyServlet
 */
//@WebServlet("/MyServlet")
@WebServlet("/MyServlet")
public class MyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MyServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/json;charset=utf-8"); 
		response.setCharacterEncoding("utf-8");
        String order=null;
        order=request.getParameter("order");
       
        if(("getUser").equals(order)){
        	List<PeopleItem> list = new ArrayList<PeopleItem>();
        	String username = request.getParameter("username");
        	GetUser getUserInfo = new GetUser();
        	list = getUserInfo.getUser(username);
        	JSONStringer stringer = new JSONStringer();	
    		try{
    			stringer.array();
    			
    			for(PeopleItem peopleItem:list){
    				stringer.object()
    				.key("name").value(peopleItem.getName())
    				.key("realName").value(peopleItem.getRealName())
    				.key("occupation").value(peopleItem.getOccupation())
    				.key("nativePlace").value(peopleItem.getNativePlace())
    				.key("email").value(peopleItem.getEmail())
    				.key("contactWay").value(peopleItem.getContactWay())
    				.key("bloodyType").value(peopleItem.getBloodyType())
    				.key("education").value(peopleItem.getEducation())
    				.key("constellation").value(peopleItem.getConstellation())
    				.key("location").value(peopleItem.getLocation())
    				.key("sex").value(peopleItem.getSex())
    				.key("age").value(peopleItem.getAge())
    				.key("image").value(peopleItem.getImage())
    				.key("introduce").value(peopleItem.getIntroduce())
    				.endObject();
    			}
    			stringer.endArray();
    		}
    		catch(Exception e){}
    		PrintWriter out = response.getWriter();  
            out.print(stringer.toString());  
            out.flush();  
            out.close();
        }

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/json;charset=utf-8"); 
		response.setCharacterEncoding("utf-8");
		
		String order = request.getParameter("order");
		if(order.equals("image")){
			String image = request.getParameter("image");
			String username = request.getParameter("username");
			username = URLDecoder.decode(username,"UTF-8");
			JSONObject result = new JSONObject();
			if(image == null){
				try {
					result.put("result", false);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				try {
					result.put("result", true);
					
					//我要获取当前的日期
			        Date date = new Date();
			        //设置要获取到什么样的时间
			        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			        //获取String类型的时间
			        String createdate = sdf.format(date);

					String fileName = username + createdate + ".jpg";

					String path = "C:/tomcat/apache-tomcat-7.0.91-windows-x64/apache-tomcat-7.0.91/webapps/images";
					
					File file = new File(path, fileName);

					FileOutputStream fos = new FileOutputStream(file);

					BASE64Decoder decoder = new BASE64Decoder();

					try{

					//Base64解码

					byte[] base = decoder.decodeBuffer(image);

					fos.write(base);

					fos.flush();

					fos.close();

					}catch(Exception e)  {

					System.out.print(e);

					}

				
					try {
						result.put("imageUrl", "http://47.101.143.29/images/" + fileName);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				
				
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				PrintWriter out = response.getWriter();  
		        out.print(result.toString());  
		        out.flush();  
		        out.close(); 
			}
			
		}
		else if(order.equals("audio")){
			String audio = request.getParameter("audio");
			String username = request.getParameter("username");
			username = URLDecoder.decode(username,"UTF-8");
			JSONObject result = new JSONObject();
			if(audio == null){
				try {
					result.put("result", false);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				try {
					result.put("result", true);
					
					//我要获取当前的日期
			        Date date = new Date();
			        //设置要获取到什么样的时间
			        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			        //获取String类型的时间
			        String createdate = sdf.format(date);

					String fileName = username + createdate + ".3gp";

					String path = "C:/tomcat/apache-tomcat-7.0.91-windows-x64/apache-tomcat-7.0.91/webapps/audios";
					
					File file = new File(path, fileName);

					FileOutputStream fos = new FileOutputStream(file);

					BASE64Decoder decoder = new BASE64Decoder();

					try{

					//Base64解码

					byte[] base = decoder.decodeBuffer(audio);

					fos.write(base);

					fos.flush();

					fos.close();

					}catch(Exception e)  {

					System.out.print(e);

					}

				
					try {
						result.put("audioUrl", "http://47.101.143.29/audios/" + fileName);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				
				
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				PrintWriter out = response.getWriter();  
		        out.print(result.toString());  
		        out.flush();  
		        out.close(); 
		}
		
	}else if(order.equals("newUser")){
		String username = request.getParameter("username");
		username = URLDecoder.decode(username,"UTF-8");
		String password = request.getParameter("password");
		password = URLDecoder.decode(password,"UTF-8");
		String realName = request.getParameter("realName");
		realName = URLDecoder.decode(realName,"UTF-8");
		String occupation = request.getParameter("occupation");
		occupation = URLDecoder.decode(occupation,"UTF-8");
		String age = request.getParameter("age");
		String nativePlace = request.getParameter("nativePlace");
		nativePlace = URLDecoder.decode(nativePlace,"UTF-8");
		String email = request.getParameter("email");
		email =  URLDecoder.decode(email,"UTF-8");
		String contactWay = request.getParameter("contactWay");
		contactWay = URLDecoder.decode(contactWay,"UTF-8");
        String location = request.getParameter("location");
        location = URLDecoder.decode(location,"UTF-8");
        String education = request.getParameter("education");
        education = URLDecoder.decode(education,"UTF-8");
        String sex = request.getParameter("sex");
        sex = URLDecoder.decode(sex,"UTF-8");
        String bloodyType = request.getParameter("bloodyType");
        bloodyType = URLDecoder.decode(bloodyType,"UTF-8");
        String constellation = request.getParameter("constellation");
        constellation = URLDecoder.decode(constellation,"UTF-8");
        JSONObject result = new JSONObject();
        if(username == null || password == null || realName == null ||occupation == null ||age == null || nativePlace == null || email == null || contactWay == null
        	|| location == null || education == null || sex == null || bloodyType == null || constellation == null){
        	try {
				result.put("result", false);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }else{
        	try {
				result.put("result", true);
				NewUser newUser = new NewUser();
				newUser.createUser(username, password, realName, occupation, sex, Integer.parseInt(age), education, constellation, location, nativePlace, email, contactWay, bloodyType);
			
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			PrintWriter out = response.getWriter();  
	        out.print(result.toString());  
	        out.flush();  
	        out.close(); 
        }
	}
	else if(order.equals("userSetting")){
		String username = request.getParameter("username");
		username = URLDecoder.decode(username,"UTF-8");
		String sign = request.getParameter("sign");
		sign = URLDecoder.decode(sign,"UTF-8");
		String realName = request.getParameter("realName");
		realName = URLDecoder.decode(realName,"UTF-8");
		String occupation = request.getParameter("occupation");
		occupation = URLDecoder.decode(occupation,"UTF-8");
		String age = request.getParameter("age");
		String nativePlace = request.getParameter("nativePlace");
		nativePlace = URLDecoder.decode(nativePlace,"UTF-8");
		String email = request.getParameter("email");
		email =  URLDecoder.decode(email,"UTF-8");
		String contactWay = request.getParameter("contactWay");
		contactWay = URLDecoder.decode(contactWay,"UTF-8");
        String location = request.getParameter("location");
        location = URLDecoder.decode(location,"UTF-8");
        String education = request.getParameter("education");
        education = URLDecoder.decode(education,"UTF-8");
        String sex = request.getParameter("sex");
        sex = URLDecoder.decode(sex,"UTF-8");
        String bloodyType = request.getParameter("bloodyType");
        bloodyType = URLDecoder.decode(bloodyType,"UTF-8");
        String constellation = request.getParameter("constellation");
        constellation = URLDecoder.decode(constellation,"UTF-8");
        JSONObject result = new JSONObject();
        if(username == null  || realName == null ||occupation == null ||age == null || nativePlace == null || email == null || contactWay == null
        	|| location == null || education == null || sex == null || bloodyType == null || constellation == null){
        	try {
				result.put("result", false);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }else{
        	try {
				result.put("result", true);		
				UpdateUser update = new UpdateUser();
				update.updateUser(username, sign, realName, occupation, sex, Integer.parseInt(age), education, constellation, location, nativePlace, email, contactWay, bloodyType);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
        }
    	
		PrintWriter out = response.getWriter();  
        out.print(result.toString());  
        out.flush();  
        out.close(); 
		
	}
	else if(order.equals("setFriends")){
		String occupation = request.getParameter("occupation");
		occupation = URLDecoder.decode(occupation,"UTF-8");
		String minAge = request.getParameter("minAge");
		String maxAge = request.getParameter("maxAge");
		String nativePlace = request.getParameter("nativePlace");
		nativePlace = URLDecoder.decode(nativePlace,"UTF-8");
        String location = request.getParameter("location");
        location = URLDecoder.decode(location,"UTF-8");
        String education = request.getParameter("education");
        education = URLDecoder.decode(education,"UTF-8");
        String sex = request.getParameter("sex");
        sex = URLDecoder.decode(sex,"UTF-8");
        String bloodyType = request.getParameter("bloodyType");
        bloodyType = URLDecoder.decode(bloodyType,"UTF-8");
        String constellation = request.getParameter("constellation");
        constellation = URLDecoder.decode(constellation,"UTF-8");
        
        String isChooseSex = request.getParameter("isChooseSex");
        String isChooseOccupation = request.getParameter("isChooseOccupation");
        String isChooseAge = request.getParameter("isChooseAge");
        String isChooseBloodyType = request.getParameter("isChooseBloodyType");
        String isChooseEducation = request.getParameter("isChooseEducation");
        String isChooseConstellation = request.getParameter("isChooseConstellation");
        String isChooseNativePlace = request.getParameter("isChooseNativePlace");
        String isChooseLocation = request.getParameter("isChooseLocation");
        
        List<PeopleItem> list = new ArrayList<PeopleItem>();
        List<PeopleItem> tempList = new ArrayList<PeopleItem>();
        boolean isChoose = false;
        //年龄、性别为硬性条件，不满足硬性条件的不会给出，其余为潜在条件，其余越高排序越前
        if(isChooseSex.equals("true")){
        	GetUserBySex bySex = new GetUserBySex();
        	list = bySex.getUserBySex(sex);
        	isChoose = true;
        }
        
        //不符合年龄范围内的则删去
        if(isChooseAge.equals("true")){
        	GetUserByAge byAge = new GetUserByAge();
        	tempList = byAge.getUserByAge(Integer.parseInt(minAge), Integer.parseInt(maxAge));
        	if(isChoose)
        		list.retainAll(tempList);	//取交集
        	else
        		list = tempList;
        	
        	isChoose = true;
        }
        
        //符合一个条件则权值加1
        if(isChooseOccupation.equals("true")){
        	GetUserByOccupation byOccupation = new GetUserByOccupation();
        	tempList = byOccupation.getUserByOccupation(occupation);
        	if(isChoose){
        		for(int i = 0; i < tempList.size(); i++){
            		for(int j = 0; j < list.size(); j++){
            			if(tempList.get(i).getName().equals(list.get(j).getName())){
            				PeopleItem temp = list.get(j);
            				temp.setWeight(temp.getWeight() + 1);
            				list.set(j, temp);
            				break;
            			}
            		}	
            	}
        	}else{
        		list = tempList;
        	}
        	
        }
        	
        if(isChooseBloodyType.equals("true")){
        	GetUserByBloodyType byBloodyType = new GetUserByBloodyType();
        	tempList = byBloodyType.getUserByBloodyType(bloodyType);
        	if(isChoose){
        		for(int i = 0; i < tempList.size(); i++){
            		for(int j = 0; j < list.size(); j++){
            			if(tempList.get(i).getName().equals(list.get(j).getName())){
            				PeopleItem temp = list.get(j);
            				temp.setWeight(temp.getWeight() + 1);
            				list.set(j, temp);
            				break;
            			}
            		}	
            	}
        	}else{
        		for(int i = 0; i < tempList.size(); i++){
        			int j;
        			for(j = 0; j < list.size(); j++){
        				if(tempList.get(i).getName().equals(list.get(j).getName())){
            				PeopleItem temp = list.get(j);
            				temp.setWeight(temp.getWeight() + 1);
            				list.set(j, temp);
            				break;
            			}
        			}
        			//没有在list中则加入
        			if(j == list.size()){
        				list.add(tempList.get(i));
        			}
        			
        		}
        	}
        		
        }
        	
        if(isChooseEducation.equals("true")){
        	GetUserByEducation byEducation = new GetUserByEducation();
        	tempList = byEducation.getUserByEducation(education);
        	if(isChoose){
        		for(int i = 0; i < tempList.size(); i++){
            		for(int j = 0; j < list.size(); j++){
            			if(tempList.get(i).getName().equals(list.get(j).getName())){
            				PeopleItem temp = list.get(j);
            				temp.setWeight(temp.getWeight() + 1);
            				list.set(j, temp);
            				break;
            			}
            		}	
            	}
        	}else{
        		for(int i = 0; i < tempList.size(); i++){
        			int j;
        			for(j = 0; j < list.size(); j++){
        				if(tempList.get(i).getName().equals(list.get(j).getName())){
            				PeopleItem temp = list.get(j);
            				temp.setWeight(temp.getWeight() + 1);
            				list.set(j, temp);
            				break;
            			}
        			}
        			//没有在list中则加入
        			if(j == list.size()){
        				list.add(tempList.get(i));
        			}
        			
        		}
        	}
        }
        	
        if(isChooseConstellation.equals("true")){
        	GetUserByConstellation byConstellation = new GetUserByConstellation();
        	tempList = byConstellation.getUserByConstellation(constellation);
        	if(isChoose){
        		for(int i = 0; i < tempList.size(); i++){
            		for(int j = 0; j < list.size(); j++){
            			if(tempList.get(i).getName().equals(list.get(j).getName())){
            				PeopleItem temp = list.get(j);
            				temp.setWeight(temp.getWeight() + 1);
            				list.set(j, temp);
            				break;
            			}
            		}	
            	}
        	}else{
        		for(int i = 0; i < tempList.size(); i++){
        			int j;
        			for(j = 0; j < list.size(); j++){
        				if(tempList.get(i).getName().equals(list.get(j).getName())){
            				PeopleItem temp = list.get(j);
            				temp.setWeight(temp.getWeight() + 1);
            				list.set(j, temp);
            				break;
            			}
        			}
        			//没有在list中则加入
        			if(j == list.size()){
        				list.add(tempList.get(i));
        			}
        			
        		}
        	}
        }
        	
        if(isChooseNativePlace.equals("true")){
        	GetUserByNativePlace byNativePlace = new GetUserByNativePlace();
        	tempList = byNativePlace.getUserByNativePlace(nativePlace);
        	if(isChoose){
        		for(int i = 0; i < tempList.size(); i++){
            		for(int j = 0; j < list.size(); j++){
            			if(tempList.get(i).getName().equals(list.get(j).getName())){
            				PeopleItem temp = list.get(j);
            				temp.setWeight(temp.getWeight() + 1);
            				list.set(j, temp);
            				break;
            			}
            		}	
            	}
        	}else{
        		for(int i = 0; i < tempList.size(); i++){
        			int j;
        			for(j = 0; j < list.size(); j++){
        				if(tempList.get(i).getName().equals(list.get(j).getName())){
            				PeopleItem temp = list.get(j);
            				temp.setWeight(temp.getWeight() + 1);
            				list.set(j, temp);
            				break;
            			}
        			}
        			//没有在list中则加入
        			if(j == list.size()){
        				list.add(tempList.get(i));
        			}
        			
        		}
        	}
        }
        	
        if(isChooseLocation.equals("true")){
        	GetUserByLocation byLocation = new GetUserByLocation();
        	tempList = byLocation.getUserByLocation(location);
        	if(isChoose){
        		for(int i = 0; i < tempList.size(); i++){
            		for(int j = 0; j < list.size(); j++){
            			if(tempList.get(i).getName().equals(list.get(j).getName())){
            				PeopleItem temp = list.get(j);
            				temp.setWeight(temp.getWeight() + 1);
            				list.set(j, temp);
            				break;
            			}
            		}	
            	}
        	}else{
        		for(int i = 0; i < tempList.size(); i++){
        			int j;
        			for(j = 0; j < list.size(); j++){
        				if(tempList.get(i).getName().equals(list.get(j).getName())){
            				PeopleItem temp = list.get(j);
            				temp.setWeight(temp.getWeight() + 1);
            				list.set(j, temp);
            				break;
            			}
        			}
        			//没有在list中则加入
        			if(j == list.size()){
        				list.add(tempList.get(i));
        			}
        			
        		}
        	}
        }
        		
        
        Collections.sort(list);	//调整顺序
        if(list.size() >= 20)
        	list = list.subList(0, 20);						//取前20条最显示
        JSONStringer stringer = new JSONStringer();	
		try{
			stringer.array();
			
			for(PeopleItem peopleItem:list){
				stringer.object()
				.key("name").value(peopleItem.getName())
				.key("realName").value(peopleItem.getRealName())
				.key("occupation").value(peopleItem.getOccupation())
				.key("nativePlace").value(peopleItem.getNativePlace())
				.key("email").value(peopleItem.getEmail())
				.key("contactWay").value(peopleItem.getContactWay())
				.key("bloodyType").value(peopleItem.getBloodyType())
				.key("education").value(peopleItem.getEducation())
				.key("constellation").value(peopleItem.getConstellation())
				.key("location").value(peopleItem.getLocation())
				.key("sex").value(peopleItem.getSex())
				.key("age").value(peopleItem.getAge())
				.key("image").value(peopleItem.getImage())
				.key("introduce").value(peopleItem.getIntroduce())
				.endObject();
			}
			stringer.endArray();
		}
		catch(Exception e){}
		PrintWriter out = response.getWriter();  
        out.print(stringer.toString());  
        out.flush();  
        out.close();
	}
	}

}
