package com.example.wtr.im.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.wtr.im.R;
import com.example.wtr.im.application.MyApplication;
import com.example.wtr.im.bean.Conversation;
import com.example.wtr.im.bean.PeopleItem;
import com.example.wtr.im.util.PreferencesUtil;
import com.example.wtr.im.util.ToastUtil;
import com.example.wtr.im.util.XMPPUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by wtr on 2017/7/2.
 */

public class ShowPeopleActivity extends Activity implements View.OnClickListener {
    PopupWindow popWindow;
    EditText edit;
    Button btn_ok,btn_cancel;

    private Context myContext;

    private RelativeLayout showPeopleRelativeLayout;

    private TextView name;
    private TextView age;
    private TextView info;
    private ImageView portrait;
    private TextView produce;
    private TextView addFriendButton;
    private TextView sendMessageButton;
    private PeopleItem thePeopleItem;

    final private DisplayImageOptions options = getSimpleOptions();
    final private ImageLoader imageLoader = ImageLoader.getInstance();
    private ImageLoadingListener animateFirstListener =
            new AnimateFirstDisplayListener();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_show_people);
        myContext = this;
        showPeopleRelativeLayout = (RelativeLayout) findViewById(R.id.show_people_relative_layout);
        portrait = (ImageView)findViewById(R.id.show_user_portrait);
        name =(TextView)findViewById(R.id.show_user_name);
        age = (TextView)findViewById(R.id.show_user_age);
        info = (TextView)findViewById(R.id.show_user_info);
        produce = (TextView)findViewById(R.id.show_user_signature);
        addFriendButton = (TextView)findViewById(R.id.show_user_require);
        sendMessageButton = (TextView)findViewById(R.id.show_user_send_message);
        /**
         * 获取这个人的信息thePeopleItem
         */
        thePeopleItem = new PeopleItem();
        Intent intent = getIntent();
        thePeopleItem.setName(intent.getStringExtra("name"));
        thePeopleItem.setArea(intent.getStringExtra("area"));
        thePeopleItem.setSex(intent.getStringExtra("sex"));
        thePeopleItem.setAge(intent.getIntExtra("age",0));
        thePeopleItem.setImage(intent.getStringExtra("image"));
        thePeopleItem.setProduce(intent.getStringExtra("produce"));

        addFriendButton.setOnClickListener(this);
        sendMessageButton.setOnClickListener(this);
        initView();
    }


    private void initView(){
        imageLoader.displayImage(thePeopleItem.getImage()
                ,portrait, options,animateFirstListener);
        name.setText(thePeopleItem.getName());
        if(("female").equals(thePeopleItem.getSex()))
            age.setText("♀"+thePeopleItem.getAge());
        else
            age.setText("♂"+thePeopleItem.getAge());
        info.setText(thePeopleItem.getArea());
        produce.setText("  "+thePeopleItem.getProduce());
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.show_user_require:
                String name = PreferencesUtil.getSharedPreStr(myContext,"username");
                String toUser = thePeopleItem.getName();
                //为空
                if(TextUtils.isEmpty(toUser))
                    return;
                //添加自己
                if(toUser.equals(name)){
                    ToastUtil.showShortToast(myContext,"不能添加自己为好友");
                    return;
                }
                //已经是好友
                if(XMPPUtil.isFriend(MyApplication.xmppConnection , toUser)){
                    ToastUtil.showShortToast(myContext,"你们已经是好友了");
                    return;
                }
                showPopupWindow(showPeopleRelativeLayout,toUser);
                break;
            case R.id.show_user_send_message:
                 toUser = thePeopleItem.getName();
                List<Conversation> conversationList = MyApplication.getMyApplication().getConversationList();
                for(int position = 0;position < conversationList.size();position++){
                    if(conversationList.get(position).getName().equals(toUser)){
                        Intent intent = new Intent(myContext,ShowConversationActivity.class);
                        intent.putExtra("Name", conversationList.get(position).getName());
                        intent.putExtra("Position",position);
                        startActivity(intent);
                        return;
                    }
                }
                Conversation conversation = new Conversation();
                conversation.setName(toUser);
                conversationList.add(0,conversation);
                Intent intent = new Intent(myContext,ShowConversationActivity.class);
                intent.putExtra("Name", toUser);
                intent.putExtra("Position",0);
                startActivity(intent);

                break;
        }
    }

    //显示弹窗
    @SuppressWarnings("deprecation")
    private void showPopupWindow(View parent,final String toUser){
        if (popWindow == null) {
            View view = getLayoutInflater().inflate(R.layout.pop_edit,null);
            popWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,true);
            initPop(view,toUser);
        }
        popWindow.setAnimationStyle(android.R.style.Animation_InputMethod);
        popWindow.setFocusable(true);
        popWindow.setOutsideTouchable(true);
        popWindow.setBackgroundDrawable(new BitmapDrawable());
        popWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
    }

    public void initPop(View view,final String toUser){
        edit=(EditText) view.findViewById(R.id.edit);
        btn_ok = (Button) view.findViewById(R.id.btn_ok);//确定
        btn_cancel= (Button) view.findViewById(R.id.btn_cancel);//取消
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(TextUtils.isEmpty(edit.getText().toString())){
                    ToastUtil.showShortToast(myContext,"验证信息为空");
                    return;
                }
                XMPPUtil.applyForFriend(MyApplication.xmppConnection,toUser,edit.getText().toString());
                popWindow.dismiss();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                popWindow.dismiss();
            }
        });
    }




    private DisplayImageOptions getSimpleOptions() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_portrait) //设置图片在下载期间显示的图片
                .showImageForEmptyUri(R.drawable.default_portrait)//设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.default_portrait)  //设置图片加载/解码过程中错误时候显示的图片
                .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                .cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)//设置图片以如何的编码方式显示
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型
                .build();//构建完成
        return options;
    }

    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                // 是否第一次显示
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    // 图片淡入效果
                    FadeInBitmapDisplayer.animate(imageView, 200);
                    displayedImages.add(imageUri);
                }
            }
        }
    }

}
