package com.example.wtr.im.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.wtr.im.R;
import com.example.wtr.im.application.MyApplication;
import com.example.wtr.im.bean.Conversation;
import com.example.wtr.im.bean.MessageItem;
import com.example.wtr.im.bean.PeopleItem;
import com.example.wtr.im.util.Const;
import com.example.wtr.im.util.XMPPUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by wtr on 2017/7/2.
 */

public class ShowConversationActivity extends Activity implements View.OnClickListener {
    public static final int TAKE_PHOTO = 1;
    public static final int CROP_PHOTO = 2;
    public static final int GET_IMAGE = 3;
    private LinearLayout chatAddContainer;
    private ImageView ivPicture;
    private ImageView ivCamera;
    private ImageView ivSound;
    private TextView title;
    private ImageView backButton;
    private ImageView chatAddButton;
    private TextView extra;
    private ListView conversationListView;
    private EditText inputText;
    private TextView sendButton;
    private ItemAdapter adapter;
    private PeopleItem user;
    private List<MessageItem> wordList;
    private String conversationName;
    private int thePosition;
    private Intent intent;
    private List<Conversation> conversationList = MyApplication.getMyApplication().getConversationList();
    private boolean isGroupConversation;
    private String groupName;

    private DisplayImageOptions options = getSimpleOptions();
    final private ImageLoader imageLoader = ImageLoader.getInstance();

    private Uri imageUri;
    private File outputImage = null;
    private Intent pictureIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_show_conversation);

        title = (TextView)findViewById(R.id.header_text);
        backButton = (ImageView)findViewById(R.id.header_portrait);
        extra = (TextView)findViewById(R.id.header_extra);
        inputText = (EditText)findViewById(R.id.show_conversation_input);
        sendButton = (TextView)findViewById(R.id.show_conversation_send);
        chatAddButton = (ImageView) findViewById(R.id.chat_add);
        chatAddContainer = (LinearLayout) findViewById(R.id.chat_add_container);
        ivPicture = (ImageView)findViewById(R.id.iv_pic);
        ivCamera = (ImageView)findViewById(R.id.iv_camera);
        ivSound = (ImageView)findViewById(R.id.iv_sound);
        user = MyApplication.getMyApplication().getUser();

        intent = getIntent();
        conversationName = intent.getStringExtra("Name");
        thePosition = intent.getIntExtra("Position",-1);
        isGroupConversation = intent.getBooleanExtra("IsGroupConversation", false);
        groupName = intent.getStringExtra("GroupName");

        if(isGroupConversation) {
            //群聊则把会话名设置成群名
            title.setText(groupName);
        }
        else {
            title.setText(conversationName);
        }


        backButton.setImageResource(R.drawable.arrow_left_white);
        extra.setText("");

        wordList = conversationList.get(thePosition).getWordList();
        conversationListView = (ListView)findViewById(R.id.show_conversation_List);
        adapter = new ItemAdapter();
        conversationListView.setAdapter(adapter);
        int position = getLastRead();
        if(position > 0)
            conversationListView.smoothScrollToPosition(position);
        backButton.setOnClickListener(this);
        extra.setOnClickListener(this);
        sendButton.setOnClickListener(this);
        chatAddButton.setOnClickListener(this);
        ivPicture.setOnClickListener(this);
        ivSound.setOnClickListener(this);
        ivCamera.setOnClickListener(this);

        conversationListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                if(arg1.getAction()==MotionEvent.ACTION_DOWN){
                    if(chatAddContainer.getVisibility()==View.VISIBLE){
                        chatAddContainer.setVisibility(View.GONE);
                    }
                }
                return false;
            }
        });


        intent = new Intent();
        intent.putExtra("data_return", "true");
        intent.putExtra("Position",thePosition);
        setResult(RESULT_OK,intent);
    }

    @SuppressLint("SimpleDateFormat")
    private String getTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH时mm分ss秒");//设置日期显示格式
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);// 将时间装换为设置好的格式
        return str;
    }


    private int getLastRead(){
        int position = wordList.size()-1;
        for(int i = 0;i < wordList.size();i++){

            if(!wordList.get(position).getIsRead() && position >= 0) {
                wordList.get(position).setIsRead(true);
                position--;
            }
            else
                break;
        }
        return position;
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.header_portrait:
                finish();
                break;
            case R.id.header_extra:
                break;
            case R.id.chat_add:
                if(chatAddContainer.getVisibility()==View.VISIBLE)
                    chatAddContainer.setVisibility(View.GONE);
                else
                    chatAddContainer.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_pic:
                //调用相册
                pictureIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pictureIntent, GET_IMAGE);
                break;
            case R.id.iv_camera:
                //调用相机拍照
                outputImage = new File(Environment.getExternalStorageDirectory(), "output_image.jpg");
                try{
                    if(outputImage.exists()){
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                }catch (IOException e){
                    e.printStackTrace();
                }
                imageUri = Uri.fromFile(outputImage);
                pictureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(pictureIntent, TAKE_PHOTO); //启动相机程序
                break;
            case R.id.iv_sound:
                break;
            case R.id.show_conversation_send:
                String inputString = inputText.getText().toString();
                inputText.setText("");
                MessageItem newMessage = new MessageItem();
                newMessage.setText(inputString);
                newMessage.setUsername(user.getName());
                //如果是单聊
                if(!isGroupConversation){
                    //发送者卍是否群聊卍消息类型卍消息内容卍发送时间卍群名
                    final String message = user.getName() + Const.SPLIT + "false" + Const.SPLIT +
                            "1" + Const.SPLIT + inputString + Const.SPLIT + getTime() +  Const.SPLIT + "null";
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            XMPPUtil.sendMessage(MyApplication.xmppConnection, message, conversationName);
                        }
                    }).start();

                }
                else{    //如果是群聊
                    //发送者卍是否群聊卍消息类型卍消息内容卍发送时间卍群名
                    final String message = user.getName() + Const.SPLIT + "true" + Const.SPLIT +
                            "1" + Const.SPLIT + inputString + Const.SPLIT + getTime() +  Const.SPLIT + groupName;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            XMPPUtil.sendGroupMessage(MyApplication.xmppConnection,groupName,message);
                        }
                    }).start();
                }
                wordList.add(newMessage);
                Conversation conversation = conversationList.remove(thePosition);

                conversation.setLastMessage(inputString);
                conversation.setLastTime(getTime());
                conversation.setWordList(wordList);
                conversation.setNewMessageCount(0);
                conversationList.add(0,conversation);

                adapter.notifyDataSetChanged();
                conversationListView.smoothScrollToPosition(wordList.size()-1);
                break;
        }
    }

    /**
     *
     * 自定义列表项适配器
     *
     */
    class ItemAdapter extends BaseAdapter {
        //图片第一次加载的监听器
        private ImageLoadingListener animateFirstListener =
                new AnimateFirstDisplayListener();
        final int OTHER = 0;
        final int ME = 1;
        private int talker;


        public ItemAdapter() {
        }
        private class ViewHolder {
            public ImageView portrait;
            public TextView name;
            public TextView text;
            public ImageView picture;
        }
        @Override
        public int getCount() {
            return wordList.size();
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            if(wordList.get(position).getUsername().equals(user.getName()))
                return 1;
            else
                return 0;
        }

        @Override
        public Object getItem(int position) {
            return wordList.get(position);
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            final ViewHolder holder;
            talker = getItemViewType(position);
            //通过convertView来判断是否已经加载过了，如果没有就加载
            if (convertView == null) {
                holder = new ViewHolder();
                switch(talker){
                    case ME:
                        v = getLayoutInflater().inflate(R.layout.word_right, parent, false);
                        holder.portrait = (ImageView) v.findViewById(R.id.word_right_portrait);
                        holder.name = (TextView) v.findViewById(R.id.word_right_name);
                        holder.text = (TextView) v.findViewById(R.id.word_right_text);
                        holder.picture = (ImageView) v.findViewById(R.id.word_right_picture);
                        break;
                    case OTHER:
                        v = getLayoutInflater().inflate(R.layout.word_left, parent, false);
                        holder.portrait = (ImageView) v.findViewById(R.id.word_left_portrait);
                        holder.name = (TextView) v.findViewById(R.id.word_left_name);
                        holder.text = (TextView) v.findViewById(R.id.word_left_text);
                        holder.picture = (ImageView) v.findViewById(R.id.word_left_picture);
                        break;
                }

                v.setTag(holder);// 给View添加一个格外的数据
            } else {
                holder = (ViewHolder) v.getTag(); // 把数据取出来
            }
            holder.name.setText(wordList.get(position).getUsername());
            holder.text.setText(wordList.get(position).getText());
            if(wordList.get(position).getBitmap() != null){
                holder.picture.setVisibility(View.VISIBLE);
                holder.picture.setImageBitmap(wordList.get(position).getBitmap());
            }
            else
                holder.picture.setVisibility(View.GONE);

            imageLoader.displayImage(wordList.get(position).getImage()
                    ,holder.portrait, options,animateFirstListener);
            switch(talker){
                case ME:
                    holder.portrait.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ShowConversationActivity.this,ShowUserActivity.class);
                            startActivity(intent);
                        }
                    });
                    break;
                case OTHER:
                    holder.portrait.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            /**
                             * 获取该用户信息并跳转
                             */
                            PeopleItem peopleItem = new PeopleItem();
                            peopleItem.setName(wordList.get(position).getUsername());
                            Intent intent = new Intent(ShowConversationActivity.this,ShowPeopleActivity.class);
                            intent.putExtra("name",peopleItem.getName());
                            intent.putExtra("area",peopleItem.getArea());
                            intent.putExtra("sex",peopleItem.getSex());
                            intent.putExtra("age",peopleItem.getAge());
                            intent.putExtra("image",peopleItem.getImage());
                            intent.putExtra("produce",peopleItem.getIntroduce());
                            startActivity(intent);

                        }
                    });
                    break;
            }
            return v;
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       switch (requestCode){
           case TAKE_PHOTO:
               if(resultCode == RESULT_OK){
                   Intent intent = new Intent("com.android.camera.action.CROP");
                   intent.setDataAndType(imageUri, "image/*");
                   intent.putExtra("scale", true);
                   intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                   startActivityForResult(intent, CROP_PHOTO);  //启动剪裁程序
               }
               break;
           case CROP_PHOTO:
               if(resultCode == RESULT_OK){
                   try{
                       Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                       sendPicture(bitmap);
                   }catch (FileNotFoundException e){
                       e.printStackTrace();
                   }
               }
               break;
           case GET_IMAGE:
               if(resultCode == RESULT_OK && data != null){
                       Uri selectedImage = data.getData();
                       String[] filePathColumns = {MediaStore.Images.Media.DATA};
                       Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
                       c.moveToFirst();
                       int columnIndex = c.getColumnIndex(filePathColumns[0]);
                       String imagePath = c.getString(columnIndex);
                       Bitmap bm = BitmapFactory.decodeFile(imagePath);
                       c.close();
                       sendPicture(bm);
                   }

           default:
               break;
       }
    }

    private void sendPicture(Bitmap bitmap){
        MessageItem newMessage = new MessageItem();
        newMessage.setText("");
        newMessage.setUsername(user.getName());
        newMessage.setBitmap(bitmap);
        //如果是单聊
        if(!isGroupConversation){
            //发送者卍是否群聊卍消息类型卍消息内容卍发送时间卍群名


        }
        else {

        }

        wordList.add(newMessage);
        Conversation conversation = conversationList.remove(thePosition);

        conversation.setLastMessage("[图片]");
        conversation.setLastTime(getTime());
        conversation.setWordList(wordList);
        conversation.setNewMessageCount(0);
        conversationList.add(0,conversation);

        adapter.notifyDataSetChanged();
        conversationListView.smoothScrollToPosition(wordList.size()-1);
    }

    /**
     * 监听返回键
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(chatAddContainer.getVisibility()==View.VISIBLE){
                chatAddContainer.setVisibility(View.GONE);
            }else{
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
