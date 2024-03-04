package com.example.duantotnghiep.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.example.duantotnghiep.R;
import com.example.duantotnghiep.adapter.ViewPager2_CartForShop_Adapter;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class OrderOfShopActivity extends AppCompatActivity {
    private TabLayout tablayout;
    private TabItem tab1;
    private TabItem tab2;
    private TabItem tab3;
    private TabItem tab4;
    private TabItem tab5;
    private ViewPager2 viewpagerTablayout;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_of_shop);
        tablayout = (TabLayout) findViewById(R.id.tablayout);
        tab1 = (TabItem) findViewById(R.id.tab1);
        tab2 = (TabItem) findViewById(R.id.tab2);
        tab3 = (TabItem) findViewById(R.id.tab3);
        tab4 = (TabItem) findViewById(R.id.tab4);
        tab5 = (TabItem) findViewById(R.id.tab5);
        viewpagerTablayout = (ViewPager2) findViewById(R.id.viewpager_tablayout);
        ViewPager2_CartForShop_Adapter viewPager2_booking_adapter = new ViewPager2_CartForShop_Adapter(OrderOfShopActivity.this);
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