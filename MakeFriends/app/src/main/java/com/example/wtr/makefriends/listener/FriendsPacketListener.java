package com.example.wtr.makefriends.listener;

import android.content.Intent;
import android.util.Log;

import com.example.wtr.makefriends.service.XMPPService;
import com.example.wtr.makefriends.util.Const;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;


public class FriendsPacketListener implements PacketListener {
    XMPPService context;
    public FriendsPacketListener(XMPPService context){
        this.context=context;
    }

    @Override
    public void processPacket(Packet packet) {
        if(packet == null){
            return;
        }
        if(packet.getFrom() != null && packet.getTo() != null && packet.getFrom().equals(packet.getTo())){
            return;
        }
        if (packet instanceof Presence) {
            Presence presence = (Presence) packet;
            final String from = presence.getFrom().split("@")[0];//发送方
            String to = presence.getTo().split("@")[0];//接收方
            String status = presence.getStatus();
            if(from.equals(to)){
                return;
            }
            if (presence.getType().equals(Presence.Type.subscribe)) {//好友申请
                Log.e("friend", "好友申请");
                Intent intent= new Intent(Const.ACTION_ADD_FRIEND);
                intent.putExtra("from",from);
                intent.putExtra("status",status);
                context.sendBroadcast(intent);
            } else if (presence.getType().equals(Presence.Type.subscribed)) {//同意添加好友
                Log.e("friend", "同意添加好友");

            }
        }
    };
}

