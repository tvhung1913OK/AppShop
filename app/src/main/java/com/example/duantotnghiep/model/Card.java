package com.example.duantotnghiep.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Card {
    private String cardId;
    private String cardSerial;
    private String cardPin;
    private String cardProvider;
    private String cardValue;
    private String time;
    private String username;
    private String userId;
    private String status;
    private boolean statusChanged;

    public Card() {
        // Constructor mặc định không có đối số để đáp ứng yêu cầu của Firebase
    }

    public Card(String cardId, String cardSerial, String cardPin, String cardProvider, String cardValue, String time, String username, String userId, String status, boolean statusChanged) {
        this.cardId = cardId;
        this.cardSerial = cardSerial;
        this.cardPin = cardPin;
        this.cardProvider = cardProvider;
        this.cardValue = cardValue;
        this.time = time;
        this.username = username;
        this.userId = userId;
        this.status = status;
        this.statusChanged = statusChanged;
    }

    // Các getter và setter

    public String getCardSerial() {
        return cardSerial;
    }

    public void setCardSerial(String cardSerial) {
        this.cardSerial = cardSerial;
    }

    public String getCardPin() {
        return cardPin;
    }

    public void setCardPin(String cardPin) {
        this.cardPin = cardPin;
    }

    public String getCardProvider() {
        return cardProvider;
    }

    public void setCardProvider(String cardProvider) {
        this.cardProvider = cardProvider;
    }

    public String getCardValue() {
        return cardValue;
    }

    public void setCardValue(String cardValue) {
        this.cardValue = cardValue;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isStatusChanged() {
        return statusChanged;
    }

    public void setStatusChanged(boolean statusChanged) {
        this.statusChanged = statusChanged;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public void updateStatusAndCreditUser() {
        if (!statusChanged && status.equals("pending")) {
            // Cập nhật trạng thái thành công
            setStatus("success");
            setStatusChanged(true);

            // Cộng tiền cho người dùng
            DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("user").child(userId);
            Log.d("zzzz", "updateStatusAndCreditUser: "+userReference);
            Log.d("zzz", "updateStatusAndCreditUser: "+userId);
            userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d("zzz", "Data from Firebase: " + dataSnapshot.getValue());

                    // Check if dataSnapshot has a value
                    if (dataSnapshot.exists()) {
                        // Get user data
                        User user = dataSnapshot.getValue(User.class);

                        if (user != null) {
                            Log.d("uservualay", "onDataChange: " + user);

                            // Thực hiện cộng tiền cho người dùng
                            Double currentWallet = user.getWallet();

                            if (currentWallet != null) {
                                // Thực hiện cộng tiền cho người dùng
                                double cardValueDouble = Double.parseDouble(getCardValue());
                                double currentWalletDouble = currentWallet;
                                double newWalletValueDouble = currentWalletDouble + cardValueDouble;

                                // Log thông tin trước khi cập nhật
                                Log.d("zzzzz", "Thông tin trước khi cập nhật - Số dư ví hiện tại: " + currentWallet + ", Giá trị thẻ: " + cardValueDouble);

                                // Cập nhật thông tin người dùng sau khi cộng tiền
                                DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("user").child(userId);
                                Map<String, Object> updateData = new HashMap<>();
                                updateData.put("wallet", newWalletValueDouble);
                                userReference.updateChildren(updateData);

                                // Log thông tin sau khi cập nhật
                            } else {
                                Log.d("wallethientai", "Số dư ví trống hoặc không hợp lệ");
                            }
                        }
                        else {
                            Log.d("usernull", "Lỗi: user là null");
                        }
                    } else {
                        Log.d("1", "Dữ liệu không tồn tại");
                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Xử lý khi có lỗi xảy ra trong quá trình đọc dữ liệu từ Firebase
                    Log.e("zzzzz", "Lỗi khi đọc dữ liệu từ Firebase: " + error.getMessage());
                }
            });

            // Lưu lại thẻ sau khi cập nhật trạng thái và cộng tiền
            DatabaseReference cardReference = FirebaseDatabase.getInstance().getReference("cards").child(cardId);
            cardReference.setValue(this);
        }
    }
    public void updateFailedStatus() {
        if (!statusChanged && status.equals("pending")) {
            // Cập nhật trạng thái không thành công
            setStatus("failed");
            setStatusChanged(true);

            // Lưu lại thẻ sau khi cập nhật trạng thái
            DatabaseReference cardReference = FirebaseDatabase.getInstance().getReference("cards").child(cardId);
            cardReference.setValue(this);
        }
    }

}
