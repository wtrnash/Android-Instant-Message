package com.example.wtr.makefriends.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Window;
import android.widget.ImageView;

import com.example.wtr.makefriends.R;
import com.example.wtr.makefriends.service.XMPPService;
import com.example.wtr.makefriends.util.Const;
import com.example.wtr.makefriends.util.PreferencesUtil;
import com.example.wtr.makefriends.util.ServiceUtil;
import com.example.wtr.makefriends.util.ToastUtil;

public class LoadingActivity extends Activity {

    public static final int UPDATE_LOGO = 1;
    public static final int JUMP = 2;

    private Context myContext;

    private ImageView logo;

    private MyThread myThread;
    private MyThread2 myThread2;
    private MyHandle myHandle;

    private BroadcastReceiver receiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_loading);
        initReceiver();
        myContext = this;
        logo = (ImageView)findViewById(R.id.loading_logo);

        myHandle = new MyHandle();
        myThread = new MyThread();
        new Thread(myThread).start();
        myThread2 = new MyThread2();
        new Thread(myThread2).start();
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
                        Intent intent3 = new Intent(LoadingActivity.this, LoginActivity.class);
                        startActivity(intent3);
                        finish();
                    }
                }
            }
        };
        //注册广播接收者
        IntentFilter myFilter = new IntentFilter();
        myFilter.addAction(Const.ACTION_IS_LOGIN_SUCCESS);
        registerReceiver(receiver, myFilter);
    }

    @SuppressLint("HandlerLeak")
    class MyHandle extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 此处可以更新UI
            Bundle bundle = msg.getData();
            switch(msg.what){
                case UPDATE_LOGO:
                    int st = bundle.getInt("stage");
                    turn(st);
                    break;
                case JUMP:
                    int de = bundle.getInt("turn");
                    doJump(de);
                    break;
                default:
                    break;
            }
        }
    }

    private void turn(int stage){
        switch (stage) {
            case 0:
                logo.setImageResource(R.drawable.loading_0);
                break;
            case 1:
                logo.setImageResource(R.drawable.loading_1);
                break;
            case 2:
                logo.setImageResource(R.drawable.loading_2);
                break;
            case 3:
                logo.setImageResource(R.drawable.loading_3);
                break;
            default:
                logo.setImageResource(R.drawable.loading_3);
                break;
        }
    }

    private void doJump(int de){
        if(de == 0){
            Intent intent = new Intent(LoadingActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else{
            if (ServiceUtil.isServiceRunning(myContext,XMPPService.class.getName())){
                Intent stopIntent = new Intent(myContext, XMPPService.class);
                stopService(stopIntent);
            }
            Intent intent = new Intent(myContext, XMPPService.class);
            startService(intent);
        }

    }

    class MyThread implements Runnable{
        @Override
        public void run() {
            int st = 0;
            while(true){
                try {
                    Thread.sleep(1000);//延时1秒
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
                st = (st + 1) % 4;
                Message message = new Message();
                message.what = UPDATE_LOGO;
                Bundle bundle = new Bundle();// 存放数据
                bundle.putInt("stage",st);
                message.setData(bundle);
                myHandle.sendMessage(message);
            }
        }
    }

    class MyThread2 implements Runnable{
        @Override
        public void run() {
            int de = 0;//0未登录 1已登录
            String username = PreferencesUtil.getSharedPreStr(myContext,"username");
            String password = PreferencesUtil.getSharedPreStr(myContext,"password");
            if(!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password) ){
                de = 1;
            }

            Message message = new Message();
            message.what = JUMP;
            Bundle bundle = new Bundle();// 存放数据
            bundle.putInt("turn",de);
            message.setData(bundle);
            myHandle.sendMessage(message);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
