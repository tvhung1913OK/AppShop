package com.example.duantotnghiep.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.duantotnghiep.fragment.CancleForShopFragment;
import com.example.duantotnghiep.fragment.CancleFragment;
import com.example.duantotnghiep.fragment.ConfirmForShopFragment;
import com.example.duantotnghiep.fragment.ConfirmFragment;
import com.example.duantotnghiep.fragment.DeliverForShopFragment;
import com.example.duantotnghiep.fragment.DeliverFragment;
import com.example.duantotnghiep.fragment.DoneForShopFragment;
import com.example.duantotnghiep.fragment.DoneFragment;
import com.example.duantotnghiep.fragment.WaitForShopFragment;
import com.example.duantotnghiep.fragment.WaitFragment;

public class ViewPager2_CartForShop_Adapter extends FragmentStateAdapter {
    public ViewPager2_CartForShop_Adapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = null;
        switch (position){
            case 0:
                fragment = new WaitForShopFragment().newInstance();
                break;
            case 1:
                fragment = new ConfirmForShopFragment().newInstance();
                break;
            case 2:
                fragment = new DeliverForShopFragment().newInstance();
                break;
            case 3:
                fragment = new DoneForShopFragment().newInstance();
                break;
            case 4:
                fragment = new CancleForShopFragment().newInstance();
                break;
            default:
                fragment = new WaitForShopFragment().newInstance();
        }
        return fragment ;
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
