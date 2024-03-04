package com.example.duantotnghiep.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duantotnghiep.R;
import com.example.duantotnghiep.adapter.ProductOrderReviewAdapter;
import com.example.duantotnghiep.model.InfoProductOrder;
import com.example.duantotnghiep.model.Reviews;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReviewsActivity extends AppCompatActivity {
    private ImageButton img_back;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference mReference;
    private RecyclerView rcylistPrReview;
    private ProductOrderReviewAdapter adapter;
    private List<InfoProductOrder> listinfoProductOrders = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        anhXa();
        Intent intent = getIntent();
        String id_Order = intent.getStringExtra("idOrder");
        getDataOrder(id_Order);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    private void anhXa(){
        img_back = findViewById(R.id.img_back);
        mReference = FirebaseDatabase.getInstance().getReference();
        rcylistPrReview = findViewById(R.id.listPrReview);
        rcylistPrReview.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductOrderReviewAdapter(ReviewsActivity.this, listinfoProductOrders);
        rcylistPrReview.setAdapter(adapter);

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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
                        for (DataSnapshot productSnapshot : orderSnapshot.child("listProduct").getChildren()) {
                            InfoProductOrder productOrder = productSnapshot.getValue(InfoProductOrder.class);
                            if (productOrder != null) {
                                listinfoProductOrders.add(productOrder);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(ReviewsActivity.this, "NULLLL", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("===", "onCancelled: Error retrieving order data", error.toException());
            }
        });
    }



    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date currentDate = new Date();
        return sdf.format(currentDate);
    }
    private void sendReviews(String id,String idOrder,String idProduct,int start, String cmt){
        String idUser = firebaseUser.getUid();
        DatabaseReference userRef = mReference.child("user").child(idUser);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String img = snapshot.child("img").getValue(String.class);
                String name = snapshot.child("username").getValue(String.class);
                String date = getCurrentTime();
                Reviews reviews = new Reviews(id,idUser,name,img,idOrder,idProduct,start,cmt,date);
                AddReviews(reviews);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void AddReviews(Reviews reviews) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("reviews");
        String id = reviews.getId();
        myRef.child(id).setValue(reviews, (error, ref) -> {
            if (error == null) {
            } else {
                Toast.makeText(ReviewsActivity.this, "Lỗi khi lưu reviews vào Realtime Database", Toast.LENGTH_SHORT).show();
            }
        });
    }
}