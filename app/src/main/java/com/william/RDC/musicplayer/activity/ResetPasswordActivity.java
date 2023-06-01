package com.william.RDC.musicplayer.activity;


import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.william.RDC.musicplayer.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.william.RDC.musicplayer.database.DatabaseOperation;

public class ResetPasswordActivity extends AppCompatActivity {
    private TextInputEditText edtUsername, edtOldPassword, edtNewPassword;
    private MaterialButton btnConfirm, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        edtUsername = findViewById(R.id.edtUsername);
        edtOldPassword = findViewById(R.id.edtOldPassword);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        btnConfirm = findViewById(R.id.btnConfirm);
        btnBack = findViewById(R.id.btnBack);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edtUsername.getText().toString();
                String oldPassword = edtOldPassword.getText().toString();
                String newPassword = edtNewPassword.getText().toString();

                DatabaseOperation dbOperation = DatabaseOperation.getInstance(ResetPasswordActivity.this);
                int resetResult = dbOperation.resetPassword(username, oldPassword, newPassword);
                switch (resetResult) {
                    case 0:
                        Toast.makeText(ResetPasswordActivity.this, "密码已重置", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(ResetPasswordActivity.this, "用户名不存在", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(ResetPasswordActivity.this, "旧密码不正确", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        Toast.makeText(ResetPasswordActivity.this, "新密码长度不符合要求，必须在6到12个字符之间", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(ResetPasswordActivity.this, "重置密码失败，未知错误", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();  // close this activity and return to the previous one
            }
        });
    }
}

