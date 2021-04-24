package com.example.patrolapplication.network.dao;

import java.util.Map;

public interface UserDao {
    void userOperate(Map<String, Object> map);
    void timeout();
}
