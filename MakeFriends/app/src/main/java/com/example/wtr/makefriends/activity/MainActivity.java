package com.example.wtr.makefriends.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.wtr.makefriends.R;
import com.example.wtr.makefriends.application.MyApplication;
import com.example.wtr.makefriends.bean.Conversation;
import com.example.wtr.makefriends.bean.MessageItem;
import com.example.wtr.makefriends.fragment.LinkmanFragment;
import com.example.wtr.makefriends.fragment.MessageFragment;
import com.example.wtr.makefriends.fragment.UserFragment;
import com.example.wtr.makefriends.util.Const;
import com.example.wtr.makefriends.util.ReFreshDataUtil;
import com.example.wtr.makefriends.util.XMPPUtil;
import com.example.wtr.makefriends.util.PreferencesUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class MainActivity extends FragmentActivity implements View.OnClickListener{
    LinearLayout mainLinearLayout;
    public static final int ON_MESSAGE = 0;
    public static final int ON_LINKMAN = 1;
    public static final int ON_USER = 2;

    private int STAGE = ON_MESSAGE;

    private TextView title;
    private ImageView portrait;
    private TextView extra;


    private MessageFragment messages;
    private LinkmanFragment linkman;
    private UserFragment user;

    private LinearLayout messageButton;
    private LinearLayout linkmanButton;
    private LinearLayout userButton;


    private FragmentManager fragmentManager;

    private BroadcastReceiver receiver;

    PopupWindow popWindow;
    TextView floatFromUsername,floatStatus;
    TextView floatAgree,floatRefuse;
    private MyHandle myHandle;
    private Context myContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        myHandle = new MyHandle();
        myContext = this;
        initView();
        fragmentManager = getSupportFragmentManager();
        selectFrame(ON_MESSAGE);
        initReceiver();
    }

    private void initView(){
        mainLinearLayout = (LinearLayout) findViewById(R.id.main_linear_layout);
        title = (TextView)findViewById(R.id.header_text);
        portrait = (ImageView)findViewById(R.id.header_portrait);
        extra = (TextView)findViewById(R.id.header_extra);
        messageButton = (LinearLayout)findViewById(R.id.footer_message);
        linkmanButton = (LinearLayout)findViewById(R.id.footer_linkman);
        userButton = (LinearLayout)findViewById(R.id.footer_user);

        messageButton.setOnClickListener(this);
        linkmanButton.setOnClickListener(this);
        userButton.setOnClickListener(this);

        extra.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.header_extra:
                switch(STAGE){
                    case ON_MESSAGE:
                        break;
                    case ON_LINKMAN:
                        Intent intent = new Intent(MainActivity.this,AddFriendsActivity.class);
                        startActivity(intent);
                        break;
                    case ON_USER:
                        break;
                }
                break;
            case R.id.footer_message:
                selectFrame(ON_MESSAGE);
                break;
            case R.id.footer_linkman:
                selectFrame(ON_LINKMAN);
                break;
            case R.id.footer_user:
                selectFrame(ON_USER);
                break;
        }
    }

    private void selectFrame(int index){
        //重置按钮
        if(index != STAGE){
            switch(STAGE){
                case ON_MESSAGE:
                    ((ImageView)messageButton.findViewById(R.id.footer_message_img)).
                            setImageResource(R.drawable.message_normal);
                    ((TextView)messageButton.findViewById(R.id.footer_message_text)).
                            setTextColor(0xFF999999);
                    break;
                case ON_LINKMAN:
                    ((ImageView)linkmanButton.findViewById(R.id.footer_linkman_img)).
                            setImageResource(R.drawable.linkman_normal);
                    ((TextView)linkmanButton.findViewById(R.id.footer_linkman_text)).
                            setTextColor(0xFF999999);
                    break;
                case ON_USER:
                    ((ImageView)userButton.findViewById(R.id.footer_user_img)).
                            setImageResource(R.drawable.user_normal);
                    ((TextView)userButton.findViewById(R.id.footer_user_text)).
                            setTextColor(0xFF999999);
                    break;
            }
        }

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        hideFrame(transaction);//隐藏碎片

        //切换
        switch(index){
            case ON_MESSAGE:
                ((ImageView)messageButton.findViewById(R.id.footer_message_img)).
                        setImageResource(R.drawable.message_pressed);
                ((TextView)messageButton.findViewById(R.id.footer_message_text)).
                        setTextColor(0xFF0099ff);
                title.setText("消息");
                extra.setText("");
                extra.setVisibility(View.VISIBLE);
                STAGE = ON_MESSAGE;
                if(messages == null){
                    messages = new MessageFragment();
                    transaction.add(R.id.frame,messages);
                }
                else{
                    messages.doRefresh();
                    transaction.show(messages);
                }
                break;
            case ON_LINKMAN:
                ((ImageView)linkmanButton.findViewById(R.id.footer_linkman_img)).
                        setImageResource(R.drawable.linkman_pressed);
                ((TextView)linkmanButton.findViewById(R.id.footer_linkman_text)).
                        setTextColor(0xFF0099ff);
                title.setText("联系人");
                extra.setText("添加");
                extra.setVisibility(View.VISIBLE);
                STAGE = ON_LINKMAN;
                if(linkman == null){
                    linkman = new LinkmanFragment();
                    transaction.add(R.id.frame, linkman);
                }
                else{
                    transaction.show(linkman);
                }
                break;
            case ON_USER:
                ((ImageView)userButton.findViewById(R.id.footer_user_img)).
                        setImageResource(R.drawable.user_pressed);
                ((TextView)userButton.findViewById(R.id.footer_user_text)).
                        setTextColor(0xFF0099ff);
                title.setText("用户");
                extra.setText("");
                extra.setVisibility(View.VISIBLE);
                STAGE = ON_USER;
                if(user == null){
                    user = new UserFragment();
                    transaction.add(R.id.frame,user);
                }
                else{
                    transaction.show(user);
                }
                break;
        }
        transaction.commit();

    }

    private void hideFrame(FragmentTransaction transaction){
        if(messages != null){transaction.hide(messages);}
        if(linkman != null){transaction.hide(linkman);}
        if(user != null){transaction.hide(user);}
    }

    //新建广播接收器
    private void initReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //如果是加好友的广播
                if(intent.getAction().equals(Const.ACTION_ADD_FRIEND)){
                  String status = intent.getStringExtra("status");  //获得额外的信息
                  String from = intent.getStringExtra("from");     //获得发送人
                  showPopupWindow(mainLinearLayout,from,status);    //显示弹窗
                } //如果是新消息的广播
                else if(intent.getAction().equals(Const.ACTION_NEW_MESSAGE)){
                    String message = intent.getStringExtra("message");     //获得消息
                    dealWithMessage(message);      //对接收到的消息进行处理
                }

            }
        };
        //注册广播接收者
        IntentFilter myFilter = new IntentFilter();
        myFilter.addAction(Const.ACTION_ADD_FRIEND);
        myFilter.addAction(Const.ACTION_NEW_MESSAGE);
        registerReceiver(receiver, myFilter);
    }

    private void dealWithMessage(String message){
        //发送者卍是否群聊卍消息类型卍消息内容卍发送时间卍群名卍语音时长
        String[] word = message.split(Const.SPLIT);

        String from = word[0];//发送者，谁给你发的消息
        final String isGroupMessage = word[1];   //是否是群聊
        String messageType = word[2];       //消息类型
        String messageContent = word[3];    //消息内容
        String messageTime = word[4];     //消息时间
        String groupName = word[5];        //如果是群聊，则有群名
        String time = word[6];

        //1代表是文字
        if(messageType.equals("1")) {
            MessageItem item = new MessageItem();  //新建消息类，进行设置成员变量
            item.setUsername(from);
            item.setIsRead(false);
            item.setDate(messageTime);
            item.setText(messageContent);
            List<Conversation> conversationList = MyApplication.getMyApplication().getConversationList(); //获得对话List
            //如果是单聊
            if(isGroupMessage.equals("false")){
                for(int position = 0; position < conversationList.size();position++){
                    if(!conversationList.get(position).getGroupConversation() &&
                            conversationList.get(position).getName().equals(from)){   //如果不是群聊，已经有对应的对话，则重新设置对话的属性，并移到消息列表的最上面
                        Conversation conversation = conversationList.remove(position);  //移除
                        conversation.setLastTime(messageTime);  //设置最后条消息的时间
                        conversation.setLastMessage(messageContent);   //设置最后条消息的内容
                        int count =  conversation.getNewMessageCount() + 1 ;  //未读消息数量+1
                        conversation.setNewMessageCount(count);  //设置未读消息
                        conversation.getWordList().add(item);   //将消息加入对话中的消息列表
                        conversationList.add(0,conversation);   //将该对话放在显示的首位
                        if(messages != null){
                            messages.doRefresh();  //刷新listView
                        }
                        return;
                    }
                }
                //没有同名则新建一个会话，还是和上面类似的操作
                Conversation conversation = new Conversation();
                conversation.setGroupConversation(false);   //不是群聊
                conversation.setName(from);
                conversation.setLastTime(messageTime);
                conversation.setLastMessage(messageContent);   //设置最后条消息的内容
                int count = 1 ;
                conversation.setNewMessageCount(count);
                conversation.getWordList().add(item);
                conversationList.add(0,conversation);
                if(messages != null){
                    messages.doRefresh();
                }
            }
            else{             //如果是群聊
                //群聊会接收到自己的信息，所以要判断发送者是不是自己
                String username = PreferencesUtil.getSharedPreStr(this,"username");
                if(username.equals(from))
                    return;

                for(int position = 0; position < conversationList.size();position++){
                    if(conversationList.get(position).getGroupConversation() &&
                            conversationList.get(position).getGroupName().equals(groupName)){   //如果已经有对应的对话，则重新设置对话的属性，并移到消息列表的最上面
                        Conversation conversation = conversationList.remove(position);  //移除
                        conversation.setLastTime(messageTime);  //设置最后条消息的时间
                        conversation.setLastMessage(messageContent);   //设置最后条消息的内容
                        conversation.setName(from);                 //设置最后条信息的发送者
                        int count =  conversation.getNewMessageCount() + 1 ;  //未读消息数量+1
                        conversation.setNewMessageCount(count);  //设置未读消息
                        conversation.getWordList().add(item);   //将消息加入对话中的消息列表
                        conversationList.add(0,conversation);   //将该对话放在显示的首位
                        if(messages != null){
                            messages.doRefresh();  //刷新listView
                        }
                        return;
                    }
                }
                //没有同名则新建一个会话，还是和上面类似的操作
                Conversation conversation = new Conversation();
                conversation.setGroupConversation(true);
                conversation.setGroupName(groupName);
                conversation.setName(from);
                conversation.setLastTime(messageTime);
                conversation.setLastMessage(messageContent);   //设置最后条消息的内容
                int count = 1 ;
                conversation.setNewMessageCount(count);
                conversation.getWordList().add(item);
                conversationList.add(0,conversation);
                if(messages != null){
                    messages.doRefresh();
                }
            }

        }//2表示是图像,网络请求需要在子线程处理
        else if(messageType.equals("2")){
            final String tempFrom = from;//发送者，谁给你发的消息
            final String tempIsGroupMessage = isGroupMessage;   //是否是群聊
            final String tempMessageType = messageType;       //消息类型
            final String tempMessageContent = messageContent;    //消息内容
            final String tempMessageTime = messageTime;     //消息时间
            final String tempGroupName = groupName;        //如果是群聊，则有群名
            final String username =  PreferencesUtil.getSharedPreStr(this,"username");

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Bitmap bitmap = ImageLoader.getInstance().loadImageSync(tempMessageContent);
                    Message message = new Message();
                    message.obj = bitmap;
                    Bundle bundle = new Bundle();// 存放数据
                    bundle.putString("from",tempFrom);
                    bundle.putString("messageType", tempMessageType);
                    bundle.putString("isGroupMessage", tempIsGroupMessage);
                    bundle.putString("messageTime", tempMessageTime);
                    bundle.putString("groupName", tempGroupName);
                    bundle.putString("username", username);
                    message.setData(bundle);
                    myHandle.sendMessage(message);
                }
            }).start();

        }   //如果是语音
        else if(messageType.equals("3")){
            int intTime = Integer.parseInt(time);
            MessageItem item = new MessageItem();  //新建消息类，进行设置成员变量
            item.setUsername(from);
            item.setIsRead(false);
            item.setDate(messageTime);
            item.setSoundTime(intTime);
            item.setText("");
            item.setSoundPath(messageContent);
            List<Conversation> conversationList = MyApplication.getMyApplication().getConversationList(); //获得对话List
            //如果是单聊
            if(isGroupMessage.equals("false")){
                for(int position = 0; position < conversationList.size();position++){
                    if(!conversationList.get(position).getGroupConversation() &&
                            conversationList.get(position).getName().equals(from)){   //如果不是群聊，已经有对应的对话，则重新设置对话的属性，并移到消息列表的最上面
                        Conversation conversation = conversationList.remove(position);  //移除
                        conversation.setLastTime(messageTime);  //设置最后条消息的时间
                        conversation.setLastMessage("[语音]");   //设置最后条消息的内容
                        int count =  conversation.getNewMessageCount() + 1 ;  //未读消息数量+1
                        conversation.setNewMessageCount(count);  //设置未读消息
                        conversation.getWordList().add(item);   //将消息加入对话中的消息列表
                        conversationList.add(0,conversation);   //将该对话放在显示的首位
                        if(messages != null){
                            messages.doRefresh();  //刷新listView
                        }
                        return;
                    }
                }
                //没有同名则新建一个会话，还是和上面类似的操作
                Conversation conversation = new Conversation();
                conversation.setGroupConversation(false);   //不是群聊
                conversation.setName(from);
                conversation.setLastTime(messageTime);
                conversation.setLastMessage("[语音]");   //设置最后条消息的内容
                int count = 1 ;
                conversation.setNewMessageCount(count);
                conversation.getWordList().add(item);
                conversationList.add(0,conversation);
                if(messages != null){
                    messages.doRefresh();
                }
            }
            else{             //如果是群聊
                //群聊会接收到自己的信息，所以要判断发送者是不是自己
                String username = PreferencesUtil.getSharedPreStr(this,"username");
                if(username.equals(from))
                    return;

                for(int position = 0; position < conversationList.size();position++){
                    if(conversationList.get(position).getGroupConversation() &&
                            conversationList.get(position).getGroupName().equals(groupName)){   //如果已经有对应的对话，则重新设置对话的属性，并移到消息列表的最上面
                        Conversation conversation = conversationList.remove(position);  //移除
                        conversation.setLastTime(messageTime);  //设置最后条消息的时间
                        conversation.setLastMessage("语音");   //设置最后条消息的内容
                        conversation.setName(from);                 //设置最后条信息的发送者
                        int count =  conversation.getNewMessageCount() + 1 ;  //未读消息数量+1
                        conversation.setNewMessageCount(count);  //设置未读消息
                        conversation.getWordList().add(item);   //将消息加入对话中的消息列表
                        conversationList.add(0,conversation);   //将该对话放在显示的首位
                        if(messages != null){
                            messages.doRefresh();  //刷新listView
                        }
                        return;
                    }
                }
                //没有同名则新建一个会话，还是和上面类似的操作
                Conversation conversation = new Conversation();
                conversation.setGroupConversation(true);
                conversation.setGroupName(groupName);
                conversation.setName(from);
                conversation.setLastTime(messageTime);
                conversation.setLastMessage("语音");   //设置最后条消息的内容
                int count = 1 ;
                conversation.setNewMessageCount(count);
                conversation.getWordList().add(item);
                conversationList.add(0,conversation);
                if(messages != null){
                    messages.doRefresh();
                }
            }
        }
    }

    //显示弹窗
    @SuppressWarnings("deprecation")
    private void showPopupWindow(View parent,String from,String status){
        if (popWindow == null) {
            View view =  getLayoutInflater().inflate(R.layout.float_window,null);
            popWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,true);
            initPop(view,from,status);
        }
        popWindow.setAnimationStyle(android.R.style.Animation_InputMethod);
        popWindow.setFocusable(true);
        popWindow.setOutsideTouchable(true);
        popWindow.setBackgroundDrawable(new BitmapDrawable());
        popWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
    }

    public void initPop(View view,String from,String status){
        floatAgree = (TextView) view.findViewById(R.id.float_agree);
        floatRefuse = (TextView) view.findViewById(R.id.float_refuse);
        floatFromUsername = (TextView) view.findViewById(R.id.float_from_username);
        floatStatus = (TextView) view.findViewById(R.id.float_status);
        floatFromUsername.setText(from);
        floatStatus.setText(status);
        floatAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //同意添加好友
                XMPPUtil.agreeApplyForFriend(MyApplication.xmppConnection,floatFromUsername.getText().toString());
                ReFreshDataUtil.reFreshPeopleList(myContext); //更新好友列表
                popWindow.dismiss();
            }
        });
        floatRefuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWindow.dismiss();
            }
        });

    }


    @SuppressLint("HandlerLeak")
    class MyHandle extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bitmap bitmap = (Bitmap) msg.obj;
            Bundle bundle = msg.getData();
            String from = bundle.getString("from");
            String messageType = bundle.getString("messageType");
            String isGroupMessage = bundle.getString("isGroupMessage");
            String messageTime = bundle.getString("messageTime");
            String groupName = bundle.getString("groupName");
            String username = bundle.getString("username");
            MessageItem item = new MessageItem();  //新建消息类，进行设置成员变量
            item.setUsername(from);
            item.setIsRead(false);
            item.setDate(messageTime);
            item.setBitmap(bitmap);

            List<Conversation> conversationList = MyApplication.getMyApplication().getConversationList(); //获得对话List
            if(messageType.equals("2")){
                //如果是单聊
                if(isGroupMessage.equals("false")){
                    for(int position = 0; position < conversationList.size();position++){
                        if(!conversationList.get(position).getGroupConversation() &&
                                conversationList.get(position).getName().equals(from)){   //如果不是群聊，已经有对应的对话，则重新设置对话的属性，并移到消息列表的最上面
                            Conversation conversation = conversationList.remove(position);  //移除
                            conversation.setLastTime(messageTime);  //设置最后条消息的时间
                            conversation.setLastMessage("[图片]");   //设置最后条消息的内容
                            int count =  conversation.getNewMessageCount() + 1 ;  //未读消息数量+1
                            conversation.setNewMessageCount(count);  //设置未读消息
                            conversation.getWordList().add(item);   //将消息加入对话中的消息列表
                            conversationList.add(0,conversation);   //将该对话放在显示的首位
                            if(messages != null){
                                messages.doRefresh();  //刷新listView
                            }
                            return;
                        }
                    }
                    //没有同名则新建一个会话，还是和上面类似的操作
                    Conversation conversation = new Conversation();
                    conversation.setGroupConversation(false);   //不是群聊
                    conversation.setName(from);
                    conversation.setLastTime(messageTime);
                    conversation.setLastMessage("[图片]");   //设置最后条消息的内容
                    int count = 1 ;
                    conversation.setNewMessageCount(count);
                    conversation.getWordList().add(item);
                    conversationList.add(0,conversation);
                    if(messages != null){
                        messages.doRefresh();
                    }
                }
                else{             //如果是群聊
                    //群聊会接收到自己的信息，所以要判断发送者是不是自己

                    if(username.equals(from))
                        return;

                    for(int position = 0; position < conversationList.size();position++){
                        if(conversationList.get(position).getGroupConversation() &&
                                conversationList.get(position).getGroupName().equals(groupName)){   //如果已经有对应的对话，则重新设置对话的属性，并移到消息列表的最上面
                            Conversation conversation = conversationList.remove(position);  //移除
                            conversation.setLastTime(messageTime);  //设置最后条消息的时间
                            conversation.setLastMessage("[图片]");   //设置最后条消息的内容
                            conversation.setName(from);                 //设置最后条信息的发送者
                            int count =  conversation.getNewMessageCount() + 1 ;  //未读消息数量+1
                            conversation.setNewMessageCount(count);  //设置未读消息
                            conversation.getWordList().add(item);   //将消息加入对话中的消息列表
                            conversationList.add(0,conversation);   //将该对话放在显示的首位
                            if(messages != null){
                                messages.doRefresh();  //刷新listView
                            }
                            return;
                        }
                    }
                    //没有同名则新建一个会话，还是和上面类似的操作
                    Conversation conversation = new Conversation();
                    conversation.setGroupConversation(true);
                    conversation.setGroupName(groupName);
                    conversation.setName(from);
                    conversation.setLastTime(messageTime);
                    conversation.setLastMessage("[图片]");   //设置最后条消息的内容
                    int count = 1 ;
                    conversation.setNewMessageCount(count);
                    conversation.getWordList().add(item);
                    conversationList.add(0,conversation);
                    if(messages != null){
                        messages.doRefresh();
                    }
                }
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
