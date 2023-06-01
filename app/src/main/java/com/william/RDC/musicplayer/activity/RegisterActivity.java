package com.william.RDC.musicplayer.activity;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.william.RDC.musicplayer.R;
import com.william.RDC.musicplayer.database.DatabaseOperation;

public class RegisterActivity extends AppCompatActivity {
    EditText edtUsername, edtPassword;
    Button btnRegister;

    TextView txtLoginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnRegister = findViewById(R.id.btnRegister);
        txtLoginLink = findViewById(R.id.txtLoginLink);


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edtUsername.getText().toString();
                String password = edtPassword.getText().toString();
                DatabaseOperation dbOperation = DatabaseOperation.getInstance(RegisterActivity.this);

                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    Toast.makeText(RegisterActivity.this, "用户名和密码不能为空", Toast.LENGTH_SHORT).show();
                } else if (password.length() < 6 || password.length() > 16) {
                    Toast.makeText(RegisterActivity.this, "密码必须在6-16位之间", Toast.LENGTH_SHORT).show();
                } else if (dbOperation.registerUser(username, password)) {
                    Toast.makeText(RegisterActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
                    // 跳转到登录界面
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    // 可选：关闭注册界面
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "注册失败，用户名可能已被使用！", Toast.LENGTH_SHORT).show();
                }
            }
        });



        txtLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 处理已有账号点击登录按钮点击事件，跳转到登录页面
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
