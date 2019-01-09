package com.example.wtr.makefriends.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.wtr.makefriends.R;
import com.example.wtr.makefriends.application.MyApplication;
import com.example.wtr.makefriends.bean.Conversation;
import com.example.wtr.makefriends.bean.MessageItem;
import com.example.wtr.makefriends.bean.PeopleItem;
import com.example.wtr.makefriends.util.Const;
import com.example.wtr.makefriends.util.ToastUtil;
import com.example.wtr.makefriends.util.XMPPUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.example.wtr.makefriends.util.Const.IP;


public class ShowConversationActivity extends Activity implements View.OnClickListener {
    public static final int TAKE_PHOTO = 1;
    public static final int CROP_PHOTO = 2;
    public static final int GET_IMAGE = 3;
    public static final int CLICK_RECORD = 1;
    public static final int CLICK_STOP = 2;
    public static final int CLICK_PLAY = 3;
    private int soundClickStatus = CLICK_RECORD;
    private LinearLayout chatSoundContainer;
    private LinearLayout linearLayoutRecord;
    private Button soundRecordReturn;
    private ImageView soundClick;
    private LinearLayout chatAddContainer;
    private TextView soundRecordCancel;
    private TextView soundRecordSend;
    private TextView soundTiming;
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

    private MediaRecorder recorder;
    private File audioFile;
    private Context myContext;

    private int count = 0;
    private int time;

    @SuppressLint("HandlerLeak")
    class MyHandle extends Handler {
        public ImageView ivSound;
        public boolean isLeft;
        MyHandle(ImageView ivSound, boolean isLeft){
            super();
            this.ivSound = ivSound;
            this.isLeft = isLeft;
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 此处可以更新UI
            Bundle bundle = msg.getData();
            int st = bundle.getInt("status");
            turn(st, ivSound, isLeft);

        }
    }

    class MyThread implements Runnable{
        public MyHandle myHandle;
        public int time;
        MyThread(MyHandle myHandle, int time){
            super();
            this.myHandle = myHandle;
            this.time = time;
        }
        @Override
        public void run() {
            int st = 0;
            while(time > 0){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
                st = st % 3;
                Message message = new Message();
                Bundle bundle = new Bundle();// 存放数据
                bundle.putInt("status",st);
                message.setData(bundle);
                myHandle.sendMessage(message);
                time--;
                st++;
            }

            Message message = new Message();
            Bundle bundle = new Bundle();// 存放数据
            bundle.putInt("status",3);
            message.setData(bundle);
            myHandle.sendMessage(message);
        }
    }

    private Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            count++;
            int minute = count / 60;
            int second = count - minute * 60;
            if(second >= 10)
                soundTiming.setText("" + minute + ":" + second);
            else
                soundTiming.setText("" + minute + ":0" + second);
            handler.postDelayed(this, 1000);
        }
    };

    Runnable runable2 = new Runnable() {
        @Override
        public void run() {
            if(count > 0){
                count--;
                int minute = count / 60;
                int second = count - minute * 60;
                if(second >= 10)
                    soundTiming.setText("" + minute + ":" + second);
                else
                    soundTiming.setText("" + minute + ":0" + second);
                handler.postDelayed(this, 1000);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_show_conversation);

        myContext = this;
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

        chatSoundContainer = (LinearLayout) findViewById(R.id.chat_sound_container);
        soundRecordReturn = (Button) findViewById(R.id.sound_record_return);
        soundClick = (ImageView) findViewById(R.id.sound_click);
        soundRecordCancel = (TextView) findViewById(R.id.sound_record_cancel);
        soundRecordSend = (TextView) findViewById(R.id.sound_record_send);
        soundTiming = (TextView) findViewById(R.id.sound_timing);
        linearLayoutRecord = (LinearLayout) findViewById(R.id.linear_layout_record);

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
        soundRecordSend.setOnClickListener(this);
        soundRecordCancel.setOnClickListener(this);
        soundRecordReturn.setOnClickListener(this);
        soundClick.setOnClickListener(this);

        conversationListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                if(arg1.getAction()==MotionEvent.ACTION_DOWN){
                    if(chatAddContainer.getVisibility()==View.VISIBLE){
                        chatAddContainer.setVisibility(View.GONE);
                    }

                    if(chatSoundContainer.getVisibility()==View.VISIBLE){
                        chatSoundContainer.setVisibility(View.GONE);
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
                //点击语音，出现录音界面
                chatAddContainer.setVisibility(View.GONE);
                chatSoundContainer.setVisibility(View.VISIBLE);
                soundClickStatus = CLICK_RECORD;
                soundClick.setImageResource(R.drawable.click_to_record);
                soundRecordReturn.setVisibility(View.VISIBLE);
                linearLayoutRecord.setVisibility(View.GONE);
                soundTiming.setText("点击录音");
                break;
            case R.id.sound_record_return:
                chatSoundContainer.setVisibility(View.GONE);
                chatAddContainer.setVisibility(View.VISIBLE);
                break;
            case R.id.sound_click:
                //点击录音
                if(soundClickStatus == CLICK_RECORD){
                    soundClick.setImageResource(R.drawable.click_to_stop);
                    soundRecordReturn.setVisibility(View.GONE);
                    soundTiming.setText("0:00");
                    soundClickStatus = CLICK_STOP;
                    count = time = 0;
                    startAudio();
                    handler.postDelayed(runnable, 1000);
                }
                else if(soundClickStatus == CLICK_STOP){
                    time = count;
                    soundClick.setImageResource(R.drawable.click_to_play);
                    linearLayoutRecord.setVisibility(View.VISIBLE);
                    soundClickStatus = CLICK_PLAY;
                    handler.removeCallbacks(runnable);
                    stopAudio();
                }else{
                    handler.postDelayed(runable2,1000);
                    playAudio(audioFile.getAbsolutePath());
                    count = time;
                }
                break;
            case R.id.sound_record_cancel:
                soundClickStatus = CLICK_RECORD;
                soundClick.setImageResource(R.drawable.click_to_record);
                soundRecordReturn.setVisibility(View.VISIBLE);
                linearLayoutRecord.setVisibility(View.GONE);
                soundTiming.setText("点击录音");
                break;
            case R.id.sound_record_send:
                soundClickStatus = CLICK_RECORD;
                soundClick.setImageResource(R.drawable.click_to_record);
                soundRecordReturn.setVisibility(View.VISIBLE);
                linearLayoutRecord.setVisibility(View.GONE);
                soundTiming.setText("点击录音");

                sendAudio(audioFile);
                break;
            case R.id.show_conversation_send:
                String inputString = inputText.getText().toString();
                inputText.setText("");
                MessageItem newMessage = new MessageItem();
                newMessage.setText(inputString);
                newMessage.setUsername(user.getName());
                //如果是单聊
                if(!isGroupConversation){
                    //发送者卍是否群聊卍消息类型卍消息内容卍发送时间卍群名卍语音时长
                    final String message = user.getName() + Const.SPLIT + "false" + Const.SPLIT +
                            "1" + Const.SPLIT + inputString + Const.SPLIT + getTime() +  Const.SPLIT + "null" + Const.SPLIT + "0";
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            XMPPUtil.sendMessage(MyApplication.xmppConnection, message, conversationName);
                        }
                    }).start();
                }
                else{    //如果是群聊
                    //发送者卍是否群聊卍消息类型卍消息内容卍发送时间卍群名卍语音时长
                    final String message = user.getName() + Const.SPLIT + "true" + Const.SPLIT +
                            "1" + Const.SPLIT + inputString + Const.SPLIT + getTime() +  Const.SPLIT + groupName + Const.SPLIT + "0";
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
            public LinearLayout soundLinearLayout;
            public TextView soundTiming;
            public ImageView ivSound;
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
                        holder.soundLinearLayout = (LinearLayout) v.findViewById(R.id.word_right_sound);
                        holder.soundTiming = (TextView) v.findViewById(R.id.word_right_sound_timing);
                        holder.ivSound = (ImageView) v.findViewById(R.id.iv_word_right_sound);
                        break;
                    case OTHER:
                        v = getLayoutInflater().inflate(R.layout.word_left, parent, false);
                        holder.portrait = (ImageView) v.findViewById(R.id.word_left_portrait);
                        holder.name = (TextView) v.findViewById(R.id.word_left_name);
                        holder.text = (TextView) v.findViewById(R.id.word_left_text);
                        holder.picture = (ImageView) v.findViewById(R.id.word_left_picture);
                        holder.soundLinearLayout = (LinearLayout) v.findViewById(R.id.word_left_sound);
                        holder.soundTiming = (TextView) v.findViewById(R.id.word_left_sound_timing);
                        holder.ivSound = (ImageView) v.findViewById(R.id.iv_word_left_sound);
                        break;
                }

                v.setTag(holder);// 给View添加一个格外的数据
            } else {
                holder = (ViewHolder) v.getTag(); // 把数据取出来
            }
            holder.name.setText(wordList.get(position).getUsername());
            holder.text.setText(wordList.get(position).getText());
            int minute = wordList.get(position).getSoundTime() / 60;
            int second = wordList.get(position).getSoundTime() - minute * 60;
            if(minute != 0)
                holder.soundTiming.setText("" + minute + "'" + second + "''");
            else
                holder.soundTiming.setText("" + second + "''");

            if(wordList.get(position).getBitmap() != null){
                holder.soundLinearLayout.setVisibility(View.GONE);
                holder.picture.setVisibility(View.VISIBLE);
                holder.picture.setImageBitmap(wordList.get(position).getBitmap());
            }
            else if(wordList.get(position).getSoundPath() != null){
                holder.picture.setVisibility(View.GONE);
                holder.soundLinearLayout.setVisibility(View.VISIBLE);
            }
            else{
                holder.picture.setVisibility(View.GONE);
                holder.soundLinearLayout.setVisibility(View.GONE);
            }


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

                    holder.soundLinearLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MyHandle myHandle = new MyHandle(holder.ivSound, false);
                            MyThread thread = new MyThread(myHandle,wordList.get(position).getSoundTime());
                            new Thread(thread).start();
                            playAudio(wordList.get(position).getSoundPath());
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
                            intent.putExtra("area",peopleItem.getLocation());
                            intent.putExtra("sex",peopleItem.getSex());
                            intent.putExtra("age",peopleItem.getAge());
                            intent.putExtra("image",peopleItem.getImage());
                            intent.putExtra("produce",peopleItem.getIntroduce());
                            startActivity(intent);

                        }
                    });

                    holder.soundLinearLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MyHandle myHandle = new MyHandle(holder.ivSound, true);
                            MyThread thread = new MyThread(myHandle,wordList.get(position).getSoundTime());
                            new Thread(thread).start();
                            playAudio(wordList.get(position).getSoundPath());

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

    private void sendPicture(final Bitmap bitmap){
        MessageItem newMessage = new MessageItem();
        newMessage.setText("");
        newMessage.setUsername(user.getName());
        newMessage.setBitmap(bitmap);
        //上传图片到服务器
        String url="http://" + IP + "/Communicate/MyServlet";
        RequestQueue queues = Volley.newRequestQueue(this.getApplicationContext());// Volley框架必用，实例化请求队列
        StringRequest request = new StringRequest(Request.Method.POST, url, // StringRequest请求
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String arg0) {// 成功得到响应数据
                        try{
                            JSONObject result_jo = new JSONObject(arg0);
                            Boolean result = result_jo.getBoolean("result");
                            if(result){
                                String imageUrl = result_jo.getString("imageUrl");// 取出图片的url值
                                //如果是单聊
                                if(!isGroupConversation){
                                    //发送者卍是否群聊卍消息类型卍消息内容卍发送时间卍群名卍语音时长
                                    final String message = user.getName() + Const.SPLIT + "false" + Const.SPLIT +
                                            "2" + Const.SPLIT + imageUrl + Const.SPLIT + getTime() +  Const.SPLIT + "null" + Const.SPLIT + "0";
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            XMPPUtil.sendMessage(MyApplication.xmppConnection, message, conversationName);
                                        }
                                    }).start();
                                }
                                else {
                                    //如果是群聊
                                    //发送者卍是否群聊卍消息类型卍消息内容卍发送时间卍群名卍语音时长
                                    final String message = user.getName() + Const.SPLIT + "true" + Const.SPLIT +
                                            "2" + Const.SPLIT + imageUrl + Const.SPLIT + getTime() +  Const.SPLIT + groupName + Const.SPLIT + "0";
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            XMPPUtil.sendGroupMessage(MyApplication.xmppConnection,groupName,message);
                                        }
                                    }).start();
                                }
                            }else{
                                ToastUtil.showShortToast(myContext,"图片尺寸超出范围");
                            }


                        }catch(Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {// 未成功得到响应数据
            @Override
            public void onErrorResponse(VolleyError arg0) {
                ToastUtil.showShortToast(myContext,"图片尺寸超出范围");
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("order", "image");
                try {
                    String name = URLEncoder.encode(user.getName(), "utf-8");
                    params.put("username",name);
                } catch (UnsupportedEncodingException e2) {
                    e2.printStackTrace();
                }
                String image = bitmapToBase64(bitmap);
                Log.d("wtr",image);
                params.put("image", image);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("enctype", "multipart/form-data");
                return headers;
            }
        };
        request.setTag("volleyGet");// 设置请求标签Tag
        queues.add(request);// 将请求加入队列queue中处理


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

    //bitmap转为Base64
    public static String bitmapToBase64(Bitmap bitmap) {

        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    //文件转base64
    public static String fileToBase64(File file) {
        String base64 = null;
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            byte[] bytes = new byte[in.available()];
            int length = in.read(bytes);
            base64 = Base64.encodeToString(bytes, 0, length, Base64.DEFAULT);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return base64;
    }

    //发送音频
    private void sendAudio(File file){
        final String audio = fileToBase64(file);
        MessageItem soundMessage = new MessageItem();
        soundMessage.setSoundPath(audioFile.getAbsolutePath());
        soundMessage.setText("");
        soundMessage.setUsername(user.getName());
        soundMessage.setSoundTime(time);

        //上传语音到服务器
        String url="http://" + IP + "/Communicate/MyServlet";
        RequestQueue queues = Volley.newRequestQueue(this.getApplicationContext());// Volley框架必用，实例化请求队列
        StringRequest request = new StringRequest(Request.Method.POST, url, // StringRequest请求
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String arg0) {// 成功得到响应数据
                        try{
                            JSONObject result_jo = new JSONObject(arg0);
                            Boolean result = result_jo.getBoolean("result");
                            if(result){
                                String audioUrl = result_jo.getString("audioUrl");// 取出图片的url值
                                //如果是单聊
                                if(!isGroupConversation){
                                    //发送者卍是否群聊卍消息类型卍消息内容卍发送时间卍群名卍语音时长
                                    final String message = user.getName() + Const.SPLIT + "false" + Const.SPLIT +
                                            "3" + Const.SPLIT + audioUrl + Const.SPLIT + getTime() +  Const.SPLIT + "null" + Const.SPLIT + time;
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            XMPPUtil.sendMessage(MyApplication.xmppConnection, message, conversationName);
                                        }
                                    }).start();
                                }
                                else {
                                    //如果是群聊
                                    //发送者卍是否群聊卍消息类型卍消息内容卍发送时间卍群名卍语音时长
                                    final String message = user.getName() + Const.SPLIT + "true" + Const.SPLIT +
                                            "3" + Const.SPLIT + audioUrl + Const.SPLIT + getTime() +  Const.SPLIT + groupName + Const.SPLIT + time;
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            XMPPUtil.sendGroupMessage(MyApplication.xmppConnection,groupName,message);
                                        }
                                    }).start();
                                }
                            }else{

                            }


                        }catch(Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {// 未成功得到响应数据
            @Override
            public void onErrorResponse(VolleyError arg0) {

            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("order", "audio");
                try {
                    String name = URLEncoder.encode(user.getName(), "utf-8");
                    params.put("username",name);
                } catch (UnsupportedEncodingException e2) {
                    e2.printStackTrace();
                }
                params.put("audio", audio);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("enctype", "multipart/form-data");
                return headers;
            }
        };
        request.setTag("volleyGet");// 设置请求标签Tag
        queues.add(request);// 将请求加入队列queue中处理


        wordList.add(soundMessage);
        Conversation soundConversation = conversationList.remove(thePosition);
        soundConversation.setLastMessage("[语音]");
        soundConversation.setLastTime(getTime());
        soundConversation.setWordList(wordList);
        soundConversation.setNewMessageCount(0);
        conversationList.add(0,soundConversation);

        adapter.notifyDataSetChanged();
        conversationListView.smoothScrollToPosition(wordList.size()-1);
    }

    //开始录制音频
    private void startAudio(){
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);//从麦克风采集声音
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP); //内容输出格式
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        //输出到缓存目录，此处可以添加上传录音的功能，也可以存到其他位置
        audioFile = new File(getCacheDir(), "recorder"+ "_" + System.currentTimeMillis() + ".3gp");
        recorder.setOutputFile(audioFile.getAbsolutePath());
        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        recorder.start();
    }

    //结束录制
    private void stopAudio(){
        recorder.stop();
        recorder.release();
    }

    //播放录音
    private void playAudio(String path){
        MediaPlayer player = new MediaPlayer();
        try {
            player.setDataSource(path);
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void turn(int status, ImageView ivSound, Boolean isLeft){
        if(isLeft){
            switch (status){
                case 0:
                    ivSound.setImageResource(R.drawable.sound_left_1);
                    break;
                case 1:
                    ivSound.setImageResource(R.drawable.sound_left_2);
                    break;
                case 2:
                    ivSound.setImageResource(R.drawable.sound_left_3);
                    break;
                default:
                    ivSound.setImageResource(R.drawable.sound_left_3);
                    break;
            }
        }else{
            switch (status){
                case 0:
                    ivSound.setImageResource(R.drawable.sound_right_1);
                    break;
                case 1:
                    ivSound.setImageResource(R.drawable.sound_right_2);
                    break;
                case 2:
                    ivSound.setImageResource(R.drawable.sound_right_3);
                    break;
                default:
                    ivSound.setImageResource(R.drawable.sound_right_3);
                    break;
            }
        }

    }

}
