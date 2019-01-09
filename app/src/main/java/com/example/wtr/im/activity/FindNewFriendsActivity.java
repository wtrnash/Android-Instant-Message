package com.example.wtr.im.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.wtr.im.R;
import com.example.wtr.im.application.MyApplication;
import com.example.wtr.im.bean.PeopleItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by wtr on 2017/7/1.
 */

public class FindNewFriendsActivity extends Activity implements View.OnClickListener{
    private TextView title;
    private ImageView backButton;
    private TextView extra;
    private ListView newFriendList;
    private ItemAdapter adapter;
    private List<PeopleItem> peopleItemLists;

    final private DisplayImageOptions options = getSimpleOptions();
    final private ImageLoader imageLoader = ImageLoader.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_find_new_friend);

        title = (TextView)findViewById(R.id.header_text);
        backButton = (ImageView)findViewById(R.id.header_portrait);
        extra = (TextView)findViewById(R.id.header_extra);

        title.setText("推荐好友");
        backButton.setImageResource(R.drawable.arrow_left_white);
        extra.setText("");

        peopleItemLists = new ArrayList<PeopleItem>();

        newFriendList = (ListView)findViewById(R.id.find_new_friend_list_view);
        adapter = new ItemAdapter();
        newFriendList.setAdapter(adapter);
        newFriendList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent,View v,int position,long id){
                PeopleItem peopleItem = peopleItemLists.get(position);
                Intent intent = new Intent(FindNewFriendsActivity.this,ShowPeopleActivity.class);
                intent.putExtra("name",peopleItem.getName());
                intent.putExtra("area",peopleItem.getArea());
                intent.putExtra("sex",peopleItem.getSex());
                intent.putExtra("age",peopleItem.getAge());
                intent.putExtra("image",peopleItem.getImage());
                intent.putExtra("produce",peopleItem.getIntroduce());
                startActivity(intent);
            }
        });

        backButton.setOnClickListener(this);
        peopleItemLists = getrecommendedfriends
                (MyApplication.getMyApplication().getUser().getName());
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.header_portrait:
                finish();
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
            public TextView age;
            public TextView info;
        }
        @Override
        public int getCount() {
            return peopleItemLists.size();
        }
        @Override
        public Object getItem(int position) {
            return peopleItemLists.get(position);
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
                v = getLayoutInflater().inflate(R.layout.people_item, parent, false);
                holder.portrait = (ImageView) v.findViewById(R.id.pepple_portrait);
                holder.name = (TextView) v.findViewById(R.id.people_name);
                holder.age = (TextView) v.findViewById(R.id.people_age);
                holder.info = (TextView) v.findViewById(R.id.people_info);
                v.setTag(holder);// 给View添加一个格外的数据
            } else {
                holder = (ViewHolder) v.getTag(); // 把数据取出来
            }
            holder.name.setText(peopleItemLists.get(position).getName());
            String sex = peopleItemLists.get(position).getSex();
            if(("female").equals(sex))
                holder.age.setText("♀"+peopleItemLists.get(position).getAge());
            else
                holder.age.setText("♂"+peopleItemLists.get(position).getAge());
            holder.info.setText(peopleItemLists.get(position).getArea()+" "
                    +peopleItemLists.get(position).getIntroduce());
            imageLoader.displayImage(peopleItemLists.get(position).getImage()
                    ,holder.portrait, options,animateFirstListener);
            return v;
        }
    }


    private List<PeopleItem> getrecommendedfriends(String name){
        peopleItemLists = new ArrayList<PeopleItem>();
        String url="http://139.196.167.145/Communicate/MyServlet?Order=GetFriends&Name="+name;
        RequestQueue queues = Volley.newRequestQueue(this.getApplicationContext());// Volley框架必用，实例化请求队列
        StringRequest request = new StringRequest(Request.Method.GET, url, // StringRequest请求
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String arg0) {// 成功得到响应数据
                        try{
                            Gson gson = new Gson();
                            peopleItemLists = gson.fromJson(arg0, new TypeToken<List<PeopleItem>>(){}.getType());
                            adapter.notifyDataSetChanged();
                        }catch(Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {// 未成功得到响应数据
            @Override
            public void onErrorResponse(VolleyError arg0) {
            }
        });
        request.setTag("volleyGet");// 设置请求标签Tag
        queues.add(request);// 将请求加入队列queue中处理
        return peopleItemLists;
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