package com.example.duantotnghiep.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duantotnghiep.MainActivity;
import com.example.duantotnghiep.R;
import com.example.duantotnghiep.activity.ReviewsActivity;
import com.example.duantotnghiep.model.InfoProductOrder;
import com.example.duantotnghiep.model.Order;
import com.example.duantotnghiep.model.Reviews;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.security.auth.callback.Callback;

public class ProductOrderReviewAdapter extends RecyclerView.Adapter<ProductOrderReviewAdapter.ProductViewHolder> {
    private Context context;
    private List<InfoProductOrder> productList;
    private Callback callback;
    private RatingBar ratingBar;
    private ImageButton img_back;
    private ImageView imgPr,colorPr;
    private TextView namepr,quantityPr,sizePr;
    private TextInputEditText edReviews;
    private Button btnsend;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference mReference;
    private int numStart;
    public ProductOrderReviewAdapter(Context context, List<InfoProductOrder> productList) {
        this.context = context;
        this.productList = productList;
    }
    @NonNull
    @Override
    public ProductOrderReviewAdapter.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_review, parent, false);
        return new ProductOrderReviewAdapter.ProductViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ProductOrderReviewAdapter.ProductViewHolder holder, int position) {
        InfoProductOrder productOrder = productList.get(position);

        holder.tvNameProduct.setText(productOrder.getNamePr());
        holder.colorProduct.setBackgroundColor(productOrder.getColorPr());
        holder.tvQuantity.setText(String.valueOf(productOrder.getQuantityPr()));
        holder.tvTotal.setText(String.valueOf(productOrder.getPrice()));
        holder.tv_size.setText("Size : " + productOrder.getSize());

        if (productOrder.getImgPr() != null && !productOrder.getImgPr().isEmpty()) {
            Uri imageUri = Uri.parse(productOrder.getImgPr());
            Picasso.get()
                    .load(imageUri)
                    .placeholder(R.drawable.pant)
                    .error(R.drawable.pant)
                    .into(holder.imgProduct);
        } else {
            holder.imgProduct.setImageResource(R.drawable.pant);
        }
        holder.btnwritereivew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogReview(context,productOrder);
            }
        });
    }
    @Override
    public int getItemCount() {
        return productList.size();
    }
    public void setProductList(List<InfoProductOrder> productList) {
        this.productList = productList;
        notifyDataSetChanged();

        for (InfoProductOrder product : productList) {
            Log.d("Product", "Name: " + product.getNamePr() + ", Price: " + product.getPrice());
        }
    }
    public class ProductViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgProduct, colorProduct;
        private TextView tvNameProduct;
        private TextView tvQuantity;
        private TextView tvTotal;
        private TextView tv_size;
        private Button btnwritereivew;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = (ImageView) itemView.findViewById(R.id.img_product);
            colorProduct = (ImageView) itemView.findViewById(R.id.tv_color);
            tvNameProduct = (TextView) itemView.findViewById(R.id.tv_nameProduct);
            tvQuantity = (TextView) itemView.findViewById(R.id.tv_quantity);
            tvTotal = (TextView) itemView.findViewById(R.id.tv_total);
            tv_size = (TextView) itemView.findViewById(R.id.tv_size);
            btnwritereivew = itemView.findViewById(R.id.btnwritereivew);
        }
    }
     void openDialogReview(Context context ,  InfoProductOrder productOrder){
         Dialog dialog = new Dialog(context);
         Window window = dialog.getWindow();
         if (window == null) {
             return;
         }
         dialog.setContentView(R.layout.dialog_review);
         window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
         window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
         WindowManager.LayoutParams windowAttributes = window.getAttributes();
         window.setAttributes(windowAttributes);

         firebaseAuth = FirebaseAuth.getInstance();
         firebaseUser = firebaseAuth.getCurrentUser();
         mReference = FirebaseDatabase.getInstance().getReference();


         ratingBar =dialog.findViewById(R.id.start);
         imgPr = dialog.findViewById(R.id.imgPr);
         colorPr = dialog.findViewById(R.id.colorPr);
         namepr = dialog.findViewById(R.id.namePr);
         quantityPr = dialog.findViewById(R.id.quantityPr);
         sizePr = dialog.findViewById(R.id.sizePr);
         edReviews = dialog.findViewById(R.id.tvReviews);
         btnsend = dialog.findViewById(R.id.btnsendreviews);

         if (productOrder.getImgPr() != null && !productOrder.getImgPr().isEmpty()) {
             Uri imageUri = Uri.parse(productOrder.getImgPr());
             Picasso.get()
                     .load(imageUri)
                     .placeholder(R.drawable.pant)
                     .error(R.drawable.pant)
                     .into(imgPr);
         } else {
             imgPr.setImageResource(R.drawable.pant);
         }
         namepr.setText(productOrder.getNamePr());
         colorPr.setBackgroundColor(productOrder.getColorPr());
         quantityPr.setText(productOrder.getQuantityPr()+"");
         sizePr.setText(productOrder.getSize()+"");
         ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
             @Override
             public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                 numStart=Math.round(v);
             }
         });
         btnsend.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 if (TextUtils.isEmpty(edReviews.getText().toString())){
                     Toast.makeText(context, "Hãy viết đánh giá", Toast.LENGTH_SHORT).show();
                 } else if (numStart==0) {
                     Toast.makeText(context, "Hãy chọn số sao để đánh giá sự hài lòng của bạn", Toast.LENGTH_SHORT).show();
                 }else {
                     FirebaseDatabase database = FirebaseDatabase.getInstance();
                     DatabaseReference reviewRef = database.getReference("reviews");
                     firebaseUser = firebaseAuth.getCurrentUser();
                     String idReview = reviewRef.push().getKey();
                     String idUser = firebaseUser.getUid();
                     sendReviews(idReview,"",productOrder.getIdProduct(),numStart,edReviews.getText().toString());
                     Toast.makeText(context, "Đánh giá thành công", Toast.LENGTH_SHORT).show();
                     Intent intent1 = new Intent(context, MainActivity.class);
                    context.startActivity(intent1);
                 }
             }
         });





         dialog.show();


    }
    private void sendReviews(String id,String idOrder,String idProduct,int start, String cmt){
        String idUser = firebaseUser.getUid();
        DatabaseReference userRef = mReference.child("user").child(idUser);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String img = snapshot.child("img").getValue(String.class);
                String name = snapshot.child("username").getValue(String.class);
                String date = getCurrentTime();
                Reviews reviews = new Reviews(id,idUser,name,img,idOrder,idProduct,start,cmt,date);
                AddReviews(reviews);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void AddReviews(Reviews reviews) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("reviews");
        String id = reviews.getId();
        myRef.child(id).setValue(reviews, (error, ref) -> {
            if (error == null) {
            } else {
                Toast.makeText(context, "Lỗi khi lưu reviews vào Realtime Database", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date currentDate = new Date();
        return sdf.format(currentDate);
    }
}
