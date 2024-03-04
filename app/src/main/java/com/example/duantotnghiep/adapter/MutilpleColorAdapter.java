package com.example.duantotnghiep.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duantotnghiep.R;
import com.example.duantotnghiep.databinding.CircleColorBinding;
import com.example.duantotnghiep.model.ColorProduct;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class MutilpleColorAdapter extends RecyclerView.Adapter<MutilpleColorAdapter.ViewHolder> {
    private List<ColorProduct> colorProductList;
    private List<String> selectedSize = new ArrayList<>();

    public MutilpleColorAdapter() {
        this.colorProductList = new ArrayList<>();
    }
    public MutilpleColorAdapter(List<ColorProduct> colorProductList) {
        this.colorProductList = colorProductList;
    }

    public void setColorList(List<ColorProduct> colorProductList) {
        this.colorProductList = colorProductList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext(); // Get the Context
        CircleColorBinding binding = CircleColorBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding, context);
    }
    public void updateColor(int color) {
//        if (colorList.contains(color)) {
//
//            colorList.remove(colorList.get(color));
//        } else {
//
//            colorList.add(color);
//        }
        notifyDataSetChanged();
    }





    @Override

    public void onBindViewHolder(@NonNull MutilpleColorAdapter.ViewHolder holder, int position) {
        ColorProduct colorProduct = colorProductList.get(position);

        holder.bind(colorProduct);
    }


    @Override

    public int getItemCount() {
        return colorProductList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private final CircleColorBinding binding;
        private final ImageView colorImageView;
        private final Context context;
        public ViewHolder(CircleColorBinding binding, Context context) {
            super(binding.getRoot());
            this.binding = binding;
            this.context = context;
            colorImageView = binding.colorImageView1;
        }


        public void bind(ColorProduct colorProduct) {

            GradientDrawable circle = new GradientDrawable();
            circle.setShape(GradientDrawable.OVAL);
            circle.setColor(colorProduct.getColor());

            LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{context.getResources().getDrawable(R.drawable.circle_background), circle});

            final TextInputEditText[] quantityEdit = {
                    binding.quantity1, binding.quantity2, binding.quantity3, binding.quantity4, binding.quantity5,
                    binding.quantity6, binding.quantity7, binding.quantity8, binding.quantity9
            };
            final TextView[] titleText = {
                    binding.title1, binding.title2, binding.title3, binding.title4, binding.title5, binding.title6,
                    binding.title7, binding.title8, binding.title9
            };

            colorProductList.get(getAdapterPosition()).setQuantity(new ArrayList<>());
            for(int i = 0; i < quantityEdit.length; i++) {
                if(i >= selectedSize.size()) {
                    titleText[i].setVisibility(View.INVISIBLE);
                    quantityEdit[i].setVisibility(View.INVISIBLE);
                    continue;
                }
                titleText[i].setVisibility(View.VISIBLE);
                quantityEdit[i].setVisibility(View.VISIBLE);

                final int _i = i;
                colorProductList.get(getAdapterPosition()).getQuantity().add(0);
                TextInputEditText edit = quantityEdit[i];
                edit.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (!editable.toString().isEmpty()) {
                            Integer n = Integer.parseInt(editable.toString());
                            if(n == null)
                                n = 0;
                            colorProductList.get(getAdapterPosition()).getQuantity().set(_i, n);
                        }
                    }
                });
            }

            for(int i = 0; i < selectedSize.size(); i++) {
                quantityEdit[i].setText(String.valueOf(colorProduct.getQuantity().get(i)));
                titleText[i].setText(selectedSize.get(i));
            }

            colorImageView.setBackground(layerDrawable);
        }

    }
    public void updateSelectedColors(List<ColorProduct> selectedColorProducts, List<String> selectedSize) {
        Log.d("_LVMINH_", "updateSelectedColors " + selectedColorProducts.size() + " " + selectedSize.size());
        if (selectedColorProducts != null) {
            colorProductList.clear();
            colorProductList.addAll(selectedColorProducts);
            this.selectedSize.clear();
            this.selectedSize.addAll(selectedSize);

        }
        notifyDataSetChanged();
    }
    public List<ColorProduct> getSelectedColors() {
        return colorProductList;
    }



}
