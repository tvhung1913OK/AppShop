package com.example.duantotnghiep.ChatBox.Messages;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duantotnghiep.ChatBox.Chat.ChatActivity;
import com.example.duantotnghiep.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MyViewHolder> {
    private List<MessagesList> messagesLists;
    private Context context;

    public MessagesAdapter(List<MessagesList> messagesLists, Context context) {
        this.messagesLists = messagesLists;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MessagesList mes = messagesLists.get(position);
        if (mes != null) {
            if (mes.getImg() != null && !mes.getImg().isEmpty()) {
                Uri imageUri = Uri.parse(mes.getImg());
                Picasso.get()
                        .load(imageUri)
                        .placeholder(R.drawable.baseline_person_24)
                        .error(R.drawable.baseline_person_24)
                        .into(holder.img);
            } else {
                holder.img.setImageResource(R.drawable.baseline_person_24);
            }
            holder.name.setText(mes.getUsername());
            holder.lastMessages.setText(mes.getLassMessage());

            if (mes.getUnseenMessages() == 0) {
                holder.unseenMessages.setVisibility(View.GONE);
                holder.lastMessages.setTextColor(Color.parseColor("#959595"));
            } else {
                holder.unseenMessages.setVisibility(View.VISIBLE);
                holder.lastMessages.setText(String.valueOf(mes.getUnseenMessages()));
                holder.lastMessages.setTextColor(context.getResources().getColor(R.color.theme_color_80));
            }


            holder.rootLayout.setOnClickListener(v -> {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("name", mes.getUsername());
                intent.putExtra("img", mes.getImg());
                intent.putExtra("act_id", mes.getId());
                context.startActivity(intent);
            });
        }
    }

    public void updateData(List<MessagesList> messagesLists) {
        this.messagesLists = messagesLists;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return messagesLists.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView img;
        private TextView name, lastMessages, unseenMessages;
        private LinearLayout rootLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.profilePic);
            name = itemView.findViewById(R.id.tv_name);
            lastMessages = itemView.findViewById(R.id.tv_lastMessages);
            unseenMessages = itemView.findViewById(R.id.unseenMessages);
            rootLayout = itemView.findViewById(R.id.rootLayout);
        }
    }
}
