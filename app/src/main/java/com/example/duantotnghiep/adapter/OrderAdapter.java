package com.example.duantotnghiep.adapter;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duantotnghiep.R;
import com.example.duantotnghiep.model.InfoProductOrder;
import com.example.duantotnghiep.model.Order;
import com.example.duantotnghiep.model.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private Context context;
    private List<Order> list;
    private Callback callback;
    private List<InfoProductOrder> infoProductOrderList;
    private String currentFragment;
    private List<Order> orderList;
    private DatabaseReference notificationRef;
    private String orderId;
    public OrderAdapter(Context context, List<Order> list, Callback callback) {
        this.context = context;
        this.list = list;
        this.infoProductOrderList = new ArrayList<>();
        this.currentFragment = currentFragment;
        this.callback = callback;
        this.orderList = orderList;
        orderId = "";
    }

    public List<InfoProductOrder> getInfoProductOrderList() {
        return infoProductOrderList;
    }
    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = list.get(position);
        orderId = order.getId();

        if (order == null) {
            return;
        }
        if (order.getCustomerImage() != null && !order.getCustomerImage().isEmpty()) {
            Uri imageUri = Uri.parse(order.getCustomerImage());
            Picasso.get()
                    .load(imageUri)
                    .placeholder(R.drawable.tnf)
                    .error(R.drawable.tnf)
                    .into(holder.img_byer);
        } else {
            holder.img_byer.setImageResource(R.drawable.tnf);
        }
        holder.tv_nameByer.setText("Đơn của: " + order.getCustomerName());
        holder.phoneByer.setText("SĐT: " +order.getNumberPhone());
        holder.adresByer.setText("Địa chỉ: " +order.getAddress());
        if (order.getNote().isEmpty()||order.getNote().equals("")||order.getNote()==""){
            holder.tvNoteOrder.setVisibility(View.GONE);
        }else {
            holder.tvNoteOrder.setText("NOTE: " +order.getNote());
        }
        holder.soluong.setText(String.valueOf("Số lượng SP: : " +order.getTotalQuantity()));
        holder.tvDate.setText(order.getDate());
        holder.tvTotal.setText("Thành tiền : "+order.getTotal()+" VNĐ");
        if (order.getStatus().equals("Deliver")||order.getStatus().equals("Done")){
            if (order.getPaid()==true){
                holder.tv_paid.setVisibility(View.VISIBLE);
            }else {
                holder.tv_paid.setVisibility(View.GONE);
            }
        }

        final int clickedPosition = position;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.logic(order);
                }
            }
        });
        holder.imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenuDialog(clickedPosition);
            }
        });
    }
    private void showMenuDialog(int position) {
        Order order = list.get(position);
        Dialog menuDialog = new Dialog(context);
        menuDialog.setContentView(R.layout.dialog_menu_order);
        menuDialog.getWindow().setBackgroundDrawable(context.getDrawable(R.drawable.bg_dialog_order));
        Window window = menuDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        window.setAttributes(windowAttributes);
        windowAttributes.gravity = Gravity.BOTTOM;

        Button btnReview = menuDialog.findViewById(R.id.btn_review);
        Button btnPropety = menuDialog.findViewById(R.id.btn_propety);
        Button btnHuyDon = menuDialog.findViewById(R.id.btnHuyDon);
        Button btnOut = menuDialog.findViewById(R.id.btnOut);

        if (currentFragment.equals("WaitForShopFragment")) {
            btnReview.setText("Xác nhận đơn hàng");
        } else if (currentFragment.equals("ConfirmForShopFragment")) {
            btnReview.setText("Vận chuyển đơn hàng");
        }else if (currentFragment.equals("DoneForShopFragment") || currentFragment.equals("CancleForShopFragment") || currentFragment.equals("DeliverForShopFragment")){
            btnReview.setVisibility(View.GONE);
        }
        if (currentFragment.equals("WaitForShopFragment")) {
            btnHuyDon.setVisibility(View.VISIBLE);
        } else {
            btnHuyDon.setVisibility(View.GONE);
        }
        btnReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFragment.equals("WaitForShopFragment")) {
                    updateOrderStatus(order.getId(), "Confirm");
                    notificationsCf(order);
                    Toast.makeText(context, "Đã xác nhận đơn!!!", Toast.LENGTH_SHORT).show();
                } else if (currentFragment.equals("ConfirmForShopFragment")) {
                    AddmoneyForSeller(order);
                    updateOrderStatus(order.getId(), "Deliver");
                    Toast.makeText(context, "Đơn đã được vận chuyển!!!", Toast.LENGTH_SHORT).show();
                    List<InfoProductOrder> productList = order.getListProduct();

                    for (InfoProductOrder product : productList) {
                        String productId = product.getIdProduct();
                        int quantityPr = product.getQuantityPr();
                        updateProductSold(productId, quantityPr);
                        updateProductQuantity(productId, quantityPr);
                    }
                }
                menuDialog.dismiss();
            }
        });
        btnPropety.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrderDetailDialog(order.getListProduct());
                menuDialog.dismiss();
            }
        });
        btnHuyDon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReturnmoneyForBuyer(order);
                updateOrderStatus(order.getId(), "Cancle");
                Toast.makeText(context, "Đã hủy đơn!!!", Toast.LENGTH_SHORT).show();
                menuDialog.dismiss();
            }
        });
        btnOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuDialog.dismiss();
            }
        });

        menuDialog.show();
    }
    private void updateProductSold(String productId, int quantityPr) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("products").child(productId);
        productRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Product product = mutableData.getValue(Product.class);
                if (product != null) {
                    int sold = product.getSold() + quantityPr;
                    product.setSold(sold);
                    mutableData.setValue(product);
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean committed, @Nullable DataSnapshot dataSnapshot) {
                if (committed) {
                    // Cập nhật thành công
                } else {
                    // Xử lý lỗi nếu cần
                }
            }
        });
    }
    private void updateProductQuantity(String productId, int quantityPr) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("products").child(productId);
        productRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Product product = mutableData.getValue(Product.class);
                if (product != null) {
                    int quantity = product.getQuantity() - quantityPr;
                    product.setQuantity(quantity);
                    mutableData.setValue(product);
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean committed, @Nullable DataSnapshot dataSnapshot) {
                if (committed) {
                    // Cập nhật thành công
                } else {
                    // Xử lý lỗi nếu cần
                }
            }
        });
    }
    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        Date currentDate = new Date();
        return sdf.format(currentDate);
    }
    private void notificationsCf(Order order) {
        String title = String.format( "Đơn hàng %s", orderId);
        String content = "Đơn hàng của bạn đã được xác nhận và giao cho đơn vị vận chuyển nhanh EXpress, đơn sẽ được giao đến bạn trong vài ngày tới.";
        String currentTime = getCurrentTime();
        String userId = order.getIdBuyer(); // Lấy idBuyer từ đối tượng Order

        notificationRef = FirebaseDatabase.getInstance().getReference("notifications").child(userId); // Gửi thông báo cho người đặt hàng
        String notificationId = notificationRef.push().getKey();

        notificationRef.child(notificationId).child("title").setValue(title);
        notificationRef.child(notificationId).child("content").setValue(content);
        notificationRef.child(notificationId).child("dateTime").setValue(currentTime);
        notificationRef.child(notificationId).child("userId").setValue(userId);
    }

    public void setCurrentFragment(String currentFragment) {
        this.currentFragment = currentFragment;
    }
    private void ReturnmoneyForBuyer(Order order){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference orderRef = firebaseDatabase.getReference("list_order");
        DatabaseReference buyerRef = firebaseDatabase.getReference("user").child(order.getIdBuyer()).child("wallet");
        String id = order.getId();
        boolean checkPaid = order.getPaid();
        order.setPaid(checkPaid);
        orderRef.child(id).setValue(order, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if (error == null) {
                    if (checkPaid) {
                            buyerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        double buyerBalance = snapshot.getValue(Double.class);
                                        double newBuyerBalance = buyerBalance + order.getTotal();
                                        buyerRef.setValue(newBuyerBalance);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show();
                                }
                            });


                    } else {
                        Toast.makeText(context, "Update status", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void AddmoneyForSeller(Order order){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference orderRef = firebaseDatabase.getReference("list_order");
        DatabaseReference sellerRef = firebaseDatabase.getReference("user").child(order.getIdSeller()).child("wallet");
        String id = order.getId();
        boolean checkPaid = order.getPaid();
        order.setPaid(checkPaid);
        orderRef.child(id).setValue(order, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if (error == null) {
                    if (checkPaid) {
                        sellerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    double buyerBalance = snapshot.getValue(Double.class);
                                    double newBuyerBalance = buyerBalance + order.getTotal();
                                    sellerRef.setValue(newBuyerBalance);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show();
                            }
                        });


                    } else {
                        Toast.makeText(context, "Update status", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void updateOrderStatus(String orderId, String status) {
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("list_order");
        DatabaseReference orderRef = ordersRef.child(orderId).child("status");
        orderRef.setValue(status);
    }
    private void showOrderDetailDialog(List<InfoProductOrder> productList) {
        Dialog orderDetailDialog = new Dialog(context);
        orderDetailDialog.setContentView(R.layout.dialog_order_detail);
        RecyclerView recyclerView = orderDetailDialog.findViewById(R.id.recyclerViewHL);
        ProductOrderAdapter productOrderAdapter = new ProductOrderAdapter(context, productList);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(productOrderAdapter);
        productOrderAdapter.setProductList(productList);
        orderDetailDialog.show();

        // Tìm TextView và thiết lập giá trị id
        TextView idOrderTextView = orderDetailDialog.findViewById(R.id.idOrder);
        if (idOrderTextView != null) {
            idOrderTextView.setText(orderId);
        }

        // Log danh sách sản phẩm
        for (InfoProductOrder product : productList) {
            Log.d("Product", "Name: " + product.getNamePr() + ", Price: " + product.getPrice());
        }
    }
    public void setProductList(List<InfoProductOrder> productList) {
        this.infoProductOrderList = productList;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }
    public class OrderViewHolder extends RecyclerView.ViewHolder {
        private ImageView img_byer;
        private TextView tv_nameByer, adresByer, phoneByer, soluong, tvNoteOrder, tvDate,tv_paid,tvTotal;
        private ImageView imgMenu;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            img_byer = (ImageView) itemView.findViewById(R.id.img_byer);
            tv_nameByer = (TextView) itemView.findViewById(R.id.tv_nameByer);
            adresByer = (TextView) itemView.findViewById(R.id.adresByer);
            phoneByer = (TextView) itemView.findViewById(R.id.phoneByer);
            imgMenu = (ImageView) itemView.findViewById(R.id.img_menu);
            soluong = (TextView) itemView.findViewById(R.id.soluong);
            tvNoteOrder = (TextView) itemView.findViewById(R.id.tvNoteOrder);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            tv_paid = (TextView) itemView.findViewById(R.id.tv_paid);
            tvTotal = (TextView) itemView.findViewById(R.id.tvTotal);

        }
    }
    public interface Callback {
        void logic(Order order);
    }
}