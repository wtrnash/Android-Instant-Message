package com.example.wtr.makefriends.listener;

import android.content.Intent;
import android.text.TextUtils;

import com.example.wtr.makefriends.service.XMPPService;
import com.example.wtr.makefriends.util.Const;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;


public class GroupMessageListener implements PacketListener {
    XMPPService context;
    public  GroupMessageListener(XMPPService context){
        this.context = context;
    }
    @Override
    public void processPacket(Packet packet) {
        Message message = (Message) packet;
        String msgBody = message.getBody();
        //如果消息为空
        if (TextUtils.isEmpty(msgBody))
            return;

        Intent intent = new Intent(Const.ACTION_NEW_MESSAGE);//发送广播到聊天界面
        intent.putExtra("message", msgBody);
        context.sendBroadcast(intent);
    }
}
