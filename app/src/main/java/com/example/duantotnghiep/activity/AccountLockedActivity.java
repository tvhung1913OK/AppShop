package com.example.duantotnghiep.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.duantotnghiep.R;

public class AccountLockedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_locked);

        if (getIntent().hasExtra("locked_message")) {
            String message = getIntent().getStringExtra("locked_message");
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }

        Button backButton = findViewById(R.id.btnBackToLogin);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AccountLockedActivity.this, LoginActivity.class));
            }
        });


    }
}