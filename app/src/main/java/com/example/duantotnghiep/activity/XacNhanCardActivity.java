package com.example.duantotnghiep.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.example.duantotnghiep.R;
import com.example.duantotnghiep.adapter.XacNhanCardAdapter;
import com.example.duantotnghiep.model.Card;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class XacNhanCardActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private XacNhanCardAdapter xacNhanCardAdapter;
    private List<Card> cardList;
    private List<Card> pendingCards;  // Add this line
    private List<Card> successCards;  // Add this line


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xac_nhan_card);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cardList = new ArrayList<>();
        pendingCards = new ArrayList<>();  // Add this line
        successCards = new ArrayList<>();  // Add this line
        xacNhanCardAdapter = new XacNhanCardAdapter(this, cardList);
        recyclerView.setAdapter(xacNhanCardAdapter);

// Kết nối đến Firebase và lấy dữ liệu
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("cards");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cardList.clear();
                pendingCards.clear();  // Add this line
                successCards.clear();  // Add this line
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Card card = dataSnapshot.getValue(Card.class);
                    if (card != null) {
                        cardList.add(card);

                        // Populate pendingCards and successCards based on status
                        if ("pending".equals(card.getStatus())) {
                            pendingCards.add(card);
                        } else if ("success".equals(card.getStatus())) {
                            successCards.add(card);
                        }
                    }
                }
                xacNhanCardAdapter.setCardLists(pendingCards);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi xảy ra trong quá trình đọc dữ liệu từ Firebase
            }
        });

        Spinner spinnerStatus = findViewById(R.id.spinnerStatus);
        spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                switch (position) {
                    case 0:
                        xacNhanCardAdapter.setCardLists(pendingCards);
                        break;
                    case 1:
                        xacNhanCardAdapter.setCardLists(successCards);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Nếu không có mục nào được chọn
            }
        });
    }
}