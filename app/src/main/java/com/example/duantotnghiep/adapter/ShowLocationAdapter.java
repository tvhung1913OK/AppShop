package com.example.duantotnghiep.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duantotnghiep.R;
import com.example.duantotnghiep.model.Location;

import java.util.List;

public class ShowLocationAdapter extends RecyclerView.Adapter<ShowLocationAdapter.LocationViewHolder>{
    private Context context;
    private List<Location> list;

    private Callback callback;

    public ShowLocationAdapter(Context context, List<Location> list, Callback callback) {
        this.context = context;
        this.list = list;
        this.callback = callback;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_show_location, parent, false);
        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        Location location = list.get(position);
        if (location == null){
            return;
        }
        holder.tvName.setText(location.getName());
        holder.tvPhone.setText(location.getPhone());
        holder.tvLocation.setText(location.getLocation());
        holder.cvItemLocation.setOnClickListener(view -> {
            callback.clickItem(location);
        });
    }

    @Override
    public int getItemCount() {
        return list == null? 0 : list.size();
    }

    public class LocationViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private TextView tvPhone;
        private TextView tvLocation;
        private CardView cvItemLocation;
        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvPhone = (TextView) itemView.findViewById(R.id.tv_phone);
            tvLocation = (TextView) itemView.findViewById(R.id.tv_location);
            cvItemLocation = (CardView) itemView.findViewById(R.id.cv_itemLocation);
        }
    }

    public interface Callback{
        void clickItem(Location location);
    }
}

