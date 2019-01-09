package com.example.wtr.makefriends.util;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.wtr.makefriends.application.MyApplication;
import com.example.wtr.makefriends.bean.GroupItem;
import com.example.wtr.makefriends.bean.PeopleItem;
import com.example.wtr.makefriends.listener.PeopleListResponseListener;

import org.jivesoftware.smack.RosterEntry;

import java.util.List;

import static com.example.wtr.makefriends.application.MyApplication.xmppConnection;
import static com.example.wtr.makefriends.util.Const.IP;

public class ReFreshDataUtil {
    public static void reFreshPeopleList(Context context){
        List<PeopleItem> PeopleItemList = MyApplication.getMyApplication().getPeopleItemList();
        List<RosterEntry> rosterEntryList =  XMPPUtil.getAllEntries(xmppConnection);
        PeopleItemList.clear();
        for(RosterEntry r: rosterEntryList){
            PeopleItem peopleItem = new PeopleItem();
            peopleItem.setName(r.getUser().split("@")[0]);
            String url ="http://" + IP + "/Communicate/MyServlet?order=getUser&username=" + peopleItem.getName();
            RequestQueue queues = Volley.newRequestQueue(context.getApplicationContext());// Volley框架必用，实例化请求队列

            StringRequest request = new StringRequest(Request.Method.GET, url, // StringRequest请求
                   new PeopleListResponseListener(), new Response.ErrorListener() {// 未成功得到响应数据
                @Override
                public void onErrorResponse(VolleyError arg0) {

                }
            });
            request.setTag("volleyGet");// 设置请求标签Tag
            queues.add(request);// 将请求加入队列queue中处理

        }
    }

    //新加群，更新群列表
    public static void reFreshGroupList(String name){
        List<GroupItem> groupItemList = MyApplication.getMyApplication().getGroupItemList();
        GroupItem groupItem = new GroupItem();
        groupItem.setName(name);
        groupItemList.add(groupItem);
    }

}
