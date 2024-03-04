package com.example.duantotnghiep.adapter;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duantotnghiep.R;
import com.example.duantotnghiep.model.InfoProductOrder;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductOrderAdapter extends RecyclerView.Adapter<ProductOrderAdapter.ProductViewHolder> {
    private Context context;
    private List<InfoProductOrder> productList;
    public ProductOrderAdapter(Context context, List<InfoProductOrder> productList) {
        this.context = context;
        this.productList = productList;
    }
    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_shop, parent, false);
        return new ProductViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        InfoProductOrder productOrder = productList.get(position);

        holder.tvNameProduct.setText(productOrder.getNamePr());
        holder.colorProduct.setBackgroundColor(productOrder.getColorPr());
        holder.tvQuantity.setText(String.valueOf(productOrder.getQuantityPr()));
        holder.tvTotal.setText(String.valueOf(productOrder.getPrice()));
        holder.tv_size.setText("Size : " + productOrder.getSize());

        if (productOrder.getImgPr() != null && !productOrder.getImgPr().isEmpty()) {
            Uri imageUri = Uri.parse(productOrder.getImgPr());
            Picasso.get()
                    .load(imageUri)
                    .placeholder(R.drawable.pant)
                    .error(R.drawable.pant)
                    .into(holder.imgProduct);
        } else {
            holder.imgProduct.setImageResource(R.drawable.pant);
        }
    }
    @Override
    public int getItemCount() {
        return productList.size();
    }
    public void setProductList(List<InfoProductOrder> productList) {
        this.productList = productList;
        notifyDataSetChanged();

        for (InfoProductOrder product : productList) {
            Log.d("Product", "Name: " + product.getNamePr() + ", Price: " + product.getPrice());
        }
    }
    public class ProductViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgProduct, colorProduct;
        private TextView tvNameProduct;
        private TextView tvQuantity;
        private TextView tvTotal;
        private TextView tv_size;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = (ImageView) itemView.findViewById(R.id.img_product);
            colorProduct = (ImageView) itemView.findViewById(R.id.tv_color);
            tvNameProduct = (TextView) itemView.findViewById(R.id.tv_nameProduct);
            tvQuantity = (TextView) itemView.findViewById(R.id.tv_quantity);
            tvTotal = (TextView) itemView.findViewById(R.id.tv_total);
            tv_size = (TextView) itemView.findViewById(R.id.tv_size);
        }
    }
}