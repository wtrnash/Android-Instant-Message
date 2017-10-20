package com.example.wtr.im.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wtr.im.R;
import com.example.wtr.im.application.MyApplication;
import com.example.wtr.im.bean.GroupItem;
import com.example.wtr.im.bean.PeopleItem;
import com.example.wtr.im.fragment.GroupListFragment;
import com.example.wtr.im.fragment.PeopleListFragment;
import com.example.wtr.im.util.ToastUtil;
import com.example.wtr.im.util.XMPPUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wtr on 2017/6/30.
 */

public class AddFriendsActivity  extends FragmentActivity implements View.OnClickListener {
    public static final int ON_PEOPLE = 0;
    public static final int ON_GROUP = 1;

    private Context myContext;
    private ImageView backButton;
    private EditText inputText;
    private ImageView clearButton;
    private TextView searchButton;

    /**
     * 两个碎片
     */
    private PeopleListFragment peopleList;
    private GroupListFragment groupList;

    /**
     * 两个按钮控制两个碎片
     */
    private TextView peopleButton;
    private TextView groupButton;

    /**
     * 两个数据列
     */
    private List<PeopleItem> peopleItemLists;
    private List<GroupItem> groupItemLists;

    private FragmentManager fragmentManager;

    private String username;

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 1:
                    doRefresh();
                    break;
                case -1:
                    ToastUtil.showLongToast( myContext,"未查询到信息");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_friends);
        myContext = this;
        initViews();
        fragmentManager = getSupportFragmentManager();
        selectFrame(ON_PEOPLE);
    }

    private void initViews(){
        backButton = (ImageView)findViewById(R.id.addfriends_back);
        inputText = (EditText)findViewById(R.id.addfriends_input);
        clearButton = (ImageView)findViewById(R.id.addfriends_clear);
        searchButton = (TextView)findViewById(R.id.addfriends_search);
        peopleButton = (TextView)findViewById(R.id.addfriends_people);
        groupButton = (TextView)findViewById(R.id.addfriends_group);
        peopleItemLists = new ArrayList<PeopleItem>();
        groupItemLists = new ArrayList<GroupItem>();

        backButton.setOnClickListener(this);
        clearButton.setOnClickListener(this);
        searchButton.setOnClickListener(this);

        peopleButton.setOnClickListener(this);
        groupButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.addfriends_back:
                finish();
                break;
            case R.id.addfriends_clear:
                inputText.setText("");
                break;
            case R.id.addfriends_search:
                doSearch();
                break;
            case R.id.addfriends_people:
                //切换字体颜色
                groupButton.setTextColor(0xff999999);
                peopleButton.setTextColor(0xff0099ff);
                selectFrame(ON_PEOPLE);
                break;
            case R.id.addfriends_group:
                peopleButton.setTextColor(0xff999999);
                groupButton.setTextColor(0xff0099ff);
                selectFrame(ON_GROUP);
                break;
        }
    }

    private void selectFrame(int index){

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        hideFrame(transaction);//隐藏碎片

        switch(index){
            case ON_PEOPLE:
                transaction.show(peopleList);
                break;
            case ON_GROUP:
                transaction.show(groupList);
                break;
        }
        transaction.commit();
    }

    private void hideFrame(FragmentTransaction transaction){
        if(peopleList != null){transaction.hide(peopleList);}
        else{
            peopleList = new PeopleListFragment();
            transaction.add(R.id.addfriend_framelist, peopleList);
            transaction.hide(peopleList);
        }
        if(groupList != null){transaction.hide(groupList);}
        else{
            groupList = new GroupListFragment();
            transaction.add(R.id.addfriend_framelist, groupList);
            transaction.hide(groupList);}
    }

    private void doSearch(){
         username = inputText.getText().toString();
        if(TextUtils.isEmpty(username)){
            ToastUtil.showShortToast(myContext,"输入为空");
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                peopleItemLists = XMPPUtil.searchUsers(MyApplication.xmppConnection, username);
                if(peopleItemLists.size() > 0){
                    mHandler.sendEmptyMessage(1);
                }else{
                    mHandler.sendEmptyMessage(-1);
                }
            }
        }).start();
    }

    private void doRefresh(){
        if(peopleList != null){
            peopleList.refreshData(peopleItemLists);
        }
        if(groupList != null){
            groupList.refreshData(groupItemLists);
        }
    }

}
