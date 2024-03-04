package com.example.duantotnghiep.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duantotnghiep.R;
import com.example.duantotnghiep.model.Location;

import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.AddressViewHolder>{
    private Context context;
    private List<Location> list;
    private Callback callback;

    public LocationAdapter(Context context, List<Location> list, Callback callback) {
        this.context = context;
        this.list = list;
        this.callback = callback;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_location, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        Location address = list.get(position);
        if (address == null){
            return;
        }

        holder.tvName.setText(address.getName());
        holder.tvPhone.setText(address.getPhone());
        holder.tvLocation.setText(address.getLocation());
        holder.imgEditAddress.setOnClickListener(view -> {
            callback.editAddress(address);
        });
        holder.imgDeleteAddress.setOnClickListener(view -> {
            callback.deleteAddress(address);
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class AddressViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private TextView tvPhone;
        private TextView tvLocation;
        private ImageView imgEditAddress;
        private ImageView imgDeleteAddress;
        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvPhone = (TextView) itemView.findViewById(R.id.tv_phone);
            tvLocation = (TextView) itemView.findViewById(R.id.tv_location);
            imgEditAddress = (ImageView) itemView.findViewById(R.id.img_editAddress);
            imgDeleteAddress = (ImageView) itemView.findViewById(R.id.img_deleteAddress);
        }
    }

    public interface Callback{
        void editAddress(Location address);
        void deleteAddress(Location address);
    }
}
