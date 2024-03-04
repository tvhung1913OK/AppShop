package com.example.duantotnghiep.ChatBox.Chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duantotnghiep.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder>{
    private List<ChatList> chatLists;
    private final Context context;

    public ChatAdapter(List<ChatList> chatLists, Context context) {
        this.chatLists = chatLists;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_adapter_layout,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ChatList chatList = chatLists.get(position);

        if(chatList.getId().equals(chatList.getIdUser())){
            holder.myLayout.setVisibility(View.VISIBLE);
            holder.oppoLayout.setVisibility(View.GONE);

            holder.myMessage.setText(chatList.getMessage());
            holder.myTime.setText(chatList.getDate()+" "+chatList.getTime());

        }else {
            holder.myLayout.setVisibility(View.GONE);
            holder.oppoLayout.setVisibility(View.VISIBLE);

            holder.oppoMessage.setText(chatList.getMessage());
            holder.oppoTime.setText(chatList.getDate()+" "+chatList.getTime());
        }
    }

    @Override
    public int getItemCount() {
        return chatLists.size();
    }

    public void updateChatList(List<ChatList> chatLists){
        this.chatLists=chatLists;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout oppoLayout,myLayout;
        private TextView oppoMessage,myMessage;
        private TextView oppoTime,myTime;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            oppoLayout = (LinearLayout) itemView.findViewById(R.id.oppoLayout);
            oppoMessage = (TextView) itemView.findViewById(R.id.oppoMessage);
            oppoTime = (TextView) itemView.findViewById(R.id.oppoMsgTime);
            myLayout = (LinearLayout) itemView.findViewById(R.id.myLayout);
            myMessage = (TextView) itemView.findViewById(R.id.myMessage);
            myTime = (TextView) itemView.findViewById(R.id.myMsgTime);
        }
    }
}
