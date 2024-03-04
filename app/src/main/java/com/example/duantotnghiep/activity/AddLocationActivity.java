package com.example.duantotnghiep.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.duantotnghiep.R;
import com.example.duantotnghiep.databinding.ActivityAddLocationBinding;
import com.example.duantotnghiep.model.Location;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddLocationActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityAddLocationBinding binding;
    private FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.imgBack.setOnClickListener(this);
        binding.btnAddLocation.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.img_back){
            finish();
        } else if (view.getId() == R.id.btn_addLocation) {
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            String id_user = firebaseUser.getUid();
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("user").child(id_user).child("infolocation");
            String name = binding.edName.getText().toString().trim();
            String phone = binding.edPhone.getText().toString().trim();
            String location = binding.edLocation.getText().toString().trim();
            String newKey = myRef.push().getKey();
            if (checkValidate(name, phone, location)){
                Location location1 = new Location(newKey,name,phone,location);
                myRef.child(newKey).setValue(location1);
                finish();
            }
        }
    }
    private boolean checkValidate(String name, String phone, String location){
        if (name.isEmpty() || phone.isEmpty() || location.isEmpty()){
            Toast.makeText(this, "Name, Phone, Location không được để trống!", Toast.LENGTH_SHORT).show();
            return false;
        }

        String phoneForm = "^0\\d{9}$";
//        String nameForm1 = "[a-zA-Z\\s]+";
//
//        if (!name.matches(nameForm1)){
//            Toast.makeText(this, "Tên không đúng định dạng!", Toast.LENGTH_SHORT).show();
//            return false;
//        }
        if (!phone.matches(phoneForm)){
            Toast.makeText(this, "Số điện thoại không đúng định dạng!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}