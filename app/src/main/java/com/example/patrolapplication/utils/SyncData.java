package com.example.patrolapplication.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.internal.connection.ConnectInterceptor;

public class SyncData {
    private static final String pattern = "yyyy-MM-dd HH:mm:ss";

    public static final int STATE_OK = 1;
    public static final int STATE_FAILED = 2;

    public static final int NORMAL = 1;
    public static final int ABNORMAL = 0;

    // mobile
    // public static final String ipAddress = "http://10.28.201.21:8080/";
    // portal
    // public static final String ipAddress = "http://10.128.201.248:8080/";
    public static final String ipAddress = "http://10.128.230.82:8080/";

    // 钟书阁
    // public static final String ipAddress = "http://172.16.14.107:8080/";
    // Sequoia
    // public static final String ipAddress = "http://192.168.43.117:8080/";

    private static final int TIMEOUT = 3;
    public static final OkHttpClient client = new OkHttpClient.Builder().
            connectTimeout(TIMEOUT, TimeUnit.SECONDS).
            readTimeout(TIMEOUT, TimeUnit.SECONDS).
            writeTimeout(TIMEOUT, TimeUnit.SECONDS)
            .build();

    public static String dateToString(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    public static String str2Str(String str){
        str = str.replace('T', ' ');
        return str.substring(0, 19);
    }

    public static Date stringToDate(String str) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.parse(str);
    }
}
