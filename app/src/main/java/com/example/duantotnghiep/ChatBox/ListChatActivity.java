package com.example.duantotnghiep.ChatBox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.duantotnghiep.ChatBox.Messages.MessagesAdapter;
import com.example.duantotnghiep.ChatBox.Messages.MessagesList;
import com.example.duantotnghiep.R;
import com.example.duantotnghiep.database.FireBaseType;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListChatActivity extends AppCompatActivity {
    private List<MessagesList> messagesList = new ArrayList<>();
    private CircleImageView imgUserProfile;
    private RecyclerView rcvMessages;
    private MessagesAdapter adapter;

    private String email;
    private String idUr;
    private String name;
    private int unseenMessages = 0;
    private String lastMessage = "";
    private boolean dataSet = false;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private MessagesList list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_chat);
        init();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        setRoleChat();
    }
    private void getListUserChat() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("user");
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        myRef.orderByChild("user_type").equalTo(false).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (messagesList != null) {
                    messagesList.clear();
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    int unseenMessages = 0;
                    String lastMessage = "";
                    MessagesList user = dataSnapshot.getValue(MessagesList.class);
                    if (dataSnapshot.hasChild("messages")) {
                        for (DataSnapshot messageSnapshot : dataSnapshot.child("messages").getChildren()) {
                            String message = messageSnapshot.child("msg").getValue(String.class);
                            String idSend = messageSnapshot.child("idSend").getValue(String.class);
                            if (!idSend.equals(currentUserUid)) {
                                unseenMessages++;
                                lastMessage = message;
                            }
                        }
                    }
                    if (user != null && !user.getId().equals(currentUserUid)) {
                        MessagesList list = new MessagesList(user.getId(), user.getUsername(), lastMessage, user.getImg(), unseenMessages);
                        messagesList.add(list);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ListChatActivity.this, "Get Fail !!!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getListAdmin() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("user");
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        myRef.orderByChild("user_type").equalTo(true).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (messagesList != null) {
                    messagesList.clear();
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    int unseenMessages = 0;
                    String lastMessage = "";
                    MessagesList user = dataSnapshot.getValue(MessagesList.class);
                    if (dataSnapshot.hasChild("messages")) {
                        for (DataSnapshot messageSnapshot : dataSnapshot.child("messages").getChildren()) {
                            String message = messageSnapshot.child("msg").getValue(String.class);
                            String idSend = messageSnapshot.child("idSend").getValue(String.class);
                            if (!idSend.equals(currentUserUid)) {
                                unseenMessages++;
                                lastMessage = message;
                            }
                        }
                    }
                    if (user != null && !user.getId().equals(currentUserUid)) {
                        MessagesList list = new MessagesList(user.getId(), user.getUsername(), lastMessage, user.getImg(), unseenMessages);
                        messagesList.add(list);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ListChatActivity.this, "Get Fail !!!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void init(){
        imgUserProfile = (CircleImageView) findViewById(R.id.img_userProfile);
        rcvMessages = (RecyclerView) findViewById(R.id.rcv_messages);

        rcvMessages.setHasFixedSize(true);
        rcvMessages.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessagesAdapter(messagesList, ListChatActivity.this);
        rcvMessages.setAdapter(adapter);

    }
    public void setRoleChat() {
        String id = firebaseUser.getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("user").child(id);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isAdmin = FireBaseType.isAdmin(dataSnapshot);
                if (isAdmin) {
                    getListUserChat();
                } else {
                    getListAdmin();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Loi", "onCancelled: " + databaseError.getMessage());
            }
        });
    }
}