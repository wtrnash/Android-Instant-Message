package com.example.wtr.im.util;

import com.example.wtr.im.application.MyApplication;
import com.example.wtr.im.bean.PeopleItem;

import org.jivesoftware.smack.RosterEntry;

import java.util.List;

/**
 * Created by wtr on 2017/7/1.
 */

public class ReFreshDataUtil {
    public static void reFreshPeopleList(){
        List<PeopleItem> PeopleItemList = MyApplication.getMyApplication().getPeopleItemList();
        List<RosterEntry> rosterEntryList =  XMPPUtil.getAllEntries(MyApplication.xmppConnection);
        PeopleItemList.clear();
        for(RosterEntry r: rosterEntryList){
            PeopleItem peopleItem = new PeopleItem();
            peopleItem.setName(r.getUser().split("@")[0]);
            PeopleItemList.add(peopleItem);
        }
    }
}
