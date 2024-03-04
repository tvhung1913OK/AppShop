package com.example.duantotnghiep.adapter;

import static java.security.AccessController.getContext;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duantotnghiep.R;
import com.example.duantotnghiep.model.AddProductToCart;
import com.example.duantotnghiep.model.Discount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartOrderAdapter extends RecyclerView.Adapter<CartOrderAdapter.ViewHolder> {
    private List<AddProductToCart> selectedProducts;
    private List<Integer> colorList;
    private List<List<Discount>> productDiscounts;
    private boolean isDiscountAppliedForProduct = false;

    double discountedPrice = 0;
    private double totalSelectedDiscounts = 0;
    double selectedDiscountTotal =0;

    private Map<String, Boolean> discountStateMap;
    private Map<String, String> productNotes;
    public interface DiscountUpdateListener {
        void onDiscountUpdated(double totalSelectedDiscounts);
    }
    private Map<String, Double> discountedPrices;
    public List<AddProductToCart> getSelectedProducts() {
        return selectedProducts;
    }
    public CartOrderAdapter() {
        discountStateMap = new HashMap<>();
        productNotes = new HashMap<>();
        discountedPrices = new HashMap<>();
    }
    private DiscountUpdateListener discountUpdateListener;
    public void setDiscountUpdateListener(DiscountUpdateListener listener) {
        this.discountUpdateListener = listener;
    }

    public Map<String, String> getProductNotes() {
        return productNotes;
    }



    @NonNull
    @Override
    public CartOrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_to_order, parent, false);
        return new CartOrderAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartOrderAdapter.ViewHolder holder, int position) {
        AddProductToCart product = selectedProducts.get(position);
        holder.bind(product, colorList, position);
        holder.calculateDiscountedPrice();

//        holder.edtNoteCart.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                String note = charSequence.toString();
//                productNotes.put(product.getId(), note);
//                Log.d("CartOrderAdapter", "Product ID: " + product.getId() + ", Note: " + note+", productNotes"+productNotes);
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//            }
//        });
    }
    public void updateSelectedProducts(List<AddProductToCart> selectedProducts) {
        this.selectedProducts = selectedProducts;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return selectedProducts.size();
    }

    public double calculateTotalPrice() {
        double totalPrice = 0;
        if (selectedProducts != null) {
            for (AddProductToCart product : selectedProducts) {
                totalPrice += product.getPricetotal_product() * product.getQuantity_product();
            }
        }

        return totalPrice;
    }
    public double getDiscountedPrice(String productId) {
        return discountedPrices.get(productId) != null ? discountedPrices.get(productId) : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgProduct;
        private TextView tvProductName, tvSize, tvPrice, tvCum, tvColor, idPR, idSl, tvColor_dt_Cart, SlProductCart, TotalPriceOneCart, ValueVoucher;
        private double totalPrice;
        LinearLayout btnShowDiscount;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            imgProduct = itemView.findViewById(R.id.img_Od_Dt_Cart);
            tvProductName = itemView.findViewById(R.id.NameProduct_Cart);
            tvSize = itemView.findViewById(R.id.tvSize_dt_Cart);
            tvColor = itemView.findViewById(R.id.tvColor_dt_Cart);
            tvPrice = itemView.findViewById(R.id.priceOrder_dt_Cart);
            tvCum = itemView.findViewById(R.id.tvCum_dt_Cart);
            ValueVoucher = itemView.findViewById(R.id.ValueVoucher);
            idPR = itemView.findViewById(R.id.idPR_Cart);
            tvColor_dt_Cart = itemView.findViewById(R.id.tvColor_dt_Cart);
            idSl = itemView.findViewById(R.id.idSL_Cart);
            SlProductCart = itemView.findViewById(R.id.SlProductCart);
            btnShowDiscount = itemView.findViewById(R.id.btnDiscountCart);
            TotalPriceOneCart = itemView.findViewById(R.id.TotalPriceOneCart);
            totalPrice = 0;
        }

        public void bind(AddProductToCart product, List<Integer> colorList, int position) {
            SlProductCart.setText("(" + product.getQuantity_product() + " sản phẩm)");
            tvProductName.setText(product.getName_product());

            idPR.setText(product.getId());
            idSl.setText(product.getId_user());
            tvColor_dt_Cart.setBackgroundColor(product.getColor_product());
            tvSize.setText(String.format("Cỡ %s", TextUtils.join(", ", Collections.singleton(product.getSize_product()))));

            if (colorList != null && colorList.size() > position) {
                int color = colorList.get(position);
                tvColor.setBackgroundColor(color);
            }

            tvPrice.setText("$ " + product.getPricetotal_product());

            tvCum.setText(String.format("Số lượng %s", product.getQuantity_product()));

            double totalPrice = selectedProducts.get(getAdapterPosition()).getPricetotal_product() * selectedProducts.get(getAdapterPosition()).getQuantity_product();
            DecimalFormat decimalFormat = new DecimalFormat("#,##0");
            String formattedDiscountedPrice = decimalFormat.format(totalPrice);
            TotalPriceOneCart.setText("$ " + formattedDiscountedPrice);

            if (product.getImage_product() != null && !product.getImage_product().isEmpty()) {
                Picasso.get().load(product.getImage_product()).into(imgProduct);
            }
            btnShowDiscount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadDiscountsFromFirebase(product.getId());
                }
            });
        }
        private void calculateDiscountedPrice() {
            double originalTotalPrice = selectedProducts.get(getAdapterPosition()).getPricetotal_product() * selectedProducts.get(getAdapterPosition()).getQuantity_product();

            if (currentDiscount != null) {

                double totalPrice = originalTotalPrice - currentDiscount.getAmount();
                discountedPrices.put(selectedProducts.get(getAdapterPosition()).getId(), totalPrice);

                DecimalFormat decimalFormat = new DecimalFormat("#,##0");
                String formattedDiscountedPrice = decimalFormat.format(totalPrice);
                TotalPriceOneCart.setText("$ " + formattedDiscountedPrice);
            } else {

                discountedPrices.put(selectedProducts.get(getAdapterPosition()).getId(), originalTotalPrice);

                DecimalFormat decimalFormat = new DecimalFormat("#,##0");
                String formattedOriginalPrice = decimalFormat.format(originalTotalPrice);
                TotalPriceOneCart.setText("$ " + formattedOriginalPrice);
            }
        }






        private void loadDiscountsFromFirebase(String productId) {
            DatabaseReference discountsRef = FirebaseDatabase.getInstance().getReference("products/" + productId + "/selectedDiscounts");

            discountsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<Discount> discounts = new ArrayList<>();

                    for (DataSnapshot discountSnapshot : dataSnapshot.getChildren()) {
                        Discount discount = discountSnapshot.getValue(Discount.class);
                        discounts.add(discount);
                    }


                    showDiscountDialog(productId, discounts);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                    Toast.makeText(itemView.getContext(), "Error loading discounts", Toast.LENGTH_SHORT).show();
                }
            });
        }



        private void showDiscountDialog(String productId,List<Discount> discounts) {
            AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
            builder.setTitle("Danh sách mã giảm giá");

            if (discounts != null && !discounts.isEmpty()) {
                final String[] discountCodes = new String[discounts.size()];

                for (int i = 0; i < discounts.size(); i++) {
                    Discount discount = discounts.get(i);
                    discountCodes[i] = "Mã giảm giá: " + discount.getCode() + "\n" +
                            "Số tiền giảm: $" + discount.getAmount() + "\n\n";
                }

                builder.setItems(discountCodes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        toggleDiscountState(productId, discounts.get(which));
                    }
                });
            } else {
                builder.setMessage("Không có mã giảm giá nào.");
            }

            builder.setPositiveButton("Đóng", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });

            builder.show();
        }
        private Discount currentDiscount;

        private void toggleDiscountState(String productId, Discount discount) {
            Boolean isDiscountApplied = discountStateMap.get(productId);

            if (isDiscountApplied != null && isDiscountApplied) {

                removeDiscount(productId, currentDiscount);
            } else {

                applyDiscount(productId, discount);
            }

            Toast.makeText(itemView.getContext(), String.valueOf(totalSelectedDiscounts), Toast.LENGTH_SHORT).show();
            if (discountUpdateListener != null) {
                discountUpdateListener.onDiscountUpdated(totalSelectedDiscounts);
            }
        }



        private void removeDiscount(String productId, Discount discount) {
            discountStateMap.put(productId, false);
            totalSelectedDiscounts -= discount.getAmount();


            double originalTotalPrice = selectedProducts.get(getAdapterPosition()).getPricetotal_product() * selectedProducts.get(getAdapterPosition()).getQuantity_product();
            DecimalFormat decimalFormat = new DecimalFormat("#,##0");
            String formattedDiscountedPrice = decimalFormat.format(originalTotalPrice);

            TotalPriceOneCart.setText("$ " + formattedDiscountedPrice);

            ValueVoucher.setText("");
            currentDiscount = null;

            calculateDiscountedPrice();
        }

        private void applyDiscount(String productId, Discount discount) {
            Boolean isDiscountApplied = discountStateMap.get(productId);

            if (isDiscountApplied != null && isDiscountApplied) {

                Toast.makeText(itemView.getContext(), "Mã giảm giá đã được áp dụng", Toast.LENGTH_SHORT).show();
            } else {

                discountStateMap.put(productId, true);
                totalSelectedDiscounts += discount.getAmount();

                if (totalSelectedDiscounts <= discount.getAmount()) {
                    totalPrice -= discount.getAmount();
                }


                Log.d("Discount", "Áp dụng giảm giá: " + discount.getAmount());

                Drawable discountIcon = createDiscountIcon(discount.getAmount());
                String displayText = formatAmount(discount.getAmount());
                SpannableStringBuilder builder = new SpannableStringBuilder(" ");
                discountIcon.setBounds(0, 0, discountIcon.getIntrinsicWidth(), discountIcon.getIntrinsicHeight());
                ImageSpan imageSpan = new ImageSpan(discountIcon, ImageSpan.ALIGN_BOTTOM);
                builder.setSpan(imageSpan, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                ValueVoucher.setText(builder);

                currentDiscount = discount;
//                Log.d("totalSelectedDiscounts","Total"+totalSelectedDiscounts);
                notifyDataSetChanged();
            }
        }











        private Drawable createDiscountIcon(double discountAmount) {
            int iconSize = 100;
            Bitmap bitmap = Bitmap.createBitmap(iconSize, iconSize, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);

            int resourceId = R.drawable.voucher;
            Drawable background = itemView.getResources().getDrawable(resourceId);
            background.setBounds(0, 0, iconSize, iconSize);
            background.draw(canvas);

            Paint paint = new Paint();
            paint.setColor(Color.parseColor("#FE5E00"));
            paint.setTextSize(35);
            paint.setTextAlign(Paint.Align.CENTER);

            String formattedAmount = formatAmount(discountAmount);


            float xPos = iconSize / 2f + 10;


            float yPos = (canvas.getHeight() / 2f) - ((paint.descent() + paint.ascent()) / 2f);

            canvas.drawText(formattedAmount, xPos, yPos, paint);

            return new BitmapDrawable(itemView.getResources(), bitmap);
        }




        private String formatAmount(double amount) {
            String formattedAmount;

            if (amount < 1000) {

                formattedAmount = String.format("%.0f", amount);
            } else if (amount < 1000000) {

                formattedAmount = amount % 1000 == 0 ? String.format("%.0fK", amount / 1000.0) : String.format("%.1fK", amount / 1000.0);
            } else {

                formattedAmount = amount % 1000000 == 0 ? String.format("%.0fM", amount / 1000000.0) : String.format("%.1fM", amount / 1000000.0);
            }

            return formattedAmount;
        }


    }

}