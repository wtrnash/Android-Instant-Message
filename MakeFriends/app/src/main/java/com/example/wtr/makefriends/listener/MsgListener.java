package com.example.wtr.makefriends.listener;

import android.content.Intent;
import android.text.TextUtils;

import com.example.wtr.makefriends.service.XMPPService;
import com.example.wtr.makefriends.util.Const;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;


public class MsgListener implements MessageListener {
    XMPPService context;
    public  MsgListener(XMPPService context){
        this.context = context;
    }
    @Override
    public void processMessage(Chat chat, Message message) {
        String msgBody = message.getBody();
        //如果消息为空
        if (TextUtils.isEmpty(msgBody))
            return;

        Intent intent=new Intent(Const.ACTION_NEW_MESSAGE);//发送广播到聊天界面
        intent.putExtra("message", msgBody);
        context.sendBroadcast(intent);

    }
}
