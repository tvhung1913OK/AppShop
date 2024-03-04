package com.example.duantotnghiep.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duantotnghiep.R;
import com.example.duantotnghiep.model.Discount;

import java.util.List;

public class DiscountDetailsAdapter extends RecyclerView.Adapter<DiscountDetailsAdapter.ViewHolder> {
    private List<Discount> discountList;
    private OnItemClickListener onItemClickListener;

    public DiscountDetailsAdapter(List<Discount> discountList) {
        this.discountList = discountList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_details_discount, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Discount discount = discountList.get(position);
        holder.bind(discount);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick(holder, adapterPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return discountList.size();
    }

    public Discount getItem(int position) {
        return discountList.get(position);
    }

    private void onItemClick(ViewHolder holder, int position) {
        Discount selectedDiscount = getItem(position);
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(selectedDiscount);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtDiscountCode, txtDiscountAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDiscountCode = itemView.findViewById(R.id.tv_name_discount);
            txtDiscountAmount = itemView.findViewById(R.id.tv_value_discount);
        }

        public void bind(Discount discount) {
            txtDiscountCode.setText(discount.getCode());
            txtDiscountAmount.setText(String.valueOf(discount.getAmount()));
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Discount discount);
    }
}