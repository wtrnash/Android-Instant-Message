package com.example.wtr.makefriends.bean;

import java.io.Serializable;


public class PeopleItem implements Serializable{
    private static final long serialVersionUID = 1L;
    private String name = "";
    private String realName = "";
    private String occupation = "";
    private String nativePlace = "";
    private String email = "";
    private String contactWay = "";
    private String bloodyType = "";
    private String education = "";
    private String constellation = "";
    private String location = "";
    private String sex = "male";
    private int age = 0;
    private String image = "";
    private String introduce = "";

    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name = name;
    }

    public String getRealName(){return this.realName;}
    public void setRealName(String realName){this.realName = realName;}

    public String getOccupation(){return this.occupation;}
    public void setOccupation(String occupation){this.occupation = occupation;}

    public String getNativePlace(){return this.nativePlace;}
    public void setNativePlace(String nativePlace){this.nativePlace = nativePlace;}

    public String getEmail(){return this.email;}
    public void setEmail(String email){this.email = email;}

    public String getContactWay(){return this.contactWay;}
    public void setContactWay(String contactWay){this.contactWay = contactWay;}

    public String getBloodyType(){return this.bloodyType;}
    public void setBloodyType(String bloodyType){this.bloodyType = bloodyType;}

    public String getEducation(){return this.education;}
    public void setEducation(String education){this.education = education;}

    public String getConstellation(){return this.constellation;}
    public void setConstellation(String constellation){this.constellation = constellation;}

    public String getLocation(){
        return this.location;
    }
    public void setLocation(String location){
        this.location = location;
    }

    public String getSex(){
        return this.sex;
    }
    public void setSex(String sex){
        this.sex = sex;
    }

    public int getAge(){
        return this.age;
    }
    public void setAge(int age ){
        this.age = age;
    }

    public void setImage(String image){
        this.image = image;
    }
    public String getImage(){
        return this.image;
    }

    public String getIntroduce(){
        return this.introduce;
    }
    public void setIntroduce(String introduce){
        this.introduce = introduce;
    }

}
