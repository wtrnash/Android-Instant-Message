package com.example.wtr.makefriends.activity;

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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.wtr.makefriends.R;
import com.example.wtr.makefriends.bean.GroupItem;
import com.example.wtr.makefriends.bean.PeopleItem;
import com.example.wtr.makefriends.fragment.GroupListFragment;
import com.example.wtr.makefriends.fragment.PeopleListFragment;
import com.example.wtr.makefriends.util.ToastUtil;
import com.example.wtr.makefriends.util.XMPPUtil;
import com.example.wtr.makefriends.application.MyApplication;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jivesoftware.smack.XMPPException;

import java.util.ArrayList;
import java.util.List;

import static com.example.wtr.makefriends.util.Const.IP;


public class AddFriendsActivity  extends FragmentActivity implements View.OnClickListener {
    public static final int ON_PEOPLE = 0;
    public static final int ON_GROUP = 1;
    private int status;

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
    private String groupName;

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
        status = ON_PEOPLE;
        selectFrame(status);
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
                if(status == ON_PEOPLE)
                    searchFriends();
                else
                    searchGroups();
                break;
            case R.id.addfriends_people:
                //切换字体颜色
                groupButton.setTextColor(0xff999999);
                peopleButton.setTextColor(0xff0099ff);
                status = ON_PEOPLE;
                selectFrame(status);
                break;
            case R.id.addfriends_group:
                peopleButton.setTextColor(0xff999999);
                groupButton.setTextColor(0xff0099ff);
                status = ON_GROUP;
                selectFrame(status);
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

    private void searchFriends(){
         username = inputText.getText().toString().trim();
        if(TextUtils.isEmpty(username)){
            ToastUtil.showShortToast(myContext,"输入为空");
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                peopleItemLists = XMPPUtil.searchUsers(MyApplication.xmppConnection, username);
                for(int i = 0; i < peopleItemLists.size(); i++){
                    final int  temp = i;
                    String searchPeopleName = peopleItemLists.get(temp).getName();
                    String url ="http://" + IP + "/Communicate/MyServlet?order=getUser&username=" + searchPeopleName;
                    RequestQueue queues = Volley.newRequestQueue(getApplicationContext());// Volley框架必用，实例化请求队列
                    StringRequest request = new StringRequest(Request.Method.GET, url, // StringRequest请求
                            new Response.Listener<String>() {
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

                                        peopleItemLists.set(temp, peopleItem);
                                        doRefresh();

                                    } catch(Exception e){

                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {// 未成功得到响应数据
                        @Override
                        public void onErrorResponse(VolleyError arg0) {

                        }
                    });
                    request.setTag("volleyGet");// 设置请求标签Tag
                    queues.add(request);// 将请求加入队列queue中处理
                }
                if(peopleItemLists.size() > 0){
                    mHandler.sendEmptyMessage(1);
                }else{
                    mHandler.sendEmptyMessage(-1);
                }
            }
        }).start();
    }

    private void searchGroups(){
        groupName = inputText.getText().toString().trim();
        if(TextUtils.isEmpty(groupName)){
            ToastUtil.showShortToast(myContext,"输入为空");
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    groupItemLists = XMPPUtil.getGroups(MyApplication.xmppConnection, groupName);
                } catch (XMPPException e) {
                    e.printStackTrace();
                }
                if(groupItemLists.size() > 0){
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
