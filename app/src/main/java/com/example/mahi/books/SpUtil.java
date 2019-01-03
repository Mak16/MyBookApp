package com.example.mahi.books;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by MAHI on 25-06-2018.
 */

public class SpUtil {

    public static int position=0;
    public static final int MAX_SEARCH=5;
    public static boolean full = false;

    public static String sharedPefName="LatestSearch";
    private SpUtil(){}

    public static SharedPreferences getSpObject(Context context)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPefName,Context.MODE_PRIVATE);
        return sharedPreferences;
    }

    public static void putStringInSp(Context context,String key,String value) {
        getSpObject(context).edit().putString(key,value).apply();
    }

    public static void putIntInSp(Context context,String key,int value){
        getSpObject(context).edit().putInt(key,value).apply();
    }

    public static String getStringFromSp(Context context,String key){
        return getSpObject(context).getString(key,"");
    }

    public static int getIntFromSp(Context context,String key){
        return getSpObject(context).getInt(key,0);
    }
}
