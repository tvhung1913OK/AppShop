package com.example.duantotnghiep.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duantotnghiep.R;
import com.example.duantotnghiep.model.Card;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class XacNhanCardAdapter extends RecyclerView.Adapter<XacNhanCardAdapter.XacNhanCardViewHolder> {
    private List<Card> cardList;
    private Context context; // Thêm context
    private List<Card> pendingCards;
    private List<Card> successCards;
    private FirebaseAuth mAuth;
    private DatabaseReference notificationRef;
    private FirebaseUser firebaseUser;

    public XacNhanCardAdapter(Context context, List<Card> cardList) {
        this.context = context;

        this.cardList = cardList;
        this.pendingCards = new ArrayList<>();
        this.successCards = new ArrayList<>();
    }

    @NonNull
    @Override
    public XacNhanCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listcardnap_item, parent, false);
        return new XacNhanCardViewHolder(view);
    }

    // Trong XacNhanCardAdapter.java
    @Override
    public void onBindViewHolder(@NonNull XacNhanCardViewHolder holder, int position) {
        Card card = cardList.get(position);

        // Hiển thị thông tin thẻ lên giao diện
        holder.textCardSerial.setText("Mã thẻ: " + card.getCardSerial());
        holder.textCardPin.setText("Mã PIN: " + card.getCardPin());
        holder.textCardProvider.setText("Nhà cung cấp: " + card.getCardProvider());
        holder.textCardValue.setText("Giá trị thẻ: " + card.getCardValue());
        holder.textTime.setText("Thời gian: " + card.getTime());
        holder.textUsername.setText("Người dùng: " + card.getUsername());
        holder.textStatus.setText("Trạng thái: " + card.getStatus());
        if (card.getStatus().equals("success")||card.getStatus().equals("failed")){
            holder.learNapthe.setVisibility(View.GONE);
        }else {
            holder.learNapthe.setVisibility(View.VISIBLE);
        }
        holder.btnToggleStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                card.updateStatusAndCreditUser();
                handleTopUpSuccess(card.getCardProvider(), card.getCardValue(), card.getUserId());
                notifyDataSetChanged();
            }
        });
        holder.btnToggleFailedStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                card.updateFailedStatus();
                handleTopUpSuccess1(card.getCardProvider(), card.getCardValue(), card.getUserId());
                notifyDataSetChanged();
            }
        });


    }
    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        Date currentDate = new Date();
        return sdf.format(currentDate);
    }
    private void handleTopUpSuccess(String cardProvider, String cardValue, String userId) {
        String title = "Nạp thẻ thành công";
        String content = String.format("Thẻ %s trị giá %s đã được Admin xác nhận, tiền đã được cộng vào tài khoản của bạn.", cardProvider, cardValue);
        String currentTime = getCurrentTime();
        notificationRef = FirebaseDatabase.getInstance().getReference("notifications").child(userId);
        String notificationId = notificationRef.push().getKey();

        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("title", title);
        notificationData.put("content", content);
        notificationData.put("dateTime", currentTime);
        notificationData.put("userId", userId);
        notificationData.put("isRead", false); // Thêm trường "isRead" với giá trị mặc định là false

        notificationRef.child(notificationId).setValue(notificationData);
    }

    private void handleTopUpSuccess1(String cardProvider, String cardValue, String userId) {
        String title = "Nạp thẻ không thành công";
        String content = String.format("Thẻ %s trị giá %s có mã thẻ hoặc số seri không đúng, Admin đã hủy yêu cầu.", cardProvider, cardValue);
        String currentTime = getCurrentTime();
        notificationRef = FirebaseDatabase.getInstance().getReference("notifications").child(userId);
        String notificationId = notificationRef.push().getKey();

        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("title", title);
        notificationData.put("content", content);
        notificationData.put("dateTime", currentTime);
        notificationData.put("userId", userId);
        notificationData.put("isRead", false); // Thêm trường "isRead" với giá trị mặc định là false

        notificationRef.child(notificationId).setValue(notificationData);
    }
    public void setCardLists(List<Card> pendingCards) {
        this.cardList.clear();
        this.cardList.addAll(pendingCards);
        notifyDataSetChanged();
    }



    // Phương thức hiển thị Toast
    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


    @Override
    public int getItemCount() {
        return cardList.size();
    }

    public static class XacNhanCardViewHolder extends RecyclerView.ViewHolder {
        TextView textCardSerial;
        TextView textCardPin;
        TextView textCardProvider;
        TextView textCardValue;
        TextView textTime;
        TextView textUsername;
        TextView textStatus;
        Button btnToggleStatus;
        Button btnToggleFailedStatus;
        LinearLayout learNapthe;


        public XacNhanCardViewHolder(@NonNull View itemView) {
            super(itemView);
            textCardSerial = itemView.findViewById(R.id.textCardSerial);
            textCardPin = itemView.findViewById(R.id.textCardPin);
            textCardProvider = itemView.findViewById(R.id.textCardProvider);
            textCardValue = itemView.findViewById(R.id.textCardValue);
            textTime = itemView.findViewById(R.id.textTime);
            textUsername = itemView.findViewById(R.id.textUsername);
            textStatus = itemView.findViewById(R.id.textStatus);
            btnToggleStatus = itemView.findViewById(R.id.btnToggleStatus);
            btnToggleFailedStatus = itemView.findViewById(R.id.btnToggleFailedStatus);
            learNapthe = itemView.findViewById(R.id.learNapthe);


        }
    }
}
