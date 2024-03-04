package com.example.duantotnghiep.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duantotnghiep.R;
import com.example.duantotnghiep.model.Discount;
import com.example.duantotnghiep.model.Product;

import java.util.List;

public class DiscountSelectionAdapter extends RecyclerView.Adapter<DiscountSelectionAdapter.ViewHolder> {

    private List<Discount> discountList;
    String tvName;
    private List<Discount> selectedDiscountIds;

    public DiscountSelectionAdapter(List<Discount> discountList, List<Discount> selectedDiscountIds) {
        this.discountList = discountList;
        this.selectedDiscountIds = selectedDiscountIds;
    }
    public void setSelectedDiscountIds(List<Discount> selectedDiscountIds) {
        this.selectedDiscountIds = selectedDiscountIds;
        notifyDataSetChanged();
    }

    public List<Discount> getSelectedDiscountIds() {
        return selectedDiscountIds;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_discount_selection, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Discount discount = discountList.get(position);

        holder.tvName.setText(discount.getCode());
        holder.tvValue.setText(String.valueOf(discount.getAmount()));

        holder.checkboxDiscount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    selectedDiscountIds.add(discount);
                    Log.d("DiscountSelectionAdapter", "Selected Discount: " + discount.getCode());
                    Log.d("DiscountSelectionAdapter", "Selected Discounts List: " + selectedDiscountIdsToString());
                } else {
                    selectedDiscountIds.remove(discount);
                    Log.d("DiscountSelectionAdapter", "Selected Discount: " + discount.getCode());
                    Log.d("DiscountSelectionAdapter", "Selected Discounts List: " + selectedDiscountIdsToString());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return discountList.size();
    }
    private String selectedDiscountIdsToString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        for (Discount selectedDiscount : selectedDiscountIds) {
            stringBuilder.append(selectedDiscount.getCode()).append(", ");
        }
        if (selectedDiscountIds.size() > 0) {
            stringBuilder.setLength(stringBuilder.length() - 2); // Loại bỏ dấu ", " cuối cùng
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkboxDiscount;
        TextView tvName,tvValue;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkboxDiscount = itemView.findViewById(R.id.checkboxDiscount);
            tvValue = itemView.findViewById(R.id.tv_value_discount_product);
            tvName = itemView.findViewById(R.id.tv_name_discount_product);
        }




    }
}
