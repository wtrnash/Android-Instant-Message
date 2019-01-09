package com.example.wtr.makefriends.listener;

import com.example.wtr.makefriends.service.XMPPService;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;


public class MyChatManagerListener implements ChatManagerListener {
    XMPPService context;
    public MyChatManagerListener(XMPPService context){
        this.context = context;
    }
    @Override
    public void chatCreated(Chat chat, boolean arg1) {
        chat.addMessageListener(new MsgListener(context));
    }
}
