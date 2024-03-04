package com.example.duantotnghiep.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.duantotnghiep.activity.OrderActivity;
import com.example.duantotnghiep.R;
import com.example.duantotnghiep.databinding.ItemProductHomeBinding;
import com.example.duantotnghiep.model.Product;
import com.example.duantotnghiep.model.Reviews;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.List;

public class ProductHomeAdapter extends RecyclerView.Adapter<ProductHomeAdapter.ProductHomeViewHolder> {
    private Context context;
    private List<Product> productList;
    public ProductHomeAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }
    public void setData(List<Product> productList) {
        this.productList = productList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductHomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductHomeViewHolder(ItemProductHomeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductHomeViewHolder holder, int position) {
        Product product = productList.get(position);
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        holder.binding.tvPriceHome.setText(decimalFormat.format(product.getPrice()) + " VND");
        holder.binding.tvTitleHome.setText(product.getName());
        holder.binding.tvSoldHome.setText(String.valueOf("Đã bán " + product.getSold()));
        if (product.getImgProduct() != null && !product.getImgProduct().isEmpty()) {
            Uri firstImageUri = Uri.parse(product.getImgProduct().get(0));
            Glide.with(context).load(firstImageUri).into(holder.binding.imvProductHome);
        } else {
            holder.binding.imvProductHome.setImageResource(R.drawable.tnf);
        }
        holder.binding.ctlProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, OrderActivity.class);
                intent.putExtra("idPro",product.getId());
                context.startActivity(intent);
            }
        });
        DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference("reviews");
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("products").child(product.getId());
        reviewsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                float totalRating = 0;
                int numRatings = 0;

                for (DataSnapshot reviewSnapshot : dataSnapshot.getChildren()) {
                    Reviews review = reviewSnapshot.getValue(Reviews.class);
                    if (review != null && review.getProductId().equals(product.getId())) {
                        totalRating += review.getStart();
                        numRatings++;
                    }
                }

                if (numRatings > 0) {
                    float averageRating = totalRating / numRatings;
                    DecimalFormat decimalFormat = new DecimalFormat("0.0");
                    String formattedRating = decimalFormat.format(averageRating);
                    holder.binding.tvStart.setText(formattedRating);
                    productRef.child("averageRating").setValue(averageRating);
                    productRef.child("numRatings").setValue(numRatings);
                } else {
                    holder.binding.tvStart.setText("5.0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("loi", "onCancelled: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void updateProductList(List<Product> updatedList) {
        productList = updatedList;
        notifyDataSetChanged();
    }
    public class ProductHomeViewHolder extends RecyclerView.ViewHolder {
        ItemProductHomeBinding binding;

        public ProductHomeViewHolder(ItemProductHomeBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}