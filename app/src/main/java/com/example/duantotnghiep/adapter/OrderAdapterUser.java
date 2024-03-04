package com.example.duantotnghiep.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import com.example.duantotnghiep.activity.ReviewsActivity;
import com.example.duantotnghiep.model.InfoProductOrder;
import com.example.duantotnghiep.model.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapterUser extends RecyclerView.Adapter<OrderAdapterUser.OrderViewHolder>{
    private Context context;
    private List<Order> list;
    private Callback callback;
    private List<InfoProductOrder> infoProductOrderList;
    private String currentFragment;
    private DatabaseReference notificationRef;
    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
    private String orderId;
    public OrderAdapterUser(Context context, List<Order> list, Callback callback) {
        this.context = context;
        this.list = list;
        this.infoProductOrderList = new ArrayList<>();
        this.currentFragment = currentFragment;
        this.callback = callback;
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
    public void onBindViewHolder(@NonNull OrderAdapterUser.OrderViewHolder holder, int position) {
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
        holder.tv_nameByer.setText("Đơn của Bạn: " + order.getCustomerName());
        holder.phoneByer.setText("SĐT: " +order.getNumberPhone());
        holder.adresByer.setText("Địa chỉ: " +order.getAddress());
        holder.tvTotal.setText("Thành tiền : "+order.getTotal()+ "VNĐ");
        if (order.getNote().isEmpty()||order.getNote().equals("")||order.getNote()==""){
            holder.tvNoteOrder.setVisibility(View.GONE);
        }else {
            holder.tvNoteOrder.setText("NOTE: " +order.getNote());
        }

        holder.soluong.setText(String.valueOf("Số lượng SP: : " +order.getTotalQuantity()));
        holder.tvDate.setText(order.getDate());
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

        if (currentFragment.equals("WaitFragment")) {
            btnHuyDon.setVisibility(View.VISIBLE);
        } else {
            btnHuyDon.setVisibility(View.GONE);
        }
        if (currentFragment.equals("DoneFragment") || currentFragment.equals("CancleFragment") || currentFragment.equals("DeliverFragment")) {
            btnReview.setVisibility(View.VISIBLE);
        } else {
            btnReview.setVisibility(View.GONE);
        }
        if (currentFragment.equals("CancleFragment")){
            btnReview.setText("Mua lại đơn hàng");
        }
        if (currentFragment.equals("DeliverFragment")){
            btnReview.setText("Đã nhận được hàng");
        }
        btnReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFragment.equals("DoneFragment")) {
                    Intent intent = new Intent(context, ReviewsActivity.class);
                    intent.putExtra("idOrder", order.getId());
                    context.startActivity(intent);
                } else if (currentFragment.equals("CancleFragment")) {
                    updateOrderStatus(order.getId(), "Waitting");
                    order.setPaid(false);
                    updateOrderPaid(order.getId(),false);
                    Toast.makeText(context, "Đã đặt lại đơn hàng", Toast.LENGTH_SHORT).show();
                }else if (currentFragment.equals("DeliverFragment")){
                    updateOrderStatus(order.getId(), "Done");
                    notificationsDone();
                    notificationsDoneAdmin();
                    Toast.makeText(context, "Đơn đã hoàn thành!!!", Toast.LENGTH_SHORT).show();
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
    public void setCurrentFragment(String currentFragment) {
        this.currentFragment = currentFragment;
    }
    private void updateOrderStatus(String orderId, String status) {
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("list_order");
        DatabaseReference orderRef = ordersRef.child(orderId).child("status");
        orderRef.setValue(status);
    }
    private void updateOrderPaid(String orderId, boolean paid) {
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("list_order");
        DatabaseReference orderRef = ordersRef.child(orderId).child("paid");
        orderRef.setValue(paid);
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

        TextView idOrderTextView = orderDetailDialog.findViewById(R.id.idOrder);
        if (idOrderTextView != null) {
            idOrderTextView.setText(orderId);
        }

        for (InfoProductOrder product : productList) {
            Log.d("Product", "Name: " + product.getNamePr() + ", Price: " + product.getPrice());
        }
    }
    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        Date currentDate = new Date();
        return sdf.format(currentDate);
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
    private void notificationsDone() {
        String title = String.format("Đơn hàng %s", orderId);
        String content = "Đơn hàng của bạn đã hoàn thành";
        String currentTime = getCurrentTime();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getUid();
        notificationRef = FirebaseDatabase.getInstance().getReference("notifications").child(firebaseUser.getUid());
        String notificationId = notificationRef.push().getKey();

        notificationRef.child(notificationId).child("title").setValue(title);
        notificationRef.child(notificationId).child("content").setValue(content);
        notificationRef.child(notificationId).child("dateTime").setValue(currentTime);
        notificationRef.child(notificationId).child("userId").setValue(userId);
    }
    private void notificationsDoneAdmin() {
        String adminId = "ZYA1yQdRAYSzh1K24ZVYIYvHIc92";

        String title = String.format("Đơn hàng %s", orderId);;
        String content = "Đơn hàng đã được giao thành công";
        String currentTime = getCurrentTime();

        notificationRef = FirebaseDatabase.getInstance().getReference("notifications").child(adminId);
        String notificationId = notificationRef.push().getKey();

        notificationRef.child(notificationId).child("title").setValue(title);
        notificationRef.child(notificationId).child("content").setValue(content);
        notificationRef.child(notificationId).child("dateTime").setValue(currentTime);
        notificationRef.child(notificationId).child("userId").setValue(adminId);
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