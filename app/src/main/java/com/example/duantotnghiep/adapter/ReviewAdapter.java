package com.example.duantotnghiep.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duantotnghiep.R;
import com.example.duantotnghiep.model.Reviews;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private Context context;
    private List<Reviews> list ;

    public ReviewAdapter(Context context, List<Reviews> reviewList1) {
        this.context = context;
        this.list = reviewList1;
    }
    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Reviews review = list.get(position);
        holder.tvUserName.setText(review.getDisplayName());
        holder.tvComment.setText(review.getComment());
        if (review.getImgUser() != null && !review.getImgUser().isEmpty()) {
            Uri imageUri = Uri.parse(review.getImgUser());
            Picasso.get()
                    .load(imageUri)
                    .placeholder(R.drawable.tnf)
                    .error(R.drawable.tnf)
                    .into(holder.imgUser);
        } else {
            holder.imgUser.setImageResource(R.drawable.baseline_person_24);
        }
        holder.date.setText("Đánh giá vào ngày : "+review.getTime());
        holder.ratingBar.setRating(Float.valueOf(review.getStart()));

    }
    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        } else {
            return 0;
        }
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName;
        ImageView imgUser;
        RatingBar ratingBar;

        TextView tvComment,date;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUser = itemView.findViewById(R.id.imgUser);
            ratingBar = itemView.findViewById(R.id.start);
            tvUserName = itemView.findViewById(R.id.nameBuyer);
            tvComment = itemView.findViewById(R.id.tvreviews);
            date = itemView.findViewById(R.id.time);
        }
    }
}