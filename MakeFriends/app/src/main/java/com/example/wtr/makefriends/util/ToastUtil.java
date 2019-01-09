package com.example.wtr.makefriends.util;

import android.content.Context;
import android.widget.Toast;

//Toast工具类
public class ToastUtil {
    public static void showShortToast(Context myContext,String text){
        Toast.makeText(myContext,text,Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(Context myContext,String text){
        Toast.makeText(myContext,text,Toast.LENGTH_LONG).show();
    }
}
