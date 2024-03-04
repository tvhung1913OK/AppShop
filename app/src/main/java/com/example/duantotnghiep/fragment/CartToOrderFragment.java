package com.example.duantotnghiep.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duantotnghiep.MainActivity;
import com.example.duantotnghiep.R;
import com.example.duantotnghiep.activity.ShowListLocationActivity;
import com.example.duantotnghiep.activity.TopUpCardActivity;
import com.example.duantotnghiep.adapter.CartOrderAdapter;
import com.example.duantotnghiep.model.AddProductToCart;
import com.example.duantotnghiep.model.InfoProductOrder;
import com.example.duantotnghiep.model.Order;
import com.example.duantotnghiep.model.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CartToOrderFragment extends Fragment implements CartOrderAdapter.DiscountUpdateListener {
    private List<AddProductToCart> selectedProducts;
    private RecyclerView recyclerViewOrder;
    private CartOrderAdapter orderAdapter;
    FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
    private String buyerUsername;
    DatabaseReference userRef, productRef, notificationRef;
    private double TotalPlusShip = 0;
    double TotalPay = 0;
    LinearLayout btn_addLocation_Cart, PaymentMethods;
    TextView total_price_cart, txtPhone, txtAddress, txtName, lineCart, txtPayment_Cart, txtAllVoucherCart, txtMoneyCart, txtSubtotalCart, txtTotalCart;
    double totalPrice;
    private double totalDiscount = 0;
    Button addOrderDetailCart;
    EditText edtNoteCart;
    private boolean finalPaid;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_cart_to_order, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            selectedProducts = bundle.getParcelableArrayList("selectedProducts");
            orderAdapter = new CartOrderAdapter();
            showSelectedProducts(selectedProducts);
        }
        recyclerViewOrder = root.findViewById(R.id.rcvOrder_cart);
        total_price_cart = root.findViewById(R.id.total_price_Allcart);
        btn_addLocation_Cart = root.findViewById(R.id.btn_addLocation_Cart);
        txtPhone = root.findViewById(R.id.tvPhoneCart);
        addOrderDetailCart = root.findViewById(R.id.addOrderDetailCart);
        txtAddress = root.findViewById(R.id.tvAddressCart);
        txtTotalCart = root.findViewById(R.id.txtTotalCart);
        txtMoneyCart = root.findViewById(R.id.txtMoneyCart);
        txtSubtotalCart = root.findViewById(R.id.txtSubtotalCart);
        txtName = root.findViewById(R.id.tvNameCart);
        PaymentMethods = root.findViewById(R.id.PaymentMethods);
        txtAllVoucherCart = root.findViewById(R.id.txtVoucherCart);
        lineCart = root.findViewById(R.id.lineCart);
        txtPayment_Cart = root.findViewById(R.id.txtPayment_Cart);
        txtSubtotalCart.setText(String.format(formatPrice(totalPrice)));
        edtNoteCart = root.findViewById(R.id.edtNoteCart);

        orderAdapter.setDiscountUpdateListener(this);
        recyclerViewOrder.setLayoutManager(new LinearLayoutManager(root.getContext()));

        recyclerViewOrder.setAdapter(orderAdapter);
        txtName.setVisibility(View.INVISIBLE);
        txtAddress.setVisibility(View.INVISIBLE);
        txtPhone.setVisibility(View.INVISIBLE);
        lineCart.setVisibility(View.INVISIBLE);

        TotalPlusShip = totalPrice + 35000;
        txtTotalCart.setText(String.format(formatPrice(TotalPlusShip)));
        total_price_cart.setText(String.format(formatPrice(TotalPlusShip) + "VND"));
        mAuth = FirebaseAuth.getInstance();
        getUsernameFromFirebase();
        poppuGetListPayment();

        showWallet();
        btn_addLocation_Cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDiaLogAddress();
            }
        });
//        Log.d("sa", "productNotes" + productNotes);
        addOrderDetailCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.d("sa", "productNotes" + productNotes);
                checkWalletBalanceBeforeOrder();

            }
        });
        return root;
    }

    private void CreateOrder() {
        if (txtPayment_Cart.getText().toString().equalsIgnoreCase("Payment methods")) {
            Toast.makeText(getContext(), "Vui lòng chọn phương thức thanh toán", Toast.LENGTH_SHORT).show();
            return;
        }
        if (txtPayment_Cart.getText().toString().equals("Pay with wallet")) {
            updateWalletAfterOrder();
        }
        if (TextUtils.isEmpty(txtAddress.getText().toString()) || TextUtils.isEmpty(txtPhone.getText().toString())) {
            Toast.makeText(getContext(), "Vui lòng chọn địa chỉ", Toast.LENGTH_SHORT).show();
            return;
        }

        String idBuyer = firebaseUser.getUid();
        String address = txtAddress.getText().toString();
        String numberPhone = txtPhone.getText().toString();
        Boolean paid = txtPayment_Cart.getText().toString().equals("Pay with wallet");

        String idSeller = "ZYA1yQdRAYSzh1K24ZVYIYvHIc92";
        List<InfoProductOrder> productList = new ArrayList<>();

        for (AddProductToCart product : selectedProducts) {
            String productId = product.getId();
            String note = edtNoteCart.getText().toString();
            DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("products").child(productId);
            productRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Product associatedProduct = dataSnapshot.getValue(Product.class);
                        String date = getCurrentDate();
                        String status = "Waitting";
                        if (product.getQuantity_product() <= associatedProduct.getQuantity()) {
                            List<InfoProductOrder> list = new ArrayList<>();
                            InfoProductOrder infoPr = new InfoProductOrder(product.getId(),
                                    product.getImage_product(),
                                    product.getName_product(),
                                    product.getColor_product(),
                                    product.getPricetotal_product(),
                                    product.getSize_product(),
                                    product.getQuantity_product());
                            productList.add(infoPr);

                            if (productList.size() == selectedProducts.size()) {
                                String orderId = userRef.child("list_order").push().getKey();
                                userRef.child("user").child(idBuyer).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            String id = firebaseUser.getUid();
                                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("user").child(id);
                                            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()) {
                                                        String customerName = dataSnapshot.child("username").getValue(String.class);
                                                        String customerImage = dataSnapshot.child("img").getValue(String.class);
                                                        int totalQuantity = 0;
                                                        for (AddProductToCart product : selectedProducts) {
                                                            totalQuantity += product.getQuantity_product();
                                                        }

                                                        Order order = new Order(
                                                                orderId,
                                                                idBuyer,
                                                                idSeller,
                                                                TotalPay,
                                                                address,
                                                                numberPhone,
                                                                paid,
                                                                status,
                                                                note,
                                                                date,
                                                                productList,
                                                                customerName,
                                                                customerImage
                                                        );
                                                        order.setTotalQuantity(totalQuantity);
                                                        notifications();
                                                        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("list_order").push();
                                                        String orderId = orderRef.getKey();
                                                        order.setId(orderId);
                                                        orderRef.setValue(order);

                                                        Log.d("CartItemDebug", "Before removing items from cart");
                                                        removeOrderedProductsFromCart(selectedProducts);
                                                        Log.d("CartItemDebug", "After removing items from cart");

                                                        updateProductQuantity(product.getId_product(), product.getQuantity_product());

                                                        showDialogOrder();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                    // Xử lý khi có lỗi xảy ra trong quá trình truy vấn Firebase
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        // Xử lý khi có lỗi xảy ra
                                    }
                                });
                            }
                        } else {
                            showOutOfStockDialog();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Xử lý khi có lỗi xảy ra trong quá trình truy vấn Firebase
                }
            });
        }
    }
    private void getUsernameFromFirebase() {
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("user").child(userId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child("username").getValue(String.class);
                    if (username != null) {
                        buyerUsername = username;
                    } else {
                    }
                } else {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý khi có lỗi xảy ra trong quá trình truy vấn cơ sở dữ liệu
            }
        });
    }
    private void removeOrderedProductsFromCart(List<AddProductToCart> selectedProducts) {
        String userId = firebaseUser.getUid();
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("cart").child(userId);

        for (AddProductToCart product : selectedProducts) {
            String productId = product.getId_product();
            Log.d("CartItemDebug", "Removing cart item with product ID: " + productId);


            cartRef.child(productId).removeValue();
        }
    }



    private int calculateTotalQuantity(List<InfoProductOrder> listProduct) {
        int totalQuantity = 0;
        for (InfoProductOrder infoProduct : listProduct) {
            totalQuantity += infoProduct.getQuantityPr();
        }
        return totalQuantity;
    }

    private void showOutOfStockDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Thông báo");
        builder.setMessage("Một số sản phẩm đã hết hàng. Vui lòng kiểm tra lại giỏ hàng của bạn.");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    @Override
    public void onDiscountUpdated(double totalSelectedDiscounts) {
        totalDiscount = totalSelectedDiscounts;
        if (totalDiscount > 0) {
            TotalPay = TotalPlusShip - totalDiscount;

            txtAllVoucherCart.setText(String.format("-" + formatPrice(totalSelectedDiscounts)));
        } else {
            TotalPay = TotalPlusShip;
            Log.d("hhhh", "TotalPlusShip" + TotalPay);
            txtAllVoucherCart.setText(String.format("-" + formatPrice(totalSelectedDiscounts)));
        }
        txtTotalCart.setText(String.format(formatPrice(TotalPay)));
        total_price_cart.setText(String.format(formatPrice(TotalPay)) + "VND");
    }

    private void updateWalletAfterOrder() {
        DatabaseReference buyerWalletRef = userRef.child("user").child(firebaseUser.getUid()).child("wallet");
        buyerWalletRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    double currentWalletAmount = dataSnapshot.getValue(Double.class);
                    if (totalDiscount > 0) {
                        TotalPay = TotalPlusShip - totalDiscount;
                    } else {
                        TotalPay = TotalPlusShip;
                        Log.d("hhhh", "TotalPlusShip" + TotalPlusShip);
                    }
                    double newWalletAmount = currentWalletAmount - TotalPay;
                    Log.d("Debug", "TotalPay: " + TotalPay);
                    buyerWalletRef.setValue(newWalletAmount);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void showWallet() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference();
        if (firebaseUser != null) {
            String buyerID = firebaseUser.getUid();
            DatabaseReference buyerRef = userRef.child("user").child(buyerID).child("wallet");
            buyerRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        double walletAmount = dataSnapshot.getValue(Double.class);
                        txtMoneyCart.setText(String.format(formatPrice(walletAmount)));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            if (getActivity() != null) {
                Intent intent = getActivity().getIntent();
                if (intent != null) {
                    String idProduct = intent.getStringExtra("idPro");
                    if (idProduct != null) {
                        productRef = userRef.child("products").child(idProduct);
                    } else {
                        Log.e("CartToOrderFragment", "idProduct is null");
                    }
                }
            }
        } else {
            Log.e("CartToOrderFragment", "Firebase user is null");
        }
    }

    private void checkWalletBalanceBeforeOrder() {
        if (txtPayment_Cart.getText().toString().equals("Pay with wallet")) {
            String walletAmountString = txtMoneyCart.getText().toString().replaceAll("[^\\d.]", "");
            double currentWalletAmount = Double.parseDouble(walletAmountString);
            if (totalDiscount > 0) {
                TotalPay = TotalPlusShip - totalDiscount;
            } else {
                TotalPay = TotalPlusShip;
            }
            if (TotalPay > currentWalletAmount) {
                showInsufficientBalanceDialog();
            } else {
                CreateOrder();
            }
        } else {
            CreateOrder();
        }
    }

    private void showInsufficientBalanceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Thông báo");
        builder.setMessage("Số dư trong ví không đủ để thanh toán. Vui lòng nạp thêm tiền vào ví để tiếp tục.");
        builder.setPositiveButton("Nạp tiền", (dialog, which) -> {
            Intent topUpIntent = new Intent(getContext(), TopUpCardActivity.class);
            startActivity(topUpIntent);
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void updateProductQuantity(String productId, int purchasedQuantity) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("products").child(productId);
        productRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Product product = mutableData.getValue(Product.class);
                if (product != null) {
                    int currentQuantity = product.getQuantity();
                    int newQuantity = currentQuantity - purchasedQuantity;
                    if (newQuantity < 0) {
                        newQuantity = 0;
                    }
                    product.setQuantity(newQuantity);
                    mutableData.setValue(product);
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean committed, @Nullable DataSnapshot dataSnapshot) {
                if (committed) {
                    Log.d("UpdateQuantity", "Số lượng sản phẩm đã cập nhật thành công");
                } else {
                    Log.e("UpdateQuantity", "Lỗi khi cập nhật số lượng sản phẩm: " + databaseError);
                }
            }
        });
    }

    private void showSelectedProducts(List<AddProductToCart> selectedProducts) {
        if (orderAdapter != null) {
            orderAdapter.updateSelectedProducts(selectedProducts);
            totalPrice = orderAdapter.calculateTotalPrice();
        }
    }

    private String formatPrice(double price) {
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
        String formattedPrice = format.format(price);
        if (formattedPrice.endsWith(".00")) {
            formattedPrice = formattedPrice.substring(0, formattedPrice.length() - 3);
        }
        return formattedPrice;
    }

    private void showDiaLogAddress() {
        Intent intent = new Intent(getContext(), ShowListLocationActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("key", "value");
        intent.putExtras(bundle);
        startActivityForResult(intent, Activity.RESULT_CANCELED);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Bundle bundle = data.getExtras();
            if (bundle == null) {
                Toast.makeText(getContext(), "Chưa chọn địa chỉ", Toast.LENGTH_SHORT).show();
                Log.d("GGGGGGG", "setLocation: ");
            } else {
//                idlr.setVisibility(View.VISIBLE);
                String name = bundle.getString("nameLocation");
                Log.d("HHHHHHHHHHH", "setLocation: " + name);
                String phone = bundle.getString("phoneLocation");
                String location = bundle.getString("location");
                String nameuser = bundle.getString("nameLocation");
                txtAddress.setVisibility(View.VISIBLE);
                txtAddress.setText(location);
                txtPhone.setVisibility(View.VISIBLE);
                txtPhone.setText(phone);
                txtName.setVisibility(View.VISIBLE);
                txtName.setText(nameuser);
                lineCart.setVisibility(View.VISIBLE);
            }
        }
    }

    private void poppuGetListPayment() {
        String[] listPayment = {"Payment on delivery", "Pay with wallet"};
        PaymentMethods.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(getContext(), PaymentMethods);
            for (String address : listPayment) {
                popupMenu.getMenu().add(address);
            }
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    String selectedAddress = item.getTitle().toString();
                    txtPayment_Cart.setText(selectedAddress);
                    return true;
                }
            });
            popupMenu.show();
        });
    }

    private void showDialogOrder() {
        ConstraintLayout successDialog = getActivity().findViewById(R.id.successDialog);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.success_dialog_order, successDialog);
        Button successDone = view.findViewById(R.id.btnDone);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        successDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

                navigateToNewScreen();
            }
        });
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    private void navigateToNewScreen() {
        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
    private void notifications() {
        // Xử lý khi người dùng nạp thẻ thành công
        String title = "Đặt hàng";
        String content = String.format("Đơn hàng của bạn đã được đặt, vui lòng chờ Admin xác nhận trong ít phút.");
        String currentTime = getCurrentDate();
        String userId = mAuth.getUid();
        notificationRef = FirebaseDatabase.getInstance().getReference("notifications").child(firebaseUser.getUid());
        String notificationId = notificationRef.push().getKey();

        notificationsToAdmin();

        // Gửi thông báo lên Firebase Realtime Database
        notificationRef.child(notificationId).child("title").setValue(title);
        notificationRef.child(notificationId).child("content").setValue(content);
        notificationRef.child(notificationId).child("dateTime").setValue(currentTime);
        notificationRef.child(notificationId).child("userId").setValue(userId);
    }
    private void notificationsToAdmin() {
        String adminId = "ZYA1yQdRAYSzh1K24ZVYIYvHIc92";

        String username = buyerUsername;
        String title = "Đơn hàng";
        String content = String.format("Người dùng %s vừa đặt đơn hàng, vui lòng kiểm tra và duyệt.", username);
        String currentTime = getCurrentDate();

        DatabaseReference adminNotificationRef = FirebaseDatabase.getInstance().getReference("notifications").child(adminId);
        String notificationId = adminNotificationRef.push().getKey();

        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("title", title);
        notificationData.put("content", content);
        notificationData.put("dateTime", currentTime);
        notificationData.put("userId", firebaseUser.getUid());
        notificationData.put("isRead", false);

        adminNotificationRef.child(notificationId).setValue(notificationData);
    }
}