package com.example.wtr.im.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.wtr.im.R;
import com.example.wtr.im.application.MyApplication;
import com.example.wtr.im.util.PreferencesUtil;
import com.example.wtr.im.util.ReFreshDataUtil;
import com.example.wtr.im.util.ToastUtil;
import com.example.wtr.im.util.XMPPUtil;


/**
 * Created by wtr on 2017/12/7.
 */

public class CreateGroupActivity extends Activity implements View.OnClickListener{
    private Context myContext;

    private LinearLayout backButton;
    private EditText groupName;
    private EditText password;
    private TextView createGroupButton;

    private String name;
    private String pwd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_create_group);
        myContext = this;
        findView();
        init();
    }
    private void findView(){
        backButton = (LinearLayout)findViewById(R.id.create_group_go_back);
        groupName = (EditText)findViewById(R.id.new_group_name);
        password = (EditText)findViewById(R.id.new_group_password);
        createGroupButton = (TextView)findViewById(R.id.create_group);
    }

    private  void  init(){
        backButton.setOnClickListener(this);
        createGroupButton.setOnClickListener(this);
    }

    private void createGroup() {
        final String username = PreferencesUtil.getSharedPreStr(myContext,"username");
        name = groupName.getText().toString().trim();
        pwd =  password.getText().toString().trim();
        //判断账号密码是否非空
        if(TextUtils.isEmpty(name)){
            ToastUtil.showShortToast(myContext,"用户名为空");
            return;
        }
        if(TextUtils.isEmpty(pwd)){
            ToastUtil.showShortToast(myContext,"密码为空");
            return;
        }

        if(XMPPUtil.createGroup(MyApplication.xmppConnection, name, username, pwd, myContext)) {
            ToastUtil.showShortToast(myContext,"创建群成功");
            ReFreshDataUtil.reFreshGroupList(name);
            finish();
        }
        else
            ToastUtil.showShortToast(myContext,"群名已存在或其他问题导致创建群失败");
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.create_group_go_back:
                finish();
                break;
            case R.id.create_group:
                createGroup();
                break;
        }
    }


}
