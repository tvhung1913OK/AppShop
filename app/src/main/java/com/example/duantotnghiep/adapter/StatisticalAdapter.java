package com.example.duantotnghiep.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duantotnghiep.R;
import com.example.duantotnghiep.model.Statistical;
import com.squareup.picasso.Picasso;

import java.util.List;

public class StatisticalAdapter extends RecyclerView.Adapter<StatisticalAdapter.StatisticalViewhoder>{
    private Context context;
    private List<Statistical> list;

    public StatisticalAdapter(Context context, List<Statistical> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public StatisticalViewhoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_statistical,parent,false);
        return new StatisticalAdapter.StatisticalViewhoder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatisticalViewhoder holder, int position) {
            Statistical statistical = list.get(position);
            if (statistical==null){
                return;
            }
        if (statistical.getImgProduct() != null && !statistical.getImgProduct().isEmpty()) {
            Uri imageUri = Uri.parse(statistical.getImgProduct());
            Picasso.get()
                    .load(imageUri)
                    .placeholder(R.drawable.tnf)
                    .error(R.drawable.tnf)
                    .into(holder.imgProduct);
        } else {
            holder.imgProduct.setImageResource(R.drawable.pant);
        }
            holder.nameProduct.setText("Tên : "+statistical.getNameProduct());
            holder.totalQuantity.setText("Số lượng bán : "+String.valueOf(statistical.getTotalQuantity()));
            holder.totalAmount.setText("Tổng tiền : "+String.valueOf(statistical.getTotalAmount()));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class StatisticalViewhoder extends RecyclerView.ViewHolder{
        private ImageView imgProduct;
        private TextView nameProduct,totalQuantity,totalAmount;


        public StatisticalViewhoder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            nameProduct=itemView.findViewById(R.id.NameProduct);
            totalQuantity = itemView.findViewById(R.id.totalQuantity);
            totalAmount = itemView.findViewById(R.id.totalAmount);
        }
    }
}
