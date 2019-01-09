package com.example.wtr.im.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by wtr on 2017/6/29.
 */

//SharedPreferences 工具类
public class PreferencesUtil {
    public static String NAME = "im";  //存放的文件的名称

    //存储String类型
    public static void putSharedPre(Context myContext,String key,String value){
        SharedPreferences sp = (SharedPreferences) myContext.getSharedPreferences(NAME,0);
        sp.edit().putString(key,value).commit();
    }

    //获取String类型
    public static String getSharedPreStr(Context myContext,String key){
        SharedPreferences sp = (SharedPreferences) myContext.getSharedPreferences(NAME,0);
        String s = sp.getString(key,"");
        return s;
    }
}
