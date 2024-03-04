package com.example.duantotnghiep.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.duantotnghiep.R;
import com.example.duantotnghiep.model.AddProductToCart;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private Context context;
    private List<AddProductToCart> productsList;

    private Callback callback;
    private Set<AddProductToCart> selectedItems = new HashSet<>();
    TextView tv_totalbill;
    public CartAdapter(Context context, List<AddProductToCart> productsList, Callback callback, TextView tv_totalbill) {
        this.context = context;
        this.productsList = productsList;
        this.callback = callback;
        this.tv_totalbill = tv_totalbill;
        notifyDataSetChanged();
    }
    public Set<AddProductToCart> getSelectedItems() {
        return selectedItems;
    }
    public void selectAllItems() {
        selectedItems.addAll(productsList);
        notifyDataSetChanged();
        updateTotalPrice();
    }

    public void deselectAllItems() {
        selectedItems.clear();
        notifyDataSetChanged();
        updateTotalPrice();
    }

    @NonNull
    @Override
    public CartAdapter.CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart,parent,false);
        return new CartViewHolder(view);
    }
    public List<AddProductToCart> getSelectedItemsList() {
        return new ArrayList<>(selectedItems);
    }


    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        AddProductToCart products = productsList.get(position);

        holder.tvNameProductCart.setText(products.getName_product());
        holder.tvNumProductCart.setText(products.getQuantity_product()+"");
        holder.tvPriceProductCart.setText(String.format(Locale.getDefault(), "%.0f", products.getPricetotal_product()));
        double totalPrice = products.getQuantity_product() * products.getPricetotal_product();
        String totalPriceFormatted = String.format(Locale.getDefault(), "Thành tiền: %.0f", totalPrice);
        holder.priceAllQuantity.setText(totalPriceFormatted);
        holder.tvColor.setBackgroundColor(products.getColor_product());
        holder.tvSize.setText(products.getSize_product());
        if (products.getImage_product() != null && !products.getImage_product().isEmpty()) {
            Uri imageUri = Uri.parse(products.getImage_product());
            Picasso.get()
                    .load(imageUri)
                    .placeholder(R.drawable.tnf)
                    .error(R.drawable.tnf)
                    .into(holder.imgProductCart);
        } else {
            holder.imgProductCart.setImageResource(R.drawable.tnf);
        }
        holder.imgDeleteProductCart.setOnClickListener(view -> {
            callback.deleteItemCart(products);
        });
        holder.cvLickitem.setOnClickListener(view -> {
            callback.updateItemCart(products);
        });
        holder.checkboxOrder.setChecked(selectedItems.contains(products));
        holder.checkboxOrder.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedItems.add(products);
            } else {
                selectedItems.remove(products);
            }
            updateTotalPrice();
        });
    }
    private void updateTotalPrice() {
        double selectedTotalPrice = 0.0;
        for (AddProductToCart product : selectedItems) {
            selectedTotalPrice += product.getPricetotal_product() * product.getQuantity_product();
        }
        tv_totalbill.setText(String.valueOf(selectedTotalPrice));
    }

    @Override
    public int getItemCount() {
        return productsList == null ? 0 : productsList.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        private ShapeableImageView imgProductCart;
        private TextView tvNameProductCart;
        private TextView tvPriceProductCart;
        private TextView tvNumProductCart;
        private ImageView tvColor;
        private TextView tvSize;
        private CardView cvLickitem;
        private ImageView imgDeleteProductCart;
        private TextView priceAllQuantity;

        CheckBox checkboxOrder;
        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            cvLickitem = (CardView) itemView.findViewById(R.id.cv_lickitem);
            imgProductCart = (ShapeableImageView) itemView.findViewById(R.id.img_productCart);
            tvNameProductCart = (TextView) itemView.findViewById(R.id.tv_nameProductCart);
            tvPriceProductCart = (TextView) itemView.findViewById(R.id.tv_priceProductCart);
            tvNumProductCart = (TextView) itemView.findViewById(R.id.tv_numProductCart);
            imgDeleteProductCart = (ImageView) itemView.findViewById(R.id.img_deleteProductCart);
            tvColor = (ImageView) itemView.findViewById(R.id.tv_colorProductCart);
            tvSize = (TextView) itemView.findViewById(R.id.tv_sizeProductCart);
            priceAllQuantity = (TextView) itemView.findViewById(R.id.priceAllQuantity);
            checkboxOrder = itemView.findViewById(R.id.checkboxOrder);
        }
    }
    public interface Callback{
        void deleteItemCart(AddProductToCart products);
        void updateItemCart(AddProductToCart products);
    }
}
