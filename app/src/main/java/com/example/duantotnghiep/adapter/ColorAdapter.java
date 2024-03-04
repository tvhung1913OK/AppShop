package com.example.duantotnghiep.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duantotnghiep.R;

import java.util.ArrayList;
import java.util.List;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ColorViewHolder> {
    private List<Integer> colors;
    private int selectedItem = RecyclerView.NO_POSITION;
    private ColorAdapterListener listener;

    public ColorAdapter(List<Integer> colors) {
        this.colors = colors;
    }

    public ColorAdapterListener getListener() {
        return listener;
    }

    public void setListener(ColorAdapterListener listener) {
        this.listener = listener;
    }

    public String getSelectedColor() {
        if (selectedItem != RecyclerView.NO_POSITION) {
            Integer color = colors.get(selectedItem);
            return color.toString();
        } else {
            return null;
        }
    }

    @NonNull
    @Override
    public ColorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_color, parent, false);
        return new ColorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorViewHolder holder, int position) {
        int color = colors.get(position);
        holder.setColor(color);
    }

    @Override
    public int getItemCount() {
        return colors.size();
    }

    public List<Integer> getSelectedColorList() {
        List<Integer> selectedColors = new ArrayList<>();
        if (selectedItem != RecyclerView.NO_POSITION) {
            selectedColors.add(colors.get(selectedItem));
        }
        return selectedColors;
    }

    public class ColorViewHolder extends RecyclerView.ViewHolder {
        private ImageView colorImageView;
        private LinearLayout ln_bg;

        public ColorViewHolder(@NonNull View itemView) {
            super(itemView);
            colorImageView = itemView.findViewById(R.id.colorImageView);
            ln_bg = itemView.findViewById(R.id.ln_bg);
        }

        public void setColor(int color) {
            colorImageView.setBackgroundColor(color);
            colorImageView.setOnClickListener(new View.OnClickListener() {
                private boolean isClicked = false;

                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        if (!isClicked) {
                            clearSelection();
                            ln_bg.setBackgroundResource(R.drawable.btn_shop_click);
                            isClicked = true;
                            selectedItem = position;
                        }
                    }
                    listener.onSelect(position);
                }
            });

            if (selectedItem == getAdapterPosition()) {
                ln_bg.setBackgroundResource(R.drawable.btn_shop_click);
            } else {
                ln_bg.setBackgroundResource(R.drawable.btn_shop);
            }
        }

        private void clearSelection() {
            int previousSelectedItem = selectedItem;
            selectedItem = RecyclerView.NO_POSITION;
            if (previousSelectedItem != RecyclerView.NO_POSITION) {
                notifyItemChanged(previousSelectedItem);
            }
        }
    }

    public interface ColorAdapterListener {
        void onSelect(int i);
    }
}