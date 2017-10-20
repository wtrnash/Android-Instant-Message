package com.example.wtr.im.bean;

/**
 * Created by wtr on 2017/6/30.
 */

public class GroupItem {

    private String name = "";
    private String area = "";
    private String produce = "";
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


    public String getProduce(){
        return this.produce;
    }
    public void setProduce(String produce ){
        this.produce = produce;
    }

    public void setImage(String image){
        this.image = image;
    }
    public String getImage(){
        return this.image;
    }

}