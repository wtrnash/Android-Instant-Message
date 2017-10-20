package com.example.wtr.im.fragment;

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

import com.example.wtr.im.R;
import com.example.wtr.im.activity.ShowPeopleActivity;
import com.example.wtr.im.bean.PeopleItem;
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
 * Created by wtr on 2017/6/30.
 */

public class PeopleListFragment extends Fragment {

    private View view;
    private List<PeopleItem> peopleItemLists;
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
        peopleItemLists = new ArrayList<PeopleItem>();
        mListView = (ListView)view.findViewById(R.id.simpleListView);
        adapter = new ItemAdapter();
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id){
                PeopleItem peopleItem = peopleItemLists.get(position);
                Intent intent = new Intent(getActivity(),ShowPeopleActivity.class);
                intent.putExtra("name",peopleItem.getName());
                intent.putExtra("area",peopleItem.getArea());
                intent.putExtra("sex",peopleItem.getSex());
                intent.putExtra("age",peopleItem.getAge());
                intent.putExtra("image",peopleItem.getImage());
                intent.putExtra("produce",peopleItem.getProduce());
                startActivity(intent);

            }
        });
    }


    public void refreshData(List<PeopleItem> newPeopleItemLists){
        peopleItemLists = newPeopleItemLists;
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
                v = LayoutInflater.from(getActivity()).inflate(R.layout.people_item, parent, false);
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
                    +peopleItemLists.get(position).getProduce());
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
