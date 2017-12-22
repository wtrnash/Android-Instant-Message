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
import com.example.wtr.im.bean.GroupItem;
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

import static com.example.wtr.im.util.PreferencesUtil.getSharedPreStr;

/**
 * Created by wtr on 2017/12/21.
 */

public class ShowGroupActivity extends Activity implements View.OnClickListener{
    PopupWindow popWindow;
    EditText edit_password;
    Button btn_ok,btn_cancel;

    private Context myContext;

    private RelativeLayout showGroupRelativeLayout;

    private TextView name;
    private TextView introduce;
    private ImageView portrait;
    private TextView applyEnterGroup;
    private GroupItem theGroupItem;

    final private DisplayImageOptions options = getSimpleOptions();
    final private ImageLoader imageLoader = ImageLoader.getInstance();
    private ImageLoadingListener animateFirstListener =
            new AnimateFirstDisplayListener();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_show_group);

        myContext = this;
        showGroupRelativeLayout = (RelativeLayout) findViewById(R.id.show_group_relative_layout);
        portrait = (ImageView)findViewById(R.id.show_group_portrait);
        name =(TextView)findViewById(R.id.show_group_name);
        introduce = (TextView)findViewById(R.id.show_group_introduce);
        applyEnterGroup = (TextView)findViewById(R.id.apply_enter_group);

        /**
         * 获取这个人群的信息theGroupItem
         */
        theGroupItem = new GroupItem();
        Intent intent = getIntent();
        theGroupItem.setName(intent.getStringExtra("name"));
        theGroupItem.setImage(intent.getStringExtra("image"));
        theGroupItem.setIntroduce(intent.getStringExtra("introduce"));

        applyEnterGroup.setOnClickListener(this);
        initView();
    }
    private void initView(){
        imageLoader.displayImage(theGroupItem.getImage()
                ,portrait, options,animateFirstListener);
        name.setText(theGroupItem.getName());
        introduce.setText("  "+ theGroupItem.getIntroduce());
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.apply_enter_group:
                if(isEnterGroup())
                    ToastUtil.showShortToast(myContext, "已经加入该群");
                else
                    showPopupWindow(showGroupRelativeLayout, theGroupItem.getName());
                break;
        }
    }

    public boolean isEnterGroup(){
        List<GroupItem> groupItemList = MyApplication.getMyApplication().getGroupItemList();
        for(int i = 0; i < groupItemList.size(); i++){
            if(groupItemList.get(i).getName().equals(theGroupItem.getName()))
                return true;
        }
        return false;
    }
    //显示弹窗
    @SuppressWarnings("deprecation")
    private void showPopupWindow(View parent,final String groupName){
        if (popWindow == null) {
            View view = getLayoutInflater().inflate(R.layout.pop_join_group,null);
            popWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,true);
            initPop(view,groupName);
        }
        popWindow.setAnimationStyle(android.R.style.Animation_InputMethod);
        popWindow.setFocusable(true);
        popWindow.setOutsideTouchable(true);
        popWindow.setBackgroundDrawable(new BitmapDrawable());
        popWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
    }

    public void initPop(View view,final String groupName){
        edit_password = (EditText) view.findViewById(R.id.join_group_password);
        btn_ok = (Button) view.findViewById(R.id.join_group_btn_ok);//确定
        btn_cancel= (Button) view.findViewById(R.id.join_group_btn_cancel);//取消
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(TextUtils.isEmpty(edit_password.getText().toString())){
                    ToastUtil.showShortToast(myContext,"加群密码为空");
                    return;
                }
                String username = getSharedPreStr(myContext,"username");
                if(XMPPUtil.enterGroup(MyApplication.xmppConnection,username,groupName,edit_password.getText().toString())){
                    ToastUtil.showLongToast(myContext, "添加群 " + groupName + " 成功！");
                    List<GroupItem> groups= MyApplication.getMyApplication().getGroupItemList();
                    GroupItem groupItem = new GroupItem();
                    groupItem.setName(groupName);
                    groups.add(groupItem);
                }
                else {
                    ToastUtil.showShortToast(myContext, "添加群失败");
                }
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
                .showImageOnLoading(R.drawable.group_portrait) //设置图片在下载期间显示的图片
                .showImageForEmptyUri(R.drawable.group_portrait)//设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.group_portrait)  //设置图片加载/解码过程中错误时候显示的图片
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
