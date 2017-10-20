package com.example.wtr.im.listener;

import com.example.wtr.im.service.XMPPService;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;

/**
 * Created by wtr on 2017/7/1.
 */

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
