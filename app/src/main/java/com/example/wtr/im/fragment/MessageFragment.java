package com.example.wtr.im.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wtr.im.R;
import com.example.wtr.im.activity.ShowConversationActivity;
import com.example.wtr.im.application.MyApplication;
import com.example.wtr.im.bean.Conversation;
import com.example.wtr.im.xlistview.XListView;
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
 * Created by wtr on 2017/6/30.
 */

public class MessageFragment  extends android.support.v4.app.Fragment implements XListView.IXListViewListener{
    private View view;
    final private DisplayImageOptions options = getSimpleOptions();
    final private ImageLoader imageLoader = ImageLoader.getInstance();
    private ItemAdapter adapter;
    private XListView mListView;
    private Handler mHandler;
    private List<Conversation> ConversationList = MyApplication.getMyApplication().getConversationList();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view= inflater.inflate(R.layout.frame_list, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView = (XListView)view.findViewById(R.id.xListView);
        adapter = new ItemAdapter();
        mListView.setAdapter(adapter);
        mListView.setXListViewListener(this);
        mHandler = new Handler();
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id){
                //点击消息，第1~n共n条
                if(0 < position && position <= ConversationList.size()){
                    Conversation conversation = ConversationList.get(position-1);
                    conversation.setNewMessageCount(0);
                    // 利用conservation跳转
                    Intent intent = new Intent(getActivity(),ShowConversationActivity.class);
                    intent.putExtra("Name", conversation.getName());
                    intent.putExtra("Position",position - 1);
                    startActivityForResult(intent, 1);
                    Log.d("wtr","onItemClick");
                }
            }
        });
    }

    public void insert(Conversation conversation,int index){//插入一项
        if(ConversationList != null){
            if(index < ConversationList.size())
                ConversationList.add(index, conversation);
            else
                ConversationList.add(conversation);
            adapter.notifyDataSetChanged();
        }
    }


    @SuppressWarnings("static-access")
    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        switch (requestCode){
            case 1:
                if(resultCode == getActivity().RESULT_OK){
                    String returndata = data.getStringExtra("data_return");
                    if(("true").equals(returndata)){
                            adapter.notifyDataSetChanged();
                    }
                }
                break;
            default:
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

        public ItemAdapter() {
        }
        private class ViewHolder {
            public ImageView portrait;
            public TextView name;
            public TextView lastMessage;
            public TextView lastTime;
            public TextView newMessageCount;
        }
        @Override
        public int getCount() {
            return ConversationList.size();
        }
        @Override
        public Object getItem(int position) {
            return ConversationList.get(position);
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            final ViewHolder holder;
            //通过convertView来判断是否已经加载过了，如果没有就加载
            if (convertView == null) {
                holder = new ViewHolder();
                v = LayoutInflater.from(getActivity()).inflate(R.layout.conversation_item, parent, false);
                holder.portrait = (ImageView) v.findViewById(R.id.conversation_portrait);
                holder.name = (TextView) v.findViewById(R.id.conversation_name);
                holder.lastMessage = (TextView) v.findViewById(R.id.conversation_lastmessage);
                holder.lastTime = (TextView) v.findViewById(R.id.conversation_lasttime);
                holder.newMessageCount = (TextView) v.findViewById(R.id.conversation_newmessage_count);
                v.setTag(holder);// 给View添加一个格外的数据
            } else {
                holder = (ViewHolder) v.getTag(); // 把数据取出来
            }
            holder.name.setText(ConversationList.get(position).getName());
            holder.lastMessage.setText(ConversationList.get(position).getLastMessage());
            holder.lastTime.setText(ConversationList.get(position).getLastTime());
            int count = ConversationList.get(position).getNewMessageCount();
            if(count>99){
                holder.newMessageCount.setText("99");
                holder.newMessageCount.setBackgroundResource(R.drawable.round_red);
            }
            else if(count<=0){
                holder.newMessageCount.setText("");
                holder.newMessageCount.setBackgroundResource(R.drawable.white);
            }
            else{
                holder.newMessageCount.setText(""+count);
                holder.newMessageCount.setBackgroundResource(R.drawable.round_red);
            }
            imageLoader.displayImage(ConversationList.get(position).getImage()
                    ,holder.portrait, options,animateFirstListener);
            return v;
        }
    }


    @SuppressLint("SimpleDateFormat")
    private void onLoad() {
        mListView.stopRefresh();
        //mListView.stopLoadMore();
       SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH时mm分ss秒");//设置日期显示格式
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);// 将时间装换为设置好的格式
        mListView.setRefreshTime(str);
    }

    @Override
    public void onRefresh() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //items.clear();
                //geneItems();刷新数据ConversationList
                //ConversationList.add(new Conversation());测试用
                adapter.notifyDataSetChanged();
                onLoad();
            }
        }, 2000);
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

    public void doRefresh(){
        adapter.notifyDataSetChanged();
    }
}
