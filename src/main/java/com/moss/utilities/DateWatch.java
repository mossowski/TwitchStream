package com.moss.utilities;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateWatch {

    private static final ThreadLocal<SimpleDateFormat> dateFormat = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    public static String getCurrentDate() {
        Date date = new Date();
        String result = dateFormat.get().format(date);
        return result;
    }

}
