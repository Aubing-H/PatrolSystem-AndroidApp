package com.example.patrolapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.patrolapplication.network.DataController;
import com.example.patrolapplication.network.dao.UserDao;
import com.example.patrolapplication.pojo.User;
import com.example.patrolapplication.utils.LocalDataController;
import com.example.patrolapplication.utils.MyData;
import com.example.patrolapplication.utils.SyncData;

import java.util.HashMap;
import java.util.Map;

import static android.view.inputmethod.EditorInfo.IME_ACTION_DONE;
import static android.view.inputmethod.EditorInfo.IME_ACTION_NEXT;

public class LoginActivity extends Activity {

    private Button loginBtn;
    private EditText userNameEdt;
    private EditText passwordEdit;
    private EditText passwordAgainEdit;
    private CheckBox adminCheckBox;
    private TextView switchText;
    private LinearLayout againLayout;
    private boolean isLogin = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBtn = findViewById(R.id.login_button);
        userNameEdt = findViewById(R.id.username_edt);
        passwordEdit = findViewById(R.id.password_edt);
        passwordAgainEdit = findViewById(R.id.password_again_edt);
        adminCheckBox = findViewById(R.id.admin_check_box);
        switchText = findViewById(R.id.model_switch);
        againLayout = findViewById(R.id.again_layout);

        switchText.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                modelSwitch();
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Btn clicked, islogin: " + isLogin);
                if(isLogin){
                    userLogin(userNameEdt.getText().toString(),
                            passwordEdit.getText().toString(), adminCheckBox.isChecked());
                }else{
                    userRegister(userNameEdt.getText().toString(),
                            passwordEdit.getText().toString(), passwordAgainEdit.getText().toString(),
                            adminCheckBox.isChecked());
                }
            }
        });
    }

    private void userRegister(String name, String psw, String pswAgain, boolean isAdmin){
        // 本地检测
        String msg = "";
        if(name.length() == 0 || psw.length() == 0 || pswAgain.length() == 0){
            msg = "输入不能为空";
        }else if(!psw.equals(pswAgain)){
            msg = "两次密码输入不一样";
        }else if(psw.length() < 4){
            msg = "输入密码不能少于4位";
        }else if(name.length() > 20 || psw.length() > 20){
            msg = "用户名或密码太长，不得超过20个字符";
        }
        if (msg.length() > 0)
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        else{
            // 远程请求
            // 将数据放入映射结构
            Map<String, Object> map = new HashMap<>();
            map.put("name", name);
            map.put("password", psw);
            map.put("type", isAdmin ? User.TYPE_ADMIN : User.TYPE_PATROL);
            // 编写接口实现
            DataController.operate(map, new UserDao() {
                @Override
                public void userOperate(Map<String, Object> rec) {
                    int state = (int)rec.get("state");
                    String msg = (String)rec.get("msg");
                    if(state == SyncData.STATE_OK){
                        modelSwitch();
                    }
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void timeout() {
                    Toast.makeText(getApplicationContext(), MyData.TIMEOUT,
                            Toast.LENGTH_SHORT).show();
                }
            }, DataController.REGISTER);
        }
    }

    private void userLogin(String name, String password, boolean isAdmin){
        // 本地合法性检查
        String msg = "";
        if(name == null || name.length() == 0)
            msg = "输入用户名不能为空";
        else if(password == null || password.length() == 0)
            msg = "输入密码不能为空";
        else if(name.length() > 20 || password.length() > 20)
            msg = "用户名或密码太长，不得超过20个字符";
        if(msg.length() > 0)
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        else{
            // 远程请求
            Map<String, Object> map = new HashMap<>();
            map.put("name", name);
            map.put("password", password);
            map.put("type", isAdmin ? User.TYPE_ADMIN : User.TYPE_PATROL);
            DataController.operate(map, new UserDao() {
                @Override
                public void userOperate(Map<String, Object> rec) {
                    int state = (int)rec.get("state");
                    String msg = (String)rec.get("msg");
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    // 跳转到主界面
                    if(state == SyncData.STATE_OK)
                        jumpToMain((User)rec.get("user"));
                }

                @Override
                public void timeout() {
                    Toast.makeText(getApplicationContext(), MyData.TIMEOUT, Toast.LENGTH_SHORT)
                            .show();
                }
            }, DataController.LOGIN);

        }
    }

    private void jumpToMain(User user){
        Intent intent = new Intent();
        if(user.getType() == User.TYPE_PATROL) {
            intent.setClass(LoginActivity.this, MainActivity.class);
            // 将数据传入本地
            LocalDataController.putUser(user.getName(), user.getPassword(), user.getId(), this);
        }else{
            intent.setClass(LoginActivity.this, AdminActivity.class);
        }
        startActivity(intent);
        finish(); // 结束自身生命周期
    }

    private void modelSwitch(){
        String login = getString(R.string.login);
        String register = getString(R.string.register);
        if(isLogin){
            // 登录状态 --> 注册状态
            isLogin = false;
            againLayout.setVisibility(View.VISIBLE);
            loginBtn.setText(register);
            switchText.setText(login);
            adminCheckBox.setText(getString(R.string.admin_register));
            passwordEdit.setImeOptions(IME_ACTION_NEXT);
            // 清空内容
            userNameEdt.setText("");
            passwordEdit.setText("");
        }else{
            // 注册状态 --> 登录状态
            isLogin = true;
            againLayout.setVisibility(View.GONE);
            loginBtn.setText(login);
            switchText.setText(register);
            passwordEdit.setImeOptions(IME_ACTION_DONE);
            adminCheckBox.setText(getString(R.string.admin_login));
            // 清空内容
            passwordEdit.setText("");
            passwordAgainEdit.setText("");
        }
    }
}
