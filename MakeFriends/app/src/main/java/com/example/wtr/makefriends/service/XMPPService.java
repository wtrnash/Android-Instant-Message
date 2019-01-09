package com.example.wtr.makefriends.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.wtr.makefriends.application.MyApplication;
import com.example.wtr.makefriends.bean.PeopleItem;
import com.example.wtr.makefriends.listener.CheckConnectionListener;
import com.example.wtr.makefriends.listener.FriendsPacketListener;
import com.example.wtr.makefriends.listener.MyChatManagerListener;
import com.example.wtr.makefriends.util.Const;
import com.example.wtr.makefriends.util.PreferencesUtil;
import com.example.wtr.makefriends.util.ReFreshDataUtil;
import com.example.wtr.makefriends.util.XMPPConnectionManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Packet;

import java.util.List;

import static com.example.wtr.makefriends.util.Const.IP;


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
                String url ="http://" + IP + "/Communicate/MyServlet?order=getUser&username="+username;
                RequestQueue queues = Volley.newRequestQueue(getApplicationContext());// Volley框架必用，实例化请求队列
                StringRequest request = new StringRequest(Request.Method.GET, url, // StringRequest请求
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String arg0) {// 成功得到响应数据
                                try {
                                        Gson gson = new Gson();
                                        List<PeopleItem> peopleItemList = gson.fromJson(arg0, new TypeToken<List<PeopleItem>>(){}.getType());
                                        PeopleItem peopleItem = peopleItemList.get(0);
                                        if(peopleItem.getSex().equals("男"))
                                            peopleItem.setSex("male");
                                        else
                                            peopleItem.setSex("female");
                                        MyApplication.setUser(peopleItem);
                                        MyApplication.setXmppService(getThisService());
                                        //设置连接
                                        MyApplication.xmppConnection = xmppConnection;

                                        //添加XMPP连接监听
                                        checkConnectionListener = new CheckConnectionListener(getThisService());
                                        xmppConnection.addConnectionListener(checkConnectionListener);
                                        //注册好友状态监听
                                        friendsPacketListener = new FriendsPacketListener(getThisService());
                                        PacketFilter filter = new AndFilter(new PacketTypeFilter(Packet.class));
                                        xmppConnection.addPacketListener(friendsPacketListener,filter);
                                        //更新数据
                                        ReFreshDataUtil.reFreshPeopleList(myContext);
                                        sendLoginBroadcast(true);
                                } catch(Exception e){
                                    sendLoginBroadcast(false);
                                    stopSelf();
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {// 未成功得到响应数据
                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        sendLoginBroadcast(false);
                        stopSelf();
                    }
                });
                request.setTag("volleyGet");// 设置请求标签Tag
                queues.add(request);// 将请求加入队列queue中处理

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

    public XMPPService getThisService(){
        return this;
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
