package com.example.wtr.im.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by wtr on 2017/6/29.
 */

//Toast工具类
public class ToastUtil {
    public static void showShortToast(Context myContext,String text){
        Toast.makeText(myContext,text,Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(Context myContext,String text){
        Toast.makeText(myContext,text,Toast.LENGTH_LONG).show();
    }
}
