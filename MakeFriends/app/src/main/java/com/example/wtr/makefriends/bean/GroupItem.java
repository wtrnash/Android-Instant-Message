package com.example.wtr.makefriends.bean;

public class GroupItem {

    private String name = "";
    private String area = "";
    private String introduce = "";
    private String image;

    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name = name;
    }

    public String getArea(){
        return this.area;
    }
    public void setArea(String area){
        this.area = area;
    }


    public String getIntroduce(){
        return this.introduce;
    }
    public void setIntroduce(String introduce){
        this.introduce = introduce;
    }

    public void setImage(String image){
        this.image = image;
    }
    public String getImage(){
        return this.image;
    }

}