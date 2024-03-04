package com.example.duantotnghiep.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.duantotnghiep.R;
import com.example.duantotnghiep.activity.CartActivity;
import com.example.duantotnghiep.adapter.NotificationAdapter;
import com.example.duantotnghiep.model.AddProductToCart;
import com.example.duantotnghiep.model.Card;
import com.example.duantotnghiep.model.NotificationItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationFragment extends Fragment {
    private ImageView imgCart, imgCart1;
    private TextView totalCart;
    private FirebaseUser firebaseUser;
    private RecyclerView recyclerView;
    private TextView noResultsTextView;
    private NotificationAdapter notificationAdapter;
    private DatabaseReference notificationRef;

    private List<NotificationItem> notificationItems = new ArrayList<>();

    public NotificationFragment() {
        // Required empty public constructor
    }

    public static NotificationFragment newInstance() {
        NotificationFragment fragment = new NotificationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        notificationRef = FirebaseDatabase.getInstance().getReference("notifications").child(firebaseUser.getUid());
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        imgCart = view.findViewById(R.id.imageView4);
        imgCart1 = view.findViewById(R.id.imageView3);
        totalCart = view.findViewById(R.id.totalCart);
        checkAndHideImageViews();
        recyclerView = view.findViewById(R.id.listNotifition);
        noResultsTextView = view.findViewById(R.id.noResultsTextView);
        notificationAdapter = new NotificationAdapter(notificationItems);
        recyclerView.setAdapter(notificationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (notificationItems.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            noResultsTextView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            noResultsTextView.setVisibility(View.GONE);
        }

        imgCart.setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), CartActivity.class);
            startActivity(intent);
        });
        imgCart1.setOnClickListener(view12 -> {
            Intent intent = new Intent(getContext(), CartActivity.class);
            startActivity(intent);
        });

        TotalItemCart();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        notificationRef.orderByChild("dateTime").addValueEventListener(notificationValueEventListener);
    }

    private void TotalItemCart() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String id_user = firebaseUser.getUid();
        DatabaseReference myReference = firebaseDatabase.getReference("cart").child(id_user);
        myReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<AddProductToCart> list = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    AddProductToCart product = dataSnapshot.getValue(AddProductToCart.class);
                    list.add(product);
                }
                if (list.size() == 0) {
                    return;
                } else {
                    totalCart.setText(" " + list.size());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Loi", "onCancelled: " + error.getMessage());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        notificationRef.removeEventListener(notificationChildEventListener);
    }
    private void checkAndHideImageViews() {
        if (firebaseUser != null && firebaseUser.getUid().equals("ZYA1yQdRAYSzh1K24ZVYIYvHIc92")) {
            imgCart.setVisibility(View.GONE);
            imgCart1.setVisibility(View.GONE);
            totalCart.setVisibility(View.GONE);
        }
    }
    private void updateUI() {
        if (notificationItems.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            noResultsTextView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            noResultsTextView.setVisibility(View.GONE);
        }
    }

    private ValueEventListener notificationValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            notificationItems.clear();
            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                NotificationItem notificationItem = childSnapshot.getValue(NotificationItem.class);
                notificationItems.add(0, notificationItem);
            }
            Collections.sort(notificationItems);

            notificationAdapter.notifyDataSetChanged();

            updateUI();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            // Handle errors
        }
    };

    private ChildEventListener notificationChildEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
            NotificationItem notificationItem = dataSnapshot.getValue(NotificationItem.class);
            if (notificationItem != null) {
                notificationItems.add(0, notificationItem);
            }
            Collections.sort(notificationItems);
            notificationAdapter.notifyDataSetChanged();
            updateUI();
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
            // Handle changed event
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            // Handle removed event
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
            // Handle moved event
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            // Handle errors
        }
    };
}