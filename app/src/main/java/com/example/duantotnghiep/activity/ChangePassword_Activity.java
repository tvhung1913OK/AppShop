package com.example.duantotnghiep.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.duantotnghiep.R;
import com.example.duantotnghiep.fragment.ProfileFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChangePassword_Activity extends AppCompatActivity {
    private TextInputEditText edPass, edPassNew, edRepassNew;
    private Button btn_Save, btn_Cancle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        edPass = findViewById(R.id.ed_Pass);
        edPassNew = findViewById(R.id.ed_Newpass);
        edRepassNew = findViewById(R.id.ed_ReNewpass);
        btn_Save = findViewById(R.id.btn_Save);
        btn_Cancle = findViewById(R.id.btn_Cancel);
        btn_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePassword();
            }
        });
        btn_Cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edPass.setText("");
                edPassNew.setText("");
                edRepassNew.setText("");
                Toast.makeText(ChangePassword_Activity.this, "Đã hủy!!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private boolean changePassword() {
        String currentPassword = edPass.getText().toString();
        String newPassword = edPassNew.getText().toString();
        String reNewPassword = edRepassNew.getText().toString();

        if (newPassword.equals(reNewPassword)) {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();

            if (Check(currentPassword, newPassword, reNewPassword)) {
                if (user != null) {
                    // xác minh mật khẩu
                    AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);

                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // xác minh thành công
                                        user.updatePassword(newPassword)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("user").child(user.getUid());
                                                            userRef.child("password").setValue(newPassword);
                                                            Toast.makeText(ChangePassword_Activity.this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                                                            finish();
                                                        }
                                                    }
                                                });
                                    } else {
                                        Toast.makeText(ChangePassword_Activity.this, "Xác minh mật khẩu hiện tại thất bại", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        } else {
            Toast.makeText(ChangePassword_Activity.this, "Mật khẩu mới không khớp", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private boolean Check(String currentPassword, String newPassword, String reNewPassword ){

        if (currentPassword.length() ==0){
            edPass.requestFocus();
            edPass.setError("Không được để trống !!!");
            return false;
        }
        else if (newPassword.length() ==0){
            edPassNew.requestFocus();
            edPassNew.setError("Không được để trống !!!");
            return false;
        } else if (newPassword.length()<=5) {
            edPassNew.requestFocus();
            edPassNew.setError("Mật khẩu phải có 6 ký tự trở lên !!!");
            return false;
        }
        else if (reNewPassword.length() ==0){
            edRepassNew.requestFocus();
            edRepassNew.setError("Không được để trống !!!");
            return false;
        } else if (reNewPassword.length()<=5) {
            edRepassNew.requestFocus();
            edRepassNew.setError("Mật khẩu phải có 6 ký tự trở lên !!!");
            return false;
        }
        return  true;
    }
}