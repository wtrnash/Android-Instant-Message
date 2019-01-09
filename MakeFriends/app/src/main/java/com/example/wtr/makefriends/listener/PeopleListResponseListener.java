package com.example.wtr.makefriends.listener;

import com.android.volley.Response;
import com.example.wtr.makefriends.application.MyApplication;
import com.example.wtr.makefriends.bean.PeopleItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;


public class PeopleListResponseListener implements Response.Listener<String> {
    private List<PeopleItem> PeopleItemList = MyApplication.getMyApplication().getPeopleItemList();
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
            PeopleItemList.add(peopleItem);

        } catch(Exception e){

            e.printStackTrace();
        }
    }

}
