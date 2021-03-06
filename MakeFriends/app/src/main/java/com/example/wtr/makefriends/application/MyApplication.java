package com.example.wtr.makefriends.application;

import android.app.Application;
import android.support.multidex.MultiDexApplication;

import com.example.wtr.makefriends.bean.Conversation;
import com.example.wtr.makefriends.bean.GroupItem;
import com.example.wtr.makefriends.bean.PeopleItem;
import com.example.wtr.makefriends.service.XMPPService;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.util.ArrayList;
import java.util.List;


public class MyApplication extends MultiDexApplication {
    public static XMPPConnection xmppConnection;
    private List<Conversation> ConversationList = new ArrayList<Conversation>();
    private static MyApplication myApplicationContext;
    private List<PeopleItem> PeopleItemList = new ArrayList<PeopleItem>();
    private List<GroupItem> GroupItemList = new ArrayList<GroupItem>();
    private static PeopleItem theUser = new PeopleItem();
    private static XMPPService xmppService;
    private static List<MultiUserChat> multiUserChatList = new ArrayList<>();

    public static MyApplication getMyApplication(){
        return myApplicationContext;
    }
    public  List<Conversation> getConversationList(){
        return ConversationList;
    }
    public List<PeopleItem> getPeopleItemList(){
        return PeopleItemList;
    }
    public List<GroupItem> getGroupItemList(){
        return GroupItemList;
    }
    public static PeopleItem getUser(){
        return theUser;
    }
    public  static void setUser(PeopleItem people){
        theUser = people;
    }
    public static void setXmppService(XMPPService service){xmppService = service;}
    public static XMPPService getXmppService() { return xmppService;}
    public static List<MultiUserChat> getMultiUserChatList(){return multiUserChatList;}

    @Override
    public void onCreate() {
        super.onCreate();
        myApplicationContext = this;
        //创建默认的ImageLoader配置参数
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration
                .createDefault(this);

        //Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(configuration);
    }
}
