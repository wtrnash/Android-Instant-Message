package com.example.wtr.im.bean;

import android.graphics.Bitmap;

/**
 * Created by wtr on 2017/7/2.
 */

public class MessageItem {
    private String username = "空";
    private String text;
    private String date;
    private String image;
    private Bitmap bitmap = null;
    private String soundPath = null;
    private Boolean isRead = false;//是否已读
    private  int soundTime = 0;

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

    public Bitmap getBitmap(){return this.bitmap;}
    public void setBitmap(Bitmap bitmap){this.bitmap = bitmap;}

    public String getSoundPath(){return this.soundPath;}
    public void setSoundPath(String soundPath){this.soundPath = soundPath;}

    public int getSoundTime(){return this.soundTime;}
    public void setSoundTime(int soundTime){this.soundTime = soundTime;}
}
