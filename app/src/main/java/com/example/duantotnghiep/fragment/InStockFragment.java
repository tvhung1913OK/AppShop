package com.example.duantotnghiep.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duantotnghiep.R;
import com.example.duantotnghiep.adapter.ProductAdapter;
import com.example.duantotnghiep.model.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class InStockFragment extends Fragment {
    RecyclerView rvManager;
    private FirebaseAuth firebaseAuth;
    ProductAdapter productAdapter;
    List<Product> productList;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.in_stock_fragment, container, false);
        rvManager = root.findViewById(R.id.rvManager);
        firebaseAuth = FirebaseAuth.getInstance();
        productList = new ArrayList<>();
         productAdapter = new ProductAdapter(requireContext(), productList);
        rvManager.setLayoutManager(new LinearLayoutManager(getContext()));
        rvManager.setAdapter(productAdapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference productsRef = database.getReference("products");

        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userId = firebaseAuth.getCurrentUser().getUid();
                productList.clear();

                for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                    Product product = productSnapshot.getValue(Product.class);

                    if (product != null && product.getSellerId() != null && product.getSellerId().equals(userId) && product.getQuantity() > 0) {
                        productList.add(product);
                    }
                }

                productAdapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi
            }
        });

        return root;
    }
}









