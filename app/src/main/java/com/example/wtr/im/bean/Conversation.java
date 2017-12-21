package com.example.wtr.im.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wtr on 2017/6/30.
 */

public class Conversation {
    private String name = "";
    private String id;
    private String image;
    private String lastTime = "";
    private String lastMessage = "";
    private List<MessageItem> wordList = new ArrayList<MessageItem>();
    private int newMessageCount = 0;
    private boolean isGroupConversation = false;
    private String groupName = "";

    public Conversation(){}
    public Conversation(String name,String id,String image,
                        String lastTime,String lastMessage,int newMessageCount, boolean isGroupConversation, String groupName){
        this.name = name;
        this.id = id;
        this.image = image;
        this.lastTime = lastTime;
        this.lastMessage = lastMessage;
        this.newMessageCount = newMessageCount;
        this.isGroupConversation = isGroupConversation;
        this.groupName = groupName;
    }

    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }

    public void setId(String id){
        this.id = id;
    }
    public String getId(){
        return this.id;
    }

    public void setImage(String image){
        this.image = image;
    }
    public String getImage(){
        return this.image;
    }

    public void setLastTime(String lastTime){
        this.lastTime = lastTime;
    }
    public String getLastTime(){
        return this.lastTime;
    }

    public void setLastMessage(String lastMessage){
        this.lastMessage = lastMessage;
    }
    public String getLastMessage(){
        return this.lastMessage;
    }

    public void setNewMessageCount(int newMessageCount){
        this.newMessageCount = newMessageCount;
    }
    public int getNewMessageCount(){
        return this.newMessageCount;
    }

    public void setWordList(List<MessageItem> wordList){this.wordList = wordList;}
    public List<MessageItem> getWordList(){return this.wordList;}

    public void setGroupConversation(boolean isGroupConversation){this.isGroupConversation = isGroupConversation;}
    public boolean getGroupConversation(){return this.isGroupConversation;}

    public void setGroupName(String groupName){this.groupName = groupName;}
    public String getGroupName(){return this.groupName;}
}
