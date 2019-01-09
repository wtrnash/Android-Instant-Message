package com.example.wtr.makefriends.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.example.wtr.makefriends.R;
import com.example.wtr.makefriends.service.XMPPService;
import com.example.wtr.makefriends.util.Const;
import com.example.wtr.makefriends.util.PreferencesUtil;
import com.example.wtr.makefriends.util.ServiceUtil;
import com.example.wtr.makefriends.util.ToastUtil;


public class LoginActivity extends Activity implements View.OnClickListener{
    private BroadcastReceiver receiver;

    private Context myContext;

    private EditText userIdInput;
    private EditText passwordInput;
    private TextView loadButton;
    private TextView newUserButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        myContext = this;
        findView();
        init();
        initReceiver();
    }

    private void findView(){
        userIdInput = (EditText)findViewById(R.id.load_user_id);
        passwordInput = (EditText)findViewById(R.id.load_password);
        loadButton = (TextView)findViewById(R.id.load_user_button);
        newUserButton = (TextView)findViewById(R.id.load_new_user_button);
    }

    private  void  init(){
        loadButton.setOnClickListener(this);
        newUserButton.setOnClickListener(this);
    }

    private void initReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(Const.ACTION_IS_LOGIN_SUCCESS)){
                    boolean isLoginSuccess=intent.getBooleanExtra("isLoginSuccess", false);
                    if(isLoginSuccess){//登录成功
                        ToastUtil.showShortToast(myContext, "登录成功");
                        Intent intent2 = new Intent(myContext, MainActivity.class);
                        startActivity(intent2);
                        finish();
                    }else{
                        ToastUtil.showShortToast(myContext, "登录失败，请检您的网络是否正常以及用户名和密码是否正确");
                    }
                }
            }
        };
        //注册广播接收者
        IntentFilter myFilter = new IntentFilter();
        myFilter.addAction(Const.ACTION_IS_LOGIN_SUCCESS);
        registerReceiver(receiver, myFilter);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.load_user_button:
               doLogin();
                break;
            case R.id.load_new_user_button:
                //跳转到注册界面
                Intent intent = new Intent(LoginActivity.this, NewUserActivity.class);
                startActivity(intent);
                break;
        }
    }

    //登录
    private void doLogin(){
        String username = userIdInput.getText().toString();
        String password  =  passwordInput.getText().toString();
        //判断账号密码是否非空
        if(TextUtils.isEmpty(username)){
            ToastUtil.showShortToast(myContext,"用户名为空");
            return;
        }
        if(TextUtils.isEmpty(password)){
            ToastUtil.showShortToast(myContext,"密码为空");
            return;
        }
        //非空后存入SharedPreference
        PreferencesUtil.putSharedPre(myContext,"username",username);
        PreferencesUtil.putSharedPre(myContext,"password",password);
        //启动服务进行XMPP连接以及登录等操作
        if(ServiceUtil.isServiceRunning(myContext,XMPPService.class.getName())){
            Intent stopIntent = new Intent(myContext, XMPPService.class);
            stopService(stopIntent);
        }
        Intent intent = new Intent(myContext, XMPPService.class);
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
