package com.example.wtr.im.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by wtr on 2017/7/2.
 */

public class ShowConversationActivity extends Activity implements View.OnClickListener {
    private TextView title;
    private ImageView backButton;
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


    final private DisplayImageOptions options = getSimpleOptions();
    final private ImageLoader imageLoader = ImageLoader.getInstance();



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
        user = MyApplication.getMyApplication().getUser();

        intent = getIntent();
        conversationName = intent.getStringExtra("Name");
        thePosition = intent.getIntExtra("Position",-1);

        title.setText(conversationName);
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

            case R.id.show_conversation_send:
                String inputString = inputText.getText().toString();
                inputText.setText("");
                MessageItem newMessage = new MessageItem();
                newMessage.setText(inputString);
                newMessage.setUsername(user.getName());
                //发送者卍消息内容卍发送时间
                final String message = user.getName() + Const.SPLIT +inputString + Const.SPLIT + getTime();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        XMPPUtil.sendMessage(MyApplication.xmppConnection, message, conversationName);
                    }
                }).start();
                wordList.add(newMessage);
                Conversation conversation = conversationList.remove(thePosition);

                conversation.setLastMessage(inputString);
                conversation.setLastTime(getTime());
                conversation.setWordList(wordList);
                conversation.setNewMessageCount(0);
                conversationList.add(0,conversation);

                adapter.notifyDataSetChanged();
                conversationListView.smoothScrollToPosition(wordList.size()-1);
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
                        break;
                    case OTHER:
                        v = getLayoutInflater().inflate(R.layout.word_left, parent, false);
                        holder.portrait = (ImageView) v.findViewById(R.id.word_left_portrait);
                        holder.name = (TextView) v.findViewById(R.id.word_left_name);
                        holder.text = (TextView) v.findViewById(R.id.word_left_text);
                        break;
                }

                v.setTag(holder);// 给View添加一个格外的数据
            } else {
                holder = (ViewHolder) v.getTag(); // 把数据取出来
            }
            holder.name.setText(wordList.get(position).getUsername());
            holder.text.setText(wordList.get(position).getText());
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
                            intent.putExtra("produce",peopleItem.getProduce());
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

}
