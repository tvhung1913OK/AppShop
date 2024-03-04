package com.example.duantotnghiep.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.duantotnghiep.AdminActivity;
import com.example.duantotnghiep.MainActivity;
import com.example.duantotnghiep.R;
import com.example.duantotnghiep.database.FireBaseType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private TextInputEditText edEmail;
    private TextInputEditText edPass;
    private TextView tvGoToSignUp,tvForgetPass;

    private Button btnLogin;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        tvForgetPass = findViewById(R.id.tv_forgetPass);
        edEmail = (TextInputEditText) findViewById(R.id.ed_email);
        edPass = (TextInputEditText) findViewById(R.id.ed_pass);
        btnLogin = findViewById(R.id.btn_login);
        tvGoToSignUp = (TextView) findViewById(R.id.tv_goToSignUp);
        btnLogin.setOnClickListener(this);
        tvGoToSignUp.setOnClickListener(this);
        tvForgetPass.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        mAuth = FirebaseAuth.getInstance();
        if(view.getId()==R.id.btn_login){
            String email = edEmail.getText().toString();
            final String password = edPass.getText().toString();
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                return;
            }
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                String id = user.getUid();
                                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("user").child(id);
                                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            boolean isAdmin = FireBaseType.isAdmin(dataSnapshot);
                                            boolean lock = dataSnapshot.child("lock").getValue(Boolean.class);
                                            if (!lock){
                                                if (isAdmin) {
                                                    startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                                                } else {
                                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                                }
                                            }else {
                                                Toast.makeText(LoginActivity.this, "Đăng nhập không thành công do tài khoản có dấu hiệu bất thường vi phạm chính sách StyleNow.", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(LoginActivity.this, AccountLockedActivity.class));
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        // Xử lý lỗi nếu cần
                                        Log.d("Loi login", " "+databaseError.getMessage());
                                    }
                                });
                            } else {
                                Toast.makeText(LoginActivity.this, "Tài khoản hoặc mât khẩu không đúng.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }else if(view.getId()==R.id.tv_goToSignUp){
            startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
            finishAffinity();
        } else if (view.getId()==R.id.tv_forgetPass) {
            startActivity(new Intent(LoginActivity.this,ForgetPasswordActivity.class));
        }


    }
}