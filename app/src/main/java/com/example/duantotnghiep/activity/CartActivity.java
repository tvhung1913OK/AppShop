package com.example.duantotnghiep.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.duantotnghiep.R;
import com.example.duantotnghiep.adapter.CartAdapter;
import com.example.duantotnghiep.fragment.CartToOrderFragment;
import com.example.duantotnghiep.model.AddProductToCart;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CartActivity extends AppCompatActivity implements CartAdapter.Callback {
    private RecyclerView recyclerViewCart;
    private TextView total_item,totalPriceCart, noResultsTextView;
    private CartAdapter cartAdapter;
    private AddProductToCart product;
    private List<AddProductToCart> list = new ArrayList<>();
    private FirebaseUser firebaseUser;
    CheckBox checkboxAllItem;

    Button addOrderDetail;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        recyclerViewCart = findViewById(R.id.rcv_cart);
        total_item = findViewById(R.id.total_item);
        checkboxAllItem = findViewById(R.id.checkboxAllItem);
        addOrderDetail = findViewById(R.id.addOrderDetail);
        totalPriceCart = findViewById(R.id.total_price_cart);
        noResultsTextView = findViewById(R.id.noResults);
        recyclerViewCart.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        getListProductAddCart();
        cartAdapter = new CartAdapter(CartActivity.this, list, this, totalPriceCart);
        recyclerViewCart.setAdapter(cartAdapter);
        addOrderDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<AddProductToCart> selectedProducts = cartAdapter.getSelectedItemsList();

                if (selectedProducts.isEmpty()) {

                    Toast.makeText(CartActivity.this, "Xin hãy vui lòng chọn sản phẩm bạn muốn mua", Toast.LENGTH_SHORT).show();
                } else {

                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("selectedProducts", new ArrayList<>(selectedProducts));

                    CartToOrderFragment cartToOrderFragment = new CartToOrderFragment();
                    cartToOrderFragment.setArguments(bundle);

                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, cartToOrderFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }
        });

        checkboxAllItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cartAdapter.selectAllItems();
                } else {
                    cartAdapter.deselectAllItems();
                }
            }
        });

    }
    private void getListProductAddCart() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String id_user = firebaseUser.getUid();
        DatabaseReference myReference = firebaseDatabase.getReference("cart").child(id_user);
        myReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (list != null) {
                    list.clear();
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    product = dataSnapshot.getValue(AddProductToCart.class);
                    list.add(product);
                }
                cartAdapter.notifyDataSetChanged();
                Log.d("=====", "onDataChange: "+snapshot+"  data   "+ myReference);
                if (list.isEmpty()) {
                    noResultsTextView.setVisibility(View.VISIBLE);
                    recyclerViewCart.setVisibility(View.GONE);
                } else {
                    noResultsTextView.setVisibility(View.GONE);
                    recyclerViewCart.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Get list users faild", Toast.LENGTH_SHORT).show();
                Log.d("LISTCART", "onCancelled: " + error.getMessage());
            }
        });
    }
    @Override
    public void deleteItemCart(AddProductToCart products) {
        AlertDialog.Builder aBuilder = new AlertDialog.Builder(CartActivity.this);
        aBuilder.setTitle("Xóa sản phẩm trong giỏ hàng");
        aBuilder.setMessage("Bạn có chắc chắn muốn xóa " + products.getName_product() + " không?");
        aBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                String id_user = firebaseUser.getUid();
                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("cart").child(id_user).child(products.getId());
                myRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
                dialogInterface.dismiss();
            }
        });

        aBuilder.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alertDialog = aBuilder.create();
        alertDialog.show();

    }

    @Override
    public void updateItemCart(AddProductToCart products) {

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}