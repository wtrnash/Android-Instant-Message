package com.example.wtr.im.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.wtr.im.R;
import com.example.wtr.im.service.XMPPService;
import com.example.wtr.im.util.PreferencesUtil;
import com.example.wtr.im.util.ServiceUtil;
import com.example.wtr.im.util.ToastUtil;
import com.example.wtr.im.util.XMPPConnectionManager;
import com.example.wtr.im.util.XMPPUtil;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

/**
 * Created by wtr on 2017/6/28.
 */

public class NewUserActivity extends Activity implements View.OnClickListener{
    private Context myContext;

    private LinearLayout backButton;
    private EditText userName;
    private EditText password;
    private TextView newUserButton;

    private String name;
    private String pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_new_user);
        myContext = this;
        findView();
        init();
    }

    private void findView(){
        backButton = (LinearLayout)findViewById(R.id.new_user_go_back);
        userName = (EditText)findViewById(R.id.new_user_name);
        password = (EditText)findViewById(R.id.new_user_password);
        newUserButton = (TextView)findViewById(R.id.new_user_button2);
    }

    private  void  init(){
        backButton.setOnClickListener(this);
        newUserButton.setOnClickListener(this);
    }

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 0:
                    ToastUtil.showLongToast(myContext, "注册失败");
                    break;
                case 1:
                    ToastUtil.showLongToast(myContext, "注册成功");
                    PreferencesUtil.putSharedPre(myContext,"username",name);
                    PreferencesUtil.putSharedPre(myContext, "password", pwd);
                    //启动服务进行登录
                    if(ServiceUtil.isServiceRunning(myContext,XMPPService.class.getName())){
                        Intent stopIntent = new Intent(myContext, XMPPService.class);
                        stopService(stopIntent);
                    }
                    Intent intent = new Intent(myContext, XMPPService.class);
                    startService(intent);
                    finish();
                    break;
                case 2:
                    ToastUtil.showLongToast(myContext, "该昵称已被注册");
                    break;
                case 3:
                    ToastUtil.showLongToast(myContext, "注册失败");
                    break;
                case 4:
                    ToastUtil.showLongToast(myContext, "注册失败,请检查您的网络");
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.new_user_go_back:
                finish();
                break;
            case R.id.new_user_button2:
                newUser();
                break;
        }
    }

    private void newUser(){
         name = userName.getText().toString();
         pwd = password.getText().toString();
        if(TextUtils.isEmpty(name)){
            ToastUtil.showLongToast(myContext, "昵称为空");
            return;
        }
        if(TextUtils.isEmpty(pwd)){
            ToastUtil.showLongToast(myContext, "密码为空");
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                XMPPConnection xmppConnection = XMPPConnectionManager.init();
                try {
                    xmppConnection.connect();
                    int result = XMPPUtil.register(xmppConnection, name, pwd);  //注册
                    handler.sendEmptyMessage(result);
                } catch (XMPPException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(4);
                }
            }
        }).start();
    }
}
