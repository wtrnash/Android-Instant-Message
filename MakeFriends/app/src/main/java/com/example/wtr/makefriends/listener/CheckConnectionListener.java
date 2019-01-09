package com.example.wtr.makefriends.listener;

import com.example.wtr.makefriends.service.XMPPService;
import com.example.wtr.makefriends.util.ToastUtil;

import org.jivesoftware.smack.ConnectionListener;


//主要为了处理账号在别处重复登录
public class CheckConnectionListener implements ConnectionListener {

    private XMPPService context;

    public CheckConnectionListener(XMPPService context){
        this.context=context;
    }

    @Override
    public void connectionClosed() {
        // TODO Auto-generated method stub

    }

    @Override
    public void connectionClosedOnError(Exception e) {
        if (e.getMessage().equals("stream:error (conflict)")) {
            ToastUtil.showLongToast(context, "您的账号在异地登录");
        }
    }

    @Override
    public void reconnectingIn(int arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void reconnectionFailed(Exception arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void reconnectionSuccessful() {
        // TODO Auto-generated method stub

    }

}

