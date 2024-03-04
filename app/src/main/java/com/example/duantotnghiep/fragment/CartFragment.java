package com.example.duantotnghiep.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.duantotnghiep.R;
import com.example.duantotnghiep.adapter.ViewPager2_Cart_Adapter;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class CartFragment extends Fragment {
    private TabLayout tablayout;
    private TabItem tab1;
    private TabItem tab2;
    private TabItem tab3;
    private TabItem tab4;
    private TabItem tab5;
    private ViewPager2 viewpagerTablayout;

    public CartFragment() {
        // Required empty public constructor
    }

    public static CartFragment newInstance(String param1, String param2) {
        CartFragment fragment = new CartFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tablayout = (TabLayout) view.findViewById(R.id.tablayout);
        tab1 = (TabItem) view.findViewById(R.id.tab1);
        tab2 = (TabItem) view.findViewById(R.id.tab2);
        tab3 = (TabItem) view.findViewById(R.id.tab3);
        tab4 = (TabItem) view.findViewById(R.id.tab4);
        tab5 = (TabItem) view.findViewById(R.id.tab5);
        viewpagerTablayout = (ViewPager2) view.findViewById(R.id.viewpager_tablayout);
        ViewPager2_Cart_Adapter viewPager2_booking_adapter = new ViewPager2_Cart_Adapter(getActivity());
        viewpagerTablayout.setAdapter(viewPager2_booking_adapter);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tablayout, viewpagerTablayout, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText("Đang chờ");
                        break;
                    case 1:
                        tab.setText("Đã Xác Nhận");
                        break;
                    case 2:
                        tab.setText("Đang giao");
                        break;
                    case 3:
                        tab.setText("Đã hoàn thành");
                        break;
                    case 4:
                        tab.setText("Đã huỷ");
                        break;
                }
            }
        });
        tabLayoutMediator.attach();
    }
}