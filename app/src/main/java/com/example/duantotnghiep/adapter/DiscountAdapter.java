package com.example.duantotnghiep.adapter;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duantotnghiep.R;
import com.example.duantotnghiep.model.Discount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class DiscountAdapter extends RecyclerView.Adapter<DiscountAdapter.ViewHolder> {
    private List<Discount> discountList;
    public DiscountAdapter(List<Discount> discountList) {
        this.discountList = discountList;
    }

    public void setDiscountList(List<Discount> discountList) {
        this.discountList = discountList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_discount, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Discount discount = discountList.get(position);

        // Bind data to ViewHolder
        holder.nameTextView.setText(discount.getCode());
        holder.valueTextView.setText(String.valueOf(discount.getAmount()));

        holder.img_deleteAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int clickedPosition = holder.getAdapterPosition();
                if (clickedPosition != RecyclerView.NO_POSITION) {
                    Discount clickedDiscount = discountList.get(clickedPosition); // Lấy đúng đối tượng từ discountList
                    if (clickedDiscount != null) {
                        // Cập nhật trạng thái chọn của discount
                        clickedDiscount.setChecked(true);
                        Log.d("DiscountAdapter", "Discount ID: " + clickedDiscount.getId());
                        deleteDiscountFromFirebase(clickedDiscount);
                    }
                }
            }
        });
    }
    private void deleteDiscountFromFirebase(Discount discount) {
        DatabaseReference discountRef = FirebaseDatabase.getInstance().getReference("discounts");
        String discountId = discount.getId() != null ? discount.getId() : "";
        Log.d("DiscountAdapter", "Deleting discount with ID: " + discountId);
        if (!discountId.isEmpty()) { // Kiểm tra discountId không rỗng
            discountRef.child(discountId).removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, "Đã xóa discount thành công", Toast.LENGTH_SHORT).show();
                            discountList.remove(discount);
                            notifyDataSetChanged();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Xóa discount thất bại
                            Toast.makeText(context, "Lỗi khi xóa discount", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Log.d("DiscountAdapter", "Discount ID is empty or null");
        }
    }
    @Override
    public int getItemCount() {
        return discountList == null ? 0 : discountList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView valueTextView;

        private ImageView img_deleteAddress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.tv_name_discount);
            valueTextView = itemView.findViewById(R.id.tv_value_discount);
            img_deleteAddress = itemView.findViewById(R.id.img_deleteAddress);
        }
    }
    public void updateDiscountList(List<Discount> updatedDiscountList) {
        this.discountList = updatedDiscountList;
        notifyDataSetChanged();
    }
    public Discount getDiscount(int position) {
        return discountList.get(position);
    }
}

