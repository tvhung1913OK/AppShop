package com.example.duantotnghiep.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duantotnghiep.R;
import com.example.duantotnghiep.adapter.LocationAdapter;

import com.example.duantotnghiep.databinding.ActivityLocationBinding;
import com.example.duantotnghiep.model.Location;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LocationActivity extends AppCompatActivity implements LocationAdapter.Callback, View.OnClickListener {
    private ActivityLocationBinding binding;
    private FirebaseUser firebaseUser;
    private LocationAdapter adapter;
    private ArrayList<Location> list = new ArrayList<>();
    private LinearLayout lnl;
    private ImageButton imgBack;
    private EditText edName;
    private EditText edPhone;
    private EditText edLocation;
    private Button btnEditLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.rcvAddressInfo.setLayoutManager(new LinearLayoutManager(this));
        showList();

        binding.imgBack.setOnClickListener(this);
        binding.imgAddAddress.setOnClickListener(this);
    }

    private void showList(){
        getListInfoLocation();
        adapter = new LocationAdapter(getApplicationContext(), list,this);
        binding.rcvAddressInfo.setAdapter(adapter);
    }
    private void getListInfoLocation(){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String id_user = firebaseUser.getUid();
        DatabaseReference myReference = firebaseDatabase.getReference("user").child(id_user).child("infolocation");
        myReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (list != null){
                    list.clear();
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Location location = dataSnapshot.getValue(Location.class);
                    list.add(location);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("LISTCART", "onCancelled: " + error.getMessage());
            }
        });
    }
    @Override
    public void editAddress(Location address) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_edit_address);
        dialog.setCancelable(false);
        Window window = dialog.getWindow();
        window.setLayout(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
        if (dialog!= null&& dialog.getWindow()!= null){
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.show();
        lnl = (LinearLayout) dialog.findViewById(R.id.lnl);
        imgBack = (ImageButton) dialog.findViewById(R.id.img_back);
        edName = (EditText) dialog.findViewById(R.id.ed_name);
        edPhone = (EditText) dialog.findViewById(R.id.ed_phone);
        edLocation = (EditText) dialog.findViewById(R.id.ed_location);
        btnEditLocation = (Button) dialog.findViewById(R.id.btn_editLocation);

        edName.setText(address.getName());
        edPhone.setText(address.getPhone());
        edLocation.setText(address.getLocation());

        btnEditLocation.setOnClickListener(view -> {
            String name = edName.getText().toString().trim();
            String phone = edPhone.getText().toString().trim();
            String location = edLocation.getText().toString().trim();
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            String id_user = firebaseUser.getUid();
            if (checkValidate(name,phone,location)){
                DatabaseReference myRef = firebaseDatabase.getReference("user").child(id_user).child("infolocation");
                Map<String, Object> updates = new HashMap<>();
                updates.put("name", name);
                updates.put("phone", phone);
                updates.put("location", location);
                myRef.child(address.getId()).updateChildren(updates);
                dialog.dismiss();
            }
        });
        imgBack.setOnClickListener(view -> {
            dialog.dismiss();
        });
    }
    @Override
    public void deleteAddress(Location address) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String id_user = firebaseUser.getUid();
        DatabaseReference myRef = firebaseDatabase.getReference("user").child(id_user).child("infolocation");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xóa sản phẩm");
        builder.setMessage("Bạn có chắc chắn muốn xóa địa chỉ này ?");
        builder.setPositiveButton("Xóa", (dialog, which) -> {
            myRef.child(address.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Xóa thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        // Handle error here
                        Exception e = task.getException();
                        if (e != null) {
                            Log.d("Lỗi", "onComplete: "+e);
                        }
                    }
                }
            });
        });
        builder.setNegativeButton("Hủy", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.img_back){
            finish();
        } else if (view.getId() == R.id.img_addAddress) {
            startActivity(new Intent(LocationActivity.this, AddLocationActivity.class));
        }
    }
    private boolean checkValidate(String name, String phone, String location){
        if (name.isEmpty() || phone.isEmpty() || location.isEmpty()){
            Toast.makeText(this, "Name, Phone, Location không được để trống!", Toast.LENGTH_SHORT).show();
            return false;
        }
        String phoneForm = "^0\\d{9}$";
        String nameForm1 = "[a-zA-Z\\s]+";

        if (!name.matches(nameForm1)){
            Toast.makeText(this, "Tên không đúng định dạng!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!phone.matches(phoneForm)){
            Toast.makeText(this, "Số điện thoại không đúng định dạng!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
    @Override
    protected void onResume() {
        super.onResume();
        showList();
    }
}