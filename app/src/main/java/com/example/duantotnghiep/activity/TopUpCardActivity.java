package com.example.duantotnghiep.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;

import com.example.duantotnghiep.R;
import com.example.duantotnghiep.adapter.CardAdapter;
import com.example.duantotnghiep.model.Card;
import com.example.duantotnghiep.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TopUpCardActivity extends AppCompatActivity {
    private EditText etCardSerial, etCardPin;
    private Spinner spinnerCardProvider, spinnerCardValue;
    private Button btnAddCard;
    private ListView lvCardList;
    private List<Card> cardList;
    private CardAdapter cardAdapter;
    private String currentUserId;
    private String currentUserUsername;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef, buyerRef, cardRef, notificationRef;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up_card);

        etCardSerial = findViewById(R.id.etCardSerial);
        etCardPin = findViewById(R.id.etCardPin);
        spinnerCardProvider = findViewById(R.id.spinnerCardProvider);
        spinnerCardValue = findViewById(R.id.spinnerCardValue);
        btnAddCard = findViewById(R.id.btnAddCard);
        lvCardList = findViewById(R.id.lvCardList);

        cardList = new ArrayList<>();
        cardAdapter = new CardAdapter(this, cardList);
        lvCardList.setAdapter(cardAdapter);

        String[] cardProviders = {"Chọn thẻ","Viettel", "Mobifone", "Vinaphone"};
        String[] cardValues = {"0 VND","10000", "20000", "50000", "100000"};

        CustomArrayAdapter cardProviderAdapter = new CustomArrayAdapter(this, android.R.layout.simple_spinner_item, List.of(cardProviders));
        CustomArrayAdapter cardValueAdapter = new CustomArrayAdapter(this, android.R.layout.simple_spinner_item, List.of(cardValues));

        cardProviderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cardValueAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerCardProvider.setAdapter(cardProviderAdapter);
        spinnerCardValue.setAdapter(cardValueAdapter);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            String buyerID = firebaseUser.getUid();
            userRef = FirebaseDatabase.getInstance().getReference("user");
            buyerRef = userRef.child("user").child(buyerID).child("wallet");
        }
        loadCardDataFromFirebase();

        btnAddCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy dữ liệu từ giao diện
                String cardSerial = etCardSerial.getText().toString().trim();
                String cardPin = etCardPin.getText().toString().trim();
                String cardProvider = spinnerCardProvider.getSelectedItem().toString();
                String cardValue = spinnerCardValue.getSelectedItem().toString();
                String time = getCurrentTime();
                String userId = mAuth.getUid();
                if (validateInput(cardSerial, cardPin, cardProvider, cardValue)) {
                    userRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String username = dataSnapshot.child("username").getValue(String.class);

                                currentUserUsername = dataSnapshot.child("username").getValue(String.class);
                                cardRef = FirebaseDatabase.getInstance().getReference("cards");
                                String cardId = cardRef.push().getKey();

                                Card card = new Card(cardId, cardSerial, cardPin, cardProvider, cardValue, time, username, userId, "pending", false);
                                cardRef.child(cardId).setValue(card);

                                etCardSerial.setText("");
                                etCardPin.setText("");

                                cardList.add(card);
                                cardAdapter.notifyDataSetChanged();
                                Toast.makeText(TopUpCardActivity.this, "Gửi thẻ thành công, vui lòng chờ trong giây lát", Toast.LENGTH_SHORT).show();
                                handleTopUpSuccess();

                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast toast = Toast.makeText(TopUpCardActivity.this, "", Toast.LENGTH_SHORT);
                                        toast.cancel();
                                    }
                                }, 3000);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Xử lý lỗi nếu cần
                        }
                    });
                }
            }
        });

    }
    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        Date currentDate = new Date();
        return sdf.format(currentDate);
    }
    private void handleTopUpSuccess() {
        // Xử lý khi người dùng nạp thẻ thành công
        String title = "Nạp thẻ";
        String cardProvider = spinnerCardProvider.getSelectedItem().toString();
        String cardValue = spinnerCardValue.getSelectedItem().toString();
        String content = String.format("Bạn đã gửi thành công thẻ %s trị giá %s, vui lòng chờ Admin duyệt trong ít phút.", cardProvider, cardValue);
        String currentTime = getCurrentTime();
        String userId = mAuth.getUid();
        notificationRef = FirebaseDatabase.getInstance().getReference("notifications").child(firebaseUser.getUid());
        String notificationId = notificationRef.push().getKey();

        handleTopUpSuccessToAdmin();

        // Gửi thông báo lên Firebase Realtime Database
        notificationRef.child(notificationId).child("title").setValue(title);
        notificationRef.child(notificationId).child("content").setValue(content);
        notificationRef.child(notificationId).child("dateTime").setValue(currentTime);
        notificationRef.child(notificationId).child("userId").setValue(userId);
    }
    private void handleTopUpSuccessToAdmin() {
        String adminId = "ZYA1yQdRAYSzh1K24ZVYIYvHIc92";

        String username = currentUserUsername;
        String title = "Nạp thẻ";
        String cardProvider = spinnerCardProvider.getSelectedItem().toString();
        String cardValue = spinnerCardValue.getSelectedItem().toString();
        String content = String.format("Người dùng %s vừa nạp thẻ %s trị giá %s, vui lòng kiểm tra và duyệt.", username, cardProvider, cardValue);
        String currentTime = getCurrentTime();

        DatabaseReference adminNotificationRef = FirebaseDatabase.getInstance().getReference("notifications").child(adminId);
        String notificationId = adminNotificationRef.push().getKey();

        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("title", title);
        notificationData.put("content", content);
        notificationData.put("dateTime", currentTime);
        notificationData.put("userId", firebaseUser.getUid());
        notificationData.put("isRead", false);

        adminNotificationRef.child(notificationId).setValue(notificationData);
    }
    private void loadCardDataFromFirebase() {

        cardRef = FirebaseDatabase.getInstance().getReference("cards");
        String currentUserId = mAuth.getUid();
        Query query = cardRef.orderByChild("userId").equalTo(currentUserId);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cardList.clear();

                for (DataSnapshot cardSnapshot : dataSnapshot.getChildren()) {
                    Card card = cardSnapshot.getValue(Card.class);
                    cardList.add(card);
                }
                cardAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi nếu cần
            }
        });
    }
    private class CustomArrayAdapter extends ArrayAdapter<String> {

        public CustomArrayAdapter(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View view = super.getDropDownView(position, convertView, parent);

            // Ẩn mục "Chọn thẻ" và "0" trong danh sách
            if (getItem(position).equals("Chọn thẻ") || getItem(position).equals("0 VND")) {
                view.setVisibility(View.GONE);
            } else {
                view.setVisibility(View.VISIBLE);
            }

            return view;
        }
    }

    private boolean validateInput(String cardSerial, String cardPin, String cardProvider, String cardValue) {
        if (cardSerial.isEmpty() || cardPin.isEmpty()) {
            Toast.makeText(this, "Không được để trống!!!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!cardSerial.matches("\\d+") || !cardPin.matches("\\d+")) {
            Toast.makeText(this, "Mã không đúng, kí tự không đủ (8 - 12 số)", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (cardSerial.length() < 8 || cardSerial.length() > 12) {
            etCardSerial.setError("Seri không tồn tại hoặc kí tự không đủ (8 - 12 số)");
            Toast.makeText(this, "Số serial thẻ phải có từ 8 đến 12 chữ số", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (cardPin.length() < 8 || cardPin.length() > 12) {
            etCardPin.setError("Mã thẻ không tồn tại hoặc kí tự không đủ (8 - 12 số)");
            Toast.makeText(this, "Mã pin thẻ phải có từ 8 đến 12 chữ số", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (cardProvider.equals("Chọn thẻ")) {
            Toast.makeText(this, "Vui lòng chọn loại thẻ", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (cardValue.equals("0 VND")) {
            Toast.makeText(this, "Vui lòng chọn mệnh giá", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}