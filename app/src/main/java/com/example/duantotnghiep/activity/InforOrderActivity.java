package com.example.duantotnghiep.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.duantotnghiep.R;
import com.example.duantotnghiep.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class InforOrderActivity extends AppCompatActivity {
    private ImageView imgBuyer, imgSeller, imgOrder, colorOrder;
    private TextView nameBuyer, phoneBuyer, emailBuyer;
    private TextView nameSeller, phoneSeller, emailSeller;
    private TextView nameOrder, quantityOrder, totalOrder;
    private Button done;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infor_order);
        imgBuyer = findViewById(R.id.imgBuyer);
        imgSeller = findViewById(R.id.imgSeller);
        imgOrder = findViewById(R.id.imgOrder);
        colorOrder = findViewById(R.id.colorOrder);

        nameBuyer = findViewById(R.id.nameBuyer);
        phoneBuyer = findViewById(R.id.phoneBuyer);
        emailBuyer = findViewById(R.id.emailBuyer);

        nameSeller = findViewById(R.id.nameSeller);
        phoneSeller = findViewById(R.id.phoneSeller);
        emailSeller = findViewById(R.id.emailSeller);

        nameOrder = findViewById(R.id.nameOrder);
        quantityOrder = findViewById(R.id.quantityOrder);
        totalOrder = findViewById(R.id.totalOrder);

        Intent intent = getIntent();
        String id_Order = intent.getStringExtra("idOrder");
        getDataBuyer(id_Order);
        getDataSeller(id_Order);
//        getDataOrder(id_Order);
        done = findViewById(R.id.btn_done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void getDataBuyer(String id) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("list_order");
        reference.orderByChild("id").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    String idOrder = orderSnapshot.child("id").getValue(String.class);
                    if (idOrder != null && idOrder.equals(id)) {
                        String idBuyer = orderSnapshot.child("idBuyer").getValue(String.class);
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("user");
                        userRef.orderByChild("id").equalTo(idBuyer).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                if (userSnapshot.exists()) {
                                    for (DataSnapshot dataSnapshot : userSnapshot.getChildren()) {
                                        String name = dataSnapshot.child("username").getValue(String.class);
                                        String img = dataSnapshot.child("img").getValue(String.class);
                                        String phone = dataSnapshot.child("phone").getValue(String.class);
                                        String email = dataSnapshot.child("email").getValue(String.class);
                                        if (img != null && !img.isEmpty()) {
                                            Uri imageUri = Uri.parse(img);
                                            Picasso.get()
                                                    .load(imageUri)
                                                    .placeholder(R.drawable.tnf)
                                                    .error(R.drawable.tnf)
                                                    .into(imgBuyer);
                                        } else {
                                          imgBuyer.setImageResource(R.drawable.baseline_person_24);
                                        }
                                        nameBuyer.setText(name);
                                        phoneBuyer.setText(phone);
                                        emailBuyer.setText(email);
                                    }
                                } else {
                                    Log.d("===", "onDataChange: User not found");
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("===", "onCancelled: Error retrieving user data", error.toException());
                            }
                        });
                    } else {
                        Toast.makeText(InforOrderActivity.this, "NULLLL", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("===", "onCancelled: Error retrieving order data", error.toException());
            }
        });
    }
    private void getDataSeller(String id) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("list_order");
        reference.orderByChild("id").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    String idOrder = orderSnapshot.child("id").getValue(String.class);
                    if (idOrder != null && idOrder.equals(id)) {
                        String idBuyer = orderSnapshot.child("idSeller").getValue(String.class);
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("user");
                        userRef.orderByChild("id").equalTo(idBuyer).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                if (userSnapshot.exists()) {
                                    for (DataSnapshot dataSnapshot : userSnapshot.getChildren()) {
                                        String name = dataSnapshot.child("username").getValue(String.class);
                                        String img = dataSnapshot.child("img").getValue(String.class);
                                        String phone = dataSnapshot.child("phone").getValue(String.class);
                                        String email = dataSnapshot.child("email").getValue(String.class);
                                        if (img != null && !img.isEmpty()) {
                                            Uri imageUri = Uri.parse(img);
                                            Picasso.get()
                                                    .load(imageUri)
                                                    .placeholder(R.drawable.tnf)
                                                    .error(R.drawable.tnf)
                                                    .into(imgSeller);
                                        } else {
                                            imgBuyer.setImageResource(R.drawable.baseline_person_24);
                                        }
                                        nameSeller.setText(name);
                                        phoneSeller.setText(phone);
                                        emailSeller.setText(email);
                                    }
                                } else {
                                    Log.d("===", "onDataChange: User not found");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("===", "onCancelled: Error retrieving user data", error.toException());
                            }
                        });
                    } else {
                        Toast.makeText(InforOrderActivity.this, "NULLLL", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("===", "onCancelled: Error retrieving order data", error.toException());
            }
        });
    }
    private void getDataOrder(String id) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("list_order");
        reference.orderByChild("id").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    String idOrder = orderSnapshot.child("id").getValue(String.class);
                    if (idOrder != null && idOrder.equals(id)) {
                        String name = orderSnapshot.child("nameProduct").getValue(String.class);
                        int quantity = orderSnapshot.child("quantity").getValue(Integer.class);
                        String img = orderSnapshot.child("imgProduct").getValue(String.class);
                        int color = orderSnapshot.child("color").getValue(Integer.class);
                        double total = orderSnapshot.child("total").getValue(Double.class);
                        nameOrder.setText("Tên SP :"+name);
                        quantityOrder.setText("Số lượng : "+quantity);
                        colorOrder.setBackgroundColor(color);
                        totalOrder.setText("Thành tiền : "+total);
                        if (img != null && !img.isEmpty()) {
                            Uri imageUri = Uri.parse(img);
                            Picasso.get()
                                    .load(imageUri)
                                    .placeholder(R.drawable.pant)
                                    .error(R.drawable.pant)
                                    .into(imgOrder);
                        } else {
                            imgOrder.setImageResource(R.drawable.pant);
                        }

                    } else {
                        Toast.makeText(InforOrderActivity.this, "NULLLL", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("===", "onCancelled: Error retrieving order data", error.toException());
            }
        });
    }
}