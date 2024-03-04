package com.example.duantotnghiep.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duantotnghiep.R;
import com.example.duantotnghiep.model.NotificationItem;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private List<NotificationItem> notificationItems;

    public NotificationAdapter(List<NotificationItem> notificationItems) {
        this.notificationItems = notificationItems;
    }

    public void updateData(List<NotificationItem> notificationItems) {
        this.notificationItems = notificationItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationItem item = notificationItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return notificationItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView img_notification;
        private TextView tvTitle;
        private TextView tvContent;
        private TextView tvNoteTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_notification = itemView.findViewById(R.id.imageNotification);
            tvTitle = itemView.findViewById(R.id.tv_ttContent);
            tvContent = itemView.findViewById(R.id.txtContent);
            tvNoteTime = itemView.findViewById(R.id.tvNoteTime);
        }

        public void bind(NotificationItem item) {
            tvTitle.setText(item.getTitle());
            tvContent.setText(item.getContent());
            tvNoteTime.setText(item.getDateTime());
        }
    }
}
