package com.example.duantotnghiep.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.duantotnghiep.fragment.InStockFragment;
import com.example.duantotnghiep.fragment.OutStockFragment;


public class ManagerSellerAdapter extends FragmentPagerAdapter {
    private Context context;
    int totalTab;

    public ManagerSellerAdapter(@NonNull FragmentManager fm, Context context, int totalTab) {
        super(fm);
        this.context = context;
        this.totalTab = totalTab;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                InStockFragment inStockFragment = new InStockFragment();
                return inStockFragment;
            case 1:
                OutStockFragment outStockFragment = new OutStockFragment();
                return outStockFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTab;
    }
}
