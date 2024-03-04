package com.example.duantotnghiep.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duantotnghiep.databinding.SliderItemContainerBinding;
import com.makeramen.roundedimageview.RoundedImageView;

public class SliderImageAdapter extends RecyclerView.Adapter<SliderImageAdapter.ViewHolder> {

    Context context;
    public SliderImageAdapter(Context context) {

        this.context = context;
    }
    @NonNull
    @Override
    public SliderImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SliderImageAdapter.ViewHolder(SliderItemContainerBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SliderImageAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        SliderItemContainerBinding binding;
        public ViewHolder(SliderItemContainerBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }

    }

}
