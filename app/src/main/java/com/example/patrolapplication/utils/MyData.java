package com.example.patrolapplication.utils;

import android.content.Context;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;

import java.util.HashMap;
import java.util.Map;

public class MyData {

    public static final String defaultLocId = "00000000";
    public static final String defaultLocName = "其他地点";
    public static final String otherItem = "其他";

    public static final String TIMEOUT = "请求超时，请检查与服务器的连接";
    public static void timeoutToast(Context context){
        Toast.makeText(context, TIMEOUT, Toast.LENGTH_SHORT).show();
    }
    public static void makeToast(Context context, String text){
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    /** 菜单 */
    // 菜单条目内容
    public static String[] menuList = {"巡更打卡", "事故上报", "记录查询"};

    /** 巡更打卡数据 */
    // 卡和地理位置绑定
//    public static final Map<String, LatLng> idLocMap = new HashMap<>();
//
//    static {
//        idLocMap.put("43538B3D", new LatLng(39.965881,116.363881));
//        idLocMap.put("2EF9903D", new LatLng(39.970705, 116.363666));
//        idLocMap.put("5C96903D", new LatLng(39.966828, 116.361914));
//        idLocMap.put("63B48C3D", new LatLng(39.967637, 116.366738));
//    }

    // 字节数组转16进制字符串
    public static String byteArrayToHexString(byte[] bytes){
        StringBuilder res = new StringBuilder();
        final char[] charMap = {
                '0', '1', '2', '3',
                '4', '5', '6', '7',
                '8', '9', 'A', 'B',
                'C', 'D', 'E', 'F'
        };
        for (byte bt : bytes) {
            res.append(charMap[(bt >> 4) & 0x0F]);
            res.append(charMap[bt & 0x0F]);
        }
        return res.toString();
    }
}
