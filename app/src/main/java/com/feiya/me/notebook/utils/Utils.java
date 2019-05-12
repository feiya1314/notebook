package com.feiya.me.notebook.utils;

import android.content.Context;
import android.util.TypedValue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

/**
 * Created by feiya on 2016/9/30.
 */

public class Utils {
    public static String dateToString(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    public static Date stringToDate(String strDate) throws Exception {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = dateFormat.parse(strDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    public static int dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
    public static int sp2dp(Context context, float spValue){
        //float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return  px2dp(context,sp2px(context,spValue));
    }
    public static int px2dp(Context context, float pxValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pxValue, context.getResources().getDisplayMetrics());
    }
    public static int px2sp(Context context, float pxValue) {
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, pxValue, context.getResources().getDisplayMetrics());
    }


    public static boolean isStringEmpty(String str) {
        return str == null ||  str.isEmpty();
    }

    public static boolean isCollectionEmpty(Collection collection){
        return collection == null || collection.isEmpty();
    }

    public static boolean isArrayEmpty(Object[] arrays){
        return arrays == null || arrays.length == 0;
    }
}
