package com.example.wtr.im.bean;

/**
 * Created by wtr on 2017/7/2.
 */

public class MessageItem {
    private String username = "空";
    private String text;
    private String date;
    private String image;
    private Boolean isRead = false;//是否已读

    public String getUsername(){
        return this.username;
    }
    public void setUsername(String username){
        this.username = username;
    }

    public String getText(){
        return this.text;
    }
    public void setText(String text){
        this.text = text;
    }

    public String getDate(){
        return this.date;
    }
    public void setDate(String date){
        this.date = date;
    }

    public String getImage(){
        return this.image;
    }
    public void setImage(String image){
        this.image = image;
    }

    public Boolean getIsRead() {
        return this.isRead;
    }
    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }
}
