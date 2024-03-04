package com.example.duantotnghiep.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.duantotnghiep.R;
import com.example.duantotnghiep.databinding.ItemImageBinding;

import java.util.List;

public class MutilpleImgAdapter extends RecyclerView.Adapter<MutilpleImgAdapter.ViewHolder> {
    private List<Uri> imageList;
    private Context context;

    public MutilpleImgAdapter(Context context, List<Uri> imageList) {
        this.imageList = imageList;
        this.context = context;
    }


    public void setImageList(List<Uri> imageList) {
        this.imageList = imageList;
        notifyDataSetChanged();
    }
    public void setImageListEdit(List<Uri> newImageList) {
        // Thêm danh sách Uri mới vào danh sách hiện có
        imageList.addAll(newImageList);
        notifyDataSetChanged(); // Cập nhật giao diện RecyclerView
    }

    @NonNull
    @Override
    public MutilpleImgAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemImageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false).getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull MutilpleImgAdapter.ViewHolder holder, int position) {
        Uri imageUri = imageList.get(position);
        holder.bind(imageUri);
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageMutil);
        }

        public void bind(Uri imageUri) {
            if (imageUri != null) {

                Glide.with(context)
                        .load(imageUri)
                        .into(imageView);
            }
        }

    }
}
