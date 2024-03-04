package com.example.duantotnghiep.ChatBox.Chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.duantotnghiep.ChatBox.Messages.MessagesList;
import com.example.duantotnghiep.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private String chatKey;
    private RecyclerView chattingRcv;
    private ChatAdapter chatAdapter;
    private List<ChatList> chatLists = new ArrayList<>();
    private MessagesList mesL ;
    private boolean loadingFirstTime = true;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    String user1, user2,getName,getImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        final ImageView back = findViewById(R.id.backImg);
        final TextView nameTv = findViewById(R.id.tv_name);
        final EditText messageEditText = findViewById(R.id.ed_messages);
        final CircleImageView profilePic = findViewById(R.id.profilePic);
        final ImageView send = findViewById(R.id.sendImg);

        chattingRcv = findViewById(R.id.chattingRcv);
        getName = getIntent().getStringExtra("name");
        getImg = getIntent().getStringExtra("img");

        user1 = firebaseUser.getUid();
        user2 = getIntent().getStringExtra("act_id");

        nameTv.setText(getName);
        if (getImg != null && !getImg.isEmpty()) {
            Uri imageUri = Uri.parse(getImg);
            Picasso.get()
                    .load(imageUri)
                    .placeholder(R.drawable.baseline_person_24)
                    .error(R.drawable.baseline_person_24)
                    .into(profilePic);
        } else {
            profilePic.setImageResource(R.drawable.baseline_person_24);
        }

        chattingRcv.setHasFixedSize(true);
        chattingRcv.setLayoutManager(new LinearLayoutManager(ChatActivity.this));

        chatAdapter = new ChatAdapter(chatLists, ChatActivity.this);
        chattingRcv.setAdapter(chatAdapter);
        findChatKey();



        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String getTxtMessage = messageEditText.getText().toString();
                String idUser = firebaseUser.getUid();

                if (chatKey != null) {
                    DatabaseReference chatRef = databaseReference.child("chat").child(chatKey);
                    String messageKey = chatRef.child("messages").push().getKey();

                    Map<String, Object> messageData = new HashMap<>();
                    messageData.put("idSend", idUser);
                    messageData.put("msg", getTxtMessage);

                    chatRef.child("messages").child(messageKey).setValue(messageData);

                    messageEditText.setText("");
                    displayChatMessages();
                } else {
                    createNewChat(idUser, user1, user2, getTxtMessage);
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void findChatKey() {
        databaseReference.child("chat").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatKey = null;

                for (DataSnapshot chatSnapshot : snapshot.getChildren()) {
                    if (isChatMatch(chatSnapshot, user1, user2)) {
                        chatKey = chatSnapshot.getKey();
                        displayChatMessages();
                        break;
                    }
                }
                if (chatKey == null) {
                    createNewChat1(user1, user2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi
            }
        });
    }
    private boolean isChatMatch(DataSnapshot chatSnapshot, String user1, String user2) {
        String chatUser1 = chatSnapshot.child("user_1").getValue(String.class);
        String chatUser2 = chatSnapshot.child("user_2").getValue(String.class);
        return (chatUser1.equals(user1) && chatUser2.equals(user2)) ||
                (chatUser1.equals(user2) && chatUser2.equals(user1));
    }
    private void createNewChat1(String user1, String user2) {
        DatabaseReference chatRef = databaseReference.child("chat").push();
        chatKey = chatRef.getKey();

        chatRef.child("user_1").setValue(user1);
        chatRef.child("user_2").setValue(user2);

        displayChatMessages();
    }
    private void createNewChat(String idUser, String user1, String user2, String message) {
        DatabaseReference chatRef = databaseReference.child("chat").push();


        chatRef.child("user_1").setValue(user1);
        chatRef.child("user_2").setValue(user2);

        String messageKey = chatRef.child("messages").push().getKey();
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("idSend", idUser);
        messageData.put("msg", message);

        chatRef.child("messages").child(messageKey).setValue(messageData);

        displayChatMessages();
    }
    private void displayChatMessages() {
        if (chatKey != null) {
            DatabaseReference messagesRef = databaseReference.child("chat").child(chatKey).child("messages");
            messagesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    chatLists.clear();
                    for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                        String idSend = messageSnapshot.child("idSend").getValue(String.class);
                        String message = messageSnapshot.child("msg").getValue(String.class);

                        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                        Date date = new Date(timestamp.getTime());
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        SimpleDateFormat simpTimeFormat = new SimpleDateFormat("hh:mm aa", Locale.getDefault());

                        ChatList chatList = new ChatList(idSend, firebaseUser.getUid(), getName, message, simpleDateFormat.format(date), simpTimeFormat.format(date));
                        chatLists.add(chatList);
                    }

                    chatAdapter.notifyDataSetChanged();
                    scrollingToBottom();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Xử lý lỗi
                }
            });
        }
    }
    private void scrollingToBottom() {
        chattingRcv.scrollToPosition(chatLists.size() - 1);
    }
}