package com.example.wtr.makefriends.activity;

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

import com.example.wtr.makefriends.R;
import com.example.wtr.makefriends.bean.PeopleItem;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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
        peopleItemLists = (List<PeopleItem>)getIntent().getSerializableExtra("list");

        title.setText(getIntent().getStringExtra("title"));
        backButton.setImageResource(R.drawable.arrow_left_white);
        extra.setText("");



        newFriendList = (ListView)findViewById(R.id.find_new_friend_list_view);
        adapter = new ItemAdapter();
        newFriendList.setAdapter(adapter);
        newFriendList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent,View v,int position,long id){
                PeopleItem peopleItem = peopleItemLists.get(position);
                Intent intent = new Intent(FindNewFriendsActivity.this, ShowPeopleActivity.class);
                intent.putExtra("name",peopleItem.getName());
                intent.putExtra("realName", peopleItem.getRealName());
                intent.putExtra("occupation", peopleItem.getOccupation());
                intent.putExtra("nativePlace", peopleItem.getNativePlace());
                intent.putExtra("email", peopleItem.getEmail());
                intent.putExtra("contactWay", peopleItem.getContactWay());
                intent.putExtra("bloodyType", peopleItem.getBloodyType());
                intent.putExtra("education", peopleItem.getEducation());
                intent.putExtra("constellation", peopleItem.getConstellation());
                intent.putExtra("location",peopleItem.getLocation());
                intent.putExtra("sex",peopleItem.getSex());
                intent.putExtra("age",peopleItem.getAge());
                intent.putExtra("image",peopleItem.getImage());
                intent.putExtra("introduce",peopleItem.getIntroduce());
                startActivity(intent);
            }
        });

        backButton.setOnClickListener(this);

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
            public TextView location;
            public TextView realName;
            public TextView occupation;
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
                holder.location = (TextView) v.findViewById(R.id.people_location);
                holder.realName = (TextView) v.findViewById(R.id.people_real_name);
                holder.occupation = (TextView) v.findViewById(R.id.people_occupation);
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
            holder.location.setText(peopleItemLists.get(position).getLocation());
            holder.realName.setText(peopleItemLists.get(position).getRealName());
            holder.occupation.setText(peopleItemLists.get(position).getOccupation());
            imageLoader.displayImage(peopleItemLists.get(position).getImage()
                    ,holder.portrait, options,animateFirstListener);
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