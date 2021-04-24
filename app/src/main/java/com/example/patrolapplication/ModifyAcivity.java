package com.example.patrolapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.patrolapplication.network.DataController;
import com.example.patrolapplication.network.dao.UserDao;
import com.example.patrolapplication.utils.LocalDataController;
import com.example.patrolapplication.utils.SyncData;

import java.util.HashMap;
import java.util.Map;

public class ModifyAcivity extends Activity {

    private EditText username_edit, password_origin_edit, password_new_edit, password_new_again_edit;
    private String userName, userPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);
        username_edit = findViewById(R.id.username_modify_edit);
        password_origin_edit = findViewById(R.id.password_orign_edit);
        password_new_edit = findViewById(R.id.password_new_edit);
        password_new_again_edit = findViewById(R.id.password_new_again_edit);

        userName = LocalDataController.getUsername(this);
        userPassword = LocalDataController.getPassword(this);

        if(userName.length() == 0 || userPassword.length() == 0){
            System.out.println("## can not find local data");
        }else{
            username_edit.setText(userName);
        }
    }

    private void modifyJumpToMain(){
        finish();
    }

    public void onBackButtonClick(View v){
        modifyJumpToMain();
    }

    public void onConfirmButtonClick(View v){
        String origin = password_origin_edit.getText().toString();
        String current = password_new_edit.getText().toString();
        String again = password_new_again_edit.getText().toString();
        // 初步合法性判断
        String msg = "";
        if(origin.length() == 0 || current.length() == 0 || again.length() == 0)
            msg = "输入不能有空";
        else if(!current.equals(again))
            msg = "两次密码输入不一致";
        else if(!userPassword.equals(origin))
            msg = "原密码错误";
        // 远程数据请求
        if(msg.length() > 0)
            Toast.makeText(getApplicationContext(), "msg", Toast.LENGTH_SHORT).show();
        else{
            Map<String, Object> map = new HashMap<>();
            map.put("name", userName);
            map.put("password", current);
            DataController.operate(map, new UserDao() {
                @Override
                public void userOperate(Map<String, Object> map) {
                    String msg = (String)map.get("msg");
                    int state = (int)map.get("state");
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    if (state == SyncData.STATE_OK)
                        modifyJumpToMain();
                }

                @Override
                public void timeout() {
                    String msg = "请求超时，请检查与服务器的链接";
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                }
            }, DataController.MODIFY);
        }
    }
}
