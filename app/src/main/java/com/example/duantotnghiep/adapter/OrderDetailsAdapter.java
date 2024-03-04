package com.example.duantotnghiep.adapter;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duantotnghiep.R;
import com.example.duantotnghiep.model.ColorProduct;
import com.example.duantotnghiep.model.Product;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailsAdapter extends RecyclerView.Adapter<OrderDetailsAdapter.ViewHolder> {
    private List<Product> productList;
    private List<ColorProduct> color;

    public OrderDetailsAdapter() {
        this.productList = productList;
        this.color = new ArrayList<>();
    }

    public void setColorList(List<ColorProduct> color) {
        this.color = color;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_details, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (productList != null && productList.size() > position) {
            Product product = productList.get(position);
            holder.bind(product, color, position);
        }
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }
    public Product getProduct(int position) {
        if (position >= 0 && position < productList.size()) {
            return productList.get(position);
        }
        return null;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgProduct;
        private TextView tvProductName, tvSize, tvPrice, tvCum, tvColor,idPR,idSl;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.img_Od_Dt);
            tvProductName = itemView.findViewById(R.id.NameProduct_dt);
            tvSize = itemView.findViewById(R.id.tvSize_dt);
            tvColor = itemView.findViewById(R.id.tvColor_dt);
            tvPrice = itemView.findViewById(R.id.priceOrder_dt);
            tvCum = itemView.findViewById(R.id.tvCum_dt);
            idPR = itemView.findViewById(R.id.idPR);
            idSl = itemView.findViewById(R.id.idSL);
        }

        public void bind(Product product, List<ColorProduct> colorPr, int position) {

            tvProductName.setText(product.getName());
            idPR.setText(product.getId());
            idSl.setText(product.getSellerId());

            tvSize.setText(String.format("Cỡ %s", TextUtils.join(", ", product.getSize())));
            if (colorPr != null) {
                ColorProduct colorProduct = product.getListColor().get(position);
                int color = colorProduct.getColor();
                tvColor.setBackgroundColor(color);
            } else {
                tvColor.setBackgroundColor(-10011977);
            }


            tvPrice.setText(product.getPrice() + " VND");

            tvCum.setText(String.format("Số lượng %s", product.getQuantity()));

            if (product.getImgProduct() != null && !product.getImgProduct().isEmpty()) {
                Picasso.get().load(product.getImgProduct().get(0)).into(imgProduct);
            }
        }
    }
}