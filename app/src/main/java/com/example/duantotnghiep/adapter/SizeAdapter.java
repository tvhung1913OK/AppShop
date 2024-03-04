package com.example.duantotnghiep.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duantotnghiep.R;

import java.util.List;

public class SizeAdapter extends RecyclerView.Adapter<SizeAdapter.SizeViewHolder> {
    private List<String> selectedSizes;
    private int selectedItemPosition = -1; // Vị trí mục đã được chọn
    private SizeAdapterListener listener;

    public SizeAdapter(List<String> selectedSizes) {
        this.selectedSizes = selectedSizes;
    }

    public String getSelectedSize() {
        if (selectedItemPosition != -1) {
            return selectedSizes.get(selectedItemPosition);
        } else {
            return null; // Trả về null nếu không có kích thước nào được chọn
        }
    }

    public SizeAdapterListener getListener() {
        return listener;
    }

    public void setListener(SizeAdapterListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public SizeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected_size, parent, false);
        return new SizeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SizeViewHolder holder, int position) {
        String size = selectedSizes.get(position);
        holder.bind(size, position);
        holder.setSelected(selectedItemPosition == position);
    }

    @Override
    public int getItemCount() {
        return selectedSizes.size();
    }

    public class SizeViewHolder extends RecyclerView.ViewHolder {
        private AppCompatButton sizeTextView;

        public SizeViewHolder(@NonNull View itemView) {
            super(itemView);
            sizeTextView = itemView.findViewById(R.id.sizeTextView);
        }

        public void bind(String size, int position) {
            sizeTextView.setText(size);

            sizeTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Thực hiện chỉ cập nhật khi mục được chọn khác với mục đã chọn trước đó
                    if (selectedItemPosition != position) {
                        // Cập nhật trạng thái đã chọn
                        selectedItemPosition = position;

                        // Cập nhật giao diện người dùng
                        notifyDataSetChanged();
                    }
                    listener.onSelect(position);
                }
            });
        }

        public void setSelected(boolean selected) {
            if (selected) {
                sizeTextView.setBackgroundResource(R.drawable.btn_shop_click);
            } else {
                sizeTextView.setBackgroundResource(R.drawable.btn_shop);
            }
        }
    }

    public interface SizeAdapterListener {
        void onSelect(int i);
    }
}