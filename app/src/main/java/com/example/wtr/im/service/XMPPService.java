package com.example.wtr.im.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.example.wtr.im.application.MyApplication;
import com.example.wtr.im.bean.PeopleItem;
import com.example.wtr.im.listener.CheckConnectionListener;
import com.example.wtr.im.listener.FriendsPacketListener;
import com.example.wtr.im.listener.MyChatManagerListener;
import com.example.wtr.im.util.Const;
import com.example.wtr.im.util.PreferencesUtil;
import com.example.wtr.im.util.ReFreshDataUtil;
import com.example.wtr.im.util.XMPPConnectionManager;

import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Packet;

/**
 * Created by wtr on 2017/6/29.
 */

public class XMPPService extends Service{
    private XMPPConnectionManager xmppConnectionManager;
    private XMPPConnection xmppConnection;
    private MyChatManagerListener myChatManagerListener;
    private CheckConnectionListener checkConnectionListener;
    private FriendsPacketListener friendsPacketListener;
    private String username;
    private String password;
    private Context myContext;

    private final IBinder binder = new MyBinder();

    public class MyBinder extends Binder {
        public XMPPService getService() {
            return XMPPService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myContext = this;
        username = PreferencesUtil.getSharedPreStr(myContext,"username");
        password  = PreferencesUtil.getSharedPreStr(myContext,"password");
        xmppConnectionManager = XMPPConnectionManager.getInstance();
        initXMPPTask();
    }

    //开线程初始化XMPP和完成后台登录
    private void initXMPPTask() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    initXMPP();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //初始化XMPP
    private void  initXMPP() throws XMPPException {
        xmppConnection = xmppConnectionManager.init();
        doLogin();
        //添加信息监听
        ChatManager chatmanager = xmppConnection.getChatManager();
        myChatManagerListener = new MyChatManagerListener(this);
        chatmanager.addChatListener(myChatManagerListener);
    }

    //登录
    private void doLogin() throws XMPPException {
        xmppConnection.connect();   //连接
        try{
            if(checkConnectionListener!=null){
                xmppConnection.removeConnectionListener(checkConnectionListener);
                checkConnectionListener=null;
            }
        }catch(Exception e){

        }

        try{
            xmppConnection.login(username,password);//登录
            //如果登录成功
            if(xmppConnection.isAuthenticated()){
                //设置连接
                MyApplication.xmppConnection = xmppConnection;
                //设置用户
                PeopleItem peopleItem = new PeopleItem();
                peopleItem.setName(username);
                MyApplication.setUser(peopleItem);

                sendLoginBroadcast(true);
                //添加XMPP连接监听
                checkConnectionListener = new CheckConnectionListener(this);
                xmppConnection.addConnectionListener(checkConnectionListener);
                //注册好友状态监听
               friendsPacketListener = new FriendsPacketListener(this);
                PacketFilter filter = new AndFilter(new PacketTypeFilter(Packet.class));
                xmppConnection.addPacketListener(friendsPacketListener,filter);
                //更新数据
                ReFreshDataUtil.reFreshPeopleList();
            }
            else{
                sendLoginBroadcast(false);
                stopSelf();
            }
        }
        catch (Exception e){
            e.printStackTrace();
            sendLoginBroadcast(false);
            stopSelf();
        }


    }

    //发送登录状态广播
    void sendLoginBroadcast(boolean isLoginSuccess){
        Intent intent =new Intent(Const.ACTION_IS_LOGIN_SUCCESS);
        intent.putExtra("isLoginSuccess", isLoginSuccess);
        sendBroadcast(intent);
    }


    @Override
    public void onDestroy() {
        if(myChatManagerListener != null){
            xmppConnection.getChatManager().removeChatListener(myChatManagerListener);
        }

        if (xmppConnection != null) {
                xmppConnection.disconnect();
                xmppConnection = null;
        }

        if(xmppConnectionManager != null){
            xmppConnectionManager = null;
        }

        super.onDestroy();
    }




}
