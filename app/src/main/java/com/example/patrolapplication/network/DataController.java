package com.example.patrolapplication.network;

import com.example.patrolapplication.network.controller.ConditionItemThread;
import com.example.patrolapplication.network.controller.LocationOperateThread;
import com.example.patrolapplication.network.controller.PatrolRecordThread;
import com.example.patrolapplication.network.controller.QueryLocationList;
import com.example.patrolapplication.network.controller.UniformGetThread;
import com.example.patrolapplication.network.controller.UniformThread;
import com.example.patrolapplication.network.controller.UserOperateThread;
import com.example.patrolapplication.network.dao.UserDao;

import java.util.Map;

public class DataController {
    public static final String LOGIN = "login";
    public static final String REGISTER = "register";
    public static final String MODIFY = "modify";

    public static final String tripRecord = "tripRecord";
    public static final String patrolRecord = "patrolRecord";
    public static final String conditionItem = "conditionItem";
    public static final String location = "location";
    public static final String abnormalRecord = "abnormalRecord";

    public static final String ADD = "add";
    public static final String DELETE = "delete";
    public static final String UPDATE = "update";
    public static final String QUERY = "query";
    public static final String queryList = "queryList";
    public static final String queryByInfo = "queryByInfo";

    public static void operate(Map<String, Object> map, UserDao userDao, String operation){
        new UserOperateThread(map, userDao, operation).start();
    }

    public static void locationOperate(Map<String, Object> map, UserDao userDao, String operation){
        new LocationOperateThread(map, userDao, operation).start();
    }

    public static void queryLocationList(UserDao userDao){
        new UniformGetThread(userDao, location, queryList).start();
    }

    public static void queryConditionItemList(UserDao userdao){
        new UniformGetThread(userdao, conditionItem, queryList).start();
    }

    public static void addPatrolRecord(Map<String, Object> map, UserDao userDao){
        new UniformThread(map, userDao, patrolRecord, ADD).start();
    }

    public static void addTripRecord(Map<String, Object> map, UserDao userDao){
        new UniformThread(map, userDao, tripRecord, ADD).start();
    }

    public static void queryTripRecord(Map<String, Object> map, UserDao userDao){
        new UniformThread(map, userDao, tripRecord, queryByInfo).start();
    }

    public static void queryPatrolRecord(Map<String, Object> map, UserDao userDao){
        new UniformThread(map, userDao, patrolRecord, queryByInfo).start();
    }

    public static void queryAbnormalRecord(Map<String, Object> map, UserDao userDao){
        new UniformThread(map, userDao, abnormalRecord, queryByInfo).start();
    }
}
