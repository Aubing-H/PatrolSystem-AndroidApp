package com.example.patrolapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

// 管理本地数据
public class LocalDataController {
    private static final String TAG = "## LocalDataController ## ";

    private static final String keyUser = "user";
    private static final String keyUsername = "username";
    private static final String keyPassword = "password";
    private static final String keyUserId = "userId";

    private static String userId = "";

    public static void putUser(String username,String password, String userId, Context context){
        System.out.println(TAG + "putUser: username = [" + username +
                "], password = [" + password + "]");
        SharedPreferences sharedPreferences = context.getSharedPreferences(keyUser,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(keyUsername, username)
                .putString(keyPassword, password)
                .putString(keyUserId, userId)
                .apply();
        LocalDataController.userId = userId;
    }

    public static String getUsername(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(keyUser,
                Context.MODE_PRIVATE);
        String username  = sharedPreferences.getString(keyUsername, "");
        System.out.println(TAG + "getUsername: " + username );
        return username;
    }

    public static String getPassword(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(keyUser,
                Context.MODE_PRIVATE);
        String password = sharedPreferences.getString(keyPassword, "");
        System.out.println(TAG + "getPassword: " + password);
        return password;
    }

    public static String getUserId(){
//        SharedPreferences sharedPreferences = context.getSharedPreferences(keyUser,
//                Context.MODE_PRIVATE);
//        String userId = sharedPreferences.getString(keyUserId, "");
        return userId;
    }

    public static void clearUserData(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(keyUser, Context.MODE_PRIVATE);
        sharedPreferences.edit()
                .clear()
                .apply();
        System.out.println(TAG + "clearUserData()");
    }
}
