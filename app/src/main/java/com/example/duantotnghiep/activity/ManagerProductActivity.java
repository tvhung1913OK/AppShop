package com.example.duantotnghiep.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.duantotnghiep.R;
import com.example.duantotnghiep.adapter.ManagerSellerAdapter;

import com.example.duantotnghiep.databinding.ActivityManagerProductBinding;
import com.example.duantotnghiep.fragment.AddProductFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

import de.hdodenhof.circleimageview.CircleImageView;

public class ManagerProductActivity extends AppCompatActivity {
    ActivityManagerProductBinding binding;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mReference;
    private FirebaseUser firebaseUser;
    private boolean isInAddProductFragment = false;
    private TextView IsYourName, rateStart, allProduct;
    private CircleImageView imgUser;
    private Picasso picasso = Picasso.get();
    private ValueEventListener productsValueEventListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManagerProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        IsYourName = findViewById(R.id.IsYourName);
        rateStart = findViewById(R.id.rateStart);
        imgUser = findViewById(R.id.imgUserT);
        allProduct = findViewById(R.id.allProduct);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        mReference = FirebaseDatabase.getInstance().getReference();

        binding.tabLayoutSeller.addTab(binding.tabLayoutSeller.newTab().setText("Còn hàng"));
        binding.tabLayoutSeller.addTab(binding.tabLayoutSeller.newTab().setText("Hết hàng"));
        binding.tabLayoutSeller.setTabTextColors(Color.parseColor("#D3D3D3"), Color.parseColor("#E91E63"));
        binding.tabLayoutSeller.setSelectedTabIndicatorColor(Color.parseColor("#E91E63"));
        binding.tabLayoutSeller.setTabGravity(TabLayout.GRAVITY_FILL);
        binding.tabLayoutSeller.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                binding.viewPaperSeller.setCurrentItem(position);
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        binding.viewPaperSeller.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabLayoutSeller));
        binding.viewPaperSeller.setAdapter(new ManagerSellerAdapter(getSupportFragmentManager(), this, binding.tabLayoutSeller.getTabCount()));
        binding.floatAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideFloatingActionButton(); // Ẩn FAB
                isInAddProductFragment = true;
                FragmentManager fragmentManager = getSupportFragmentManager();
                AddProductFragment addProductFragment = new AddProductFragment();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.layout, addProductFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        productsValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Xử lý khi dữ liệu trên Firebase thay đổi
                setInfoProfile();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Loi", "onCancelled: " + error.getMessage());
            }
        };
    }
    private void setInfoProfile() {
        String id = firebaseUser.getUid();
        DatabaseReference userRef = mReference.child("user").child(id);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("username").getValue(String.class);
                IsYourName.setText(name);
                String img = snapshot.child("img").getValue(String.class);
                if (img.equals("") || img.isEmpty()) {
                    imgUser.setImageResource(R.drawable.baseline_person_24);
                } else {
                    picasso.load(img).into(imgUser);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Loi", "onCancelled: " + error.getMessage());
            }
        });
        DatabaseReference productsRef = mReference.child("products");
        Query query = productsRef.orderByChild("sellerId").equalTo(id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalProducts = (int) snapshot.getChildrenCount();
                allProduct.setText("Tổng sản phẩm: " + totalProducts);

                float totalRating = 0;
                int numRatings = 0;

                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    if (productSnapshot.child("averageRating").exists()) {
                        float averageRating = productSnapshot.child("averageRating").getValue(Float.class);
                        totalRating += averageRating;
                        numRatings++;
                    }
                }
                if (numRatings > 0) {
                    float averageRating = totalRating / numRatings;
                    DecimalFormat decimalFormat = new DecimalFormat("0.0");
                    String formattedRating = decimalFormat.format(averageRating);
                    rateStart.setText(formattedRating);
                } else {
                    rateStart.setText("5.0");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Loi", "onCancelled: " + error.getMessage());
            }
        });
    }
    public void hideFloatingActionButton() {
        binding.floatAddProduct.hide();
    }
    public void showFloatingActionButton() {
        binding.floatAddProduct.show();
    }
    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        int backStackEntryCount = fragmentManager.getBackStackEntryCount();
        if (backStackEntryCount > 0) {
            fragmentManager.popBackStack();
            isInAddProductFragment = false;
            if (backStackEntryCount == 1) {
                showFloatingActionButton();
            }
        } else {
            super.onBackPressed();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        setInfoProfile();
        if (!isInAddProductFragment) {
            showFloatingActionButton();
        }
        DatabaseReference productsRef = mReference.child("products");
        productsRef.addValueEventListener(productsValueEventListener);
    }
    @Override
    protected void onPause() {
        super.onPause();

        DatabaseReference productsRef = mReference.child("products");
        productsRef.removeEventListener(productsValueEventListener);
    }
}