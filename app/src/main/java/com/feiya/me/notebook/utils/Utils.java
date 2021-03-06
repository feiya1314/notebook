package com.feiya.me.notebook.utils;

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
