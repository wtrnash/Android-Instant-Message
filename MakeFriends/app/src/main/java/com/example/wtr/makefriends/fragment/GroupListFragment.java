package com.example.wtr.makefriends.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.wtr.makefriends.R;
import com.example.wtr.makefriends.activity.ShowGroupActivity;
import com.example.wtr.makefriends.bean.GroupItem;
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


public class GroupListFragment extends Fragment {

    private View view;
    private List<GroupItem> groItemsList;
    final private DisplayImageOptions options = getSimpleOptions();
    final private ImageLoader imageLoader = ImageLoader.getInstance();
    private ItemAdapter adapter;
    private ListView mListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view= inflater.inflate(R.layout.frame_simple_list, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        groItemsList = new ArrayList<GroupItem>();
        mListView = (ListView)view.findViewById(R.id.simpleListView);
        adapter = new ItemAdapter();
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id){
                GroupItem groupItem = groItemsList.get(position);
                //利用groupItem跳转
                Intent intent = new Intent(getActivity(),ShowGroupActivity.class);
                intent.putExtra("name",groupItem.getName());
                intent.putExtra("image",groupItem.getImage());
                intent.putExtra("introduce",groupItem.getIntroduce());
                startActivity(intent);

            }
        });
    }

    public void refreshData(List<GroupItem> newgroItemsList){
        groItemsList = newgroItemsList;
        adapter.notifyDataSetChanged();
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
            public TextView info;
        }
        @Override
        public int getCount() {
            return groItemsList.size();
        }
        @Override
        public Object getItem(int position) {
            return groItemsList.get(position);
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
                v = LayoutInflater.from(getActivity()).inflate(R.layout.group_item, parent, false);
                holder.portrait = (ImageView) v.findViewById(R.id.group_portrait);
                holder.name = (TextView) v.findViewById(R.id.group_name);
                holder.info = (TextView) v.findViewById(R.id.group_info);
                v.setTag(holder);// 给View添加一个格外的数据
            } else {
                holder = (ViewHolder) v.getTag(); // 把数据取出来
            }
            holder.name.setText(groItemsList.get(position).getName());
            holder.info.setText(groItemsList.get(position).getArea()+" "
                    +groItemsList.get(position).getIntroduce());
            imageLoader.displayImage(groItemsList.get(position).getImage()
                    ,holder.portrait, options,animateFirstListener);
            return v;
        }
    }

    private DisplayImageOptions getSimpleOptions() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.group_portrait) //设置图片在下载期间显示的图片
                .showImageForEmptyUri(R.drawable.group_portrait)//设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.group_portrait)  //设置图片加载/解码过程中错误时候显示的图片
                .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                .cacheOnDisc(true)//设置下载的图片是否缓存在SD卡中
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
