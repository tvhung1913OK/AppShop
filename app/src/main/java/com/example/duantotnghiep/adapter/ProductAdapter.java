package com.example.duantotnghiep.adapter;




import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;


import com.example.duantotnghiep.R;
import com.example.duantotnghiep.databinding.ItemProductBinding;
import com.example.duantotnghiep.model.ColorProduct;
import com.example.duantotnghiep.model.Product;
import com.example.duantotnghiep.model.Reviews;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import gun0912.tedimagepicker.builder.TedImagePicker;



public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    Context context;
    List<String> sizes;
    List<Product> productList;
    List<Uri> selectedImageUris = new ArrayList<>();
    int YOUR_REQUEST_CODE = 1;
    DatabaseReference databaseReference;
    FirebaseStorage storage;
    MutilpleColorAdapter mAdapter = new MutilpleColorAdapter();
    Product product;
    private Product editingProduct;
    List<Uri> imageUris;
    ImageView chooseImgEdit, chooseColor;
    EditText editTitle, editPrice, editQuantity, editBrand, editDes;
    Button editProduct;
    RecyclerView rvMultipleColorEdit, rvMultipleImgEdit;
    int quantity = 0;
    private List<ColorProduct> selectedColors = new ArrayList<>();
    Spinner sizeSpinnerEdit;
    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }
    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductViewHolder(ItemProductBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }
    public void updateProductList(List<Product> productList) {
        this.productList = productList;
        notifyDataSetChanged();
    }
    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, @SuppressLint("RecyclerView") int position) {
        product = productList.get(position);
        holder.binding.tvProduct.setText(product.getName());
        holder.binding.priceProduct.setText(String.valueOf(product.getPrice()));
        holder.binding.quantity.setText("Số lượng: " + product.getQuantity());
        holder.binding.sold.setText(String.valueOf("Đã bán: " +product.getSold()));
        if (product.getImgProduct() != null && !product.getImgProduct().isEmpty()) {
            Uri firstImageUri = Uri.parse(product.getImgProduct().get(0));
            Glide.with(context).load(firstImageUri).into(holder.binding.imageProduct);
        } else {
            holder.binding.imageProduct.setImageResource(R.drawable.tnf);
        }
        holder.binding.imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogEdit(productList.get(position));
            }
        });
        holder.binding.imvDeleteProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    product = productList.get(adapterPosition);
                    String productId = product.getId();
                    Log.d("ProductAdapter", "Deleting product: " + productId);

                    deleteProduct(productId, product.getImgProduct());
                }
            }
        });
        // Truy cập đến nhánh "reviews" trong Firebase
        DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference("reviews");
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("products").child(product.getId());
        reviewsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                float totalRating = 0;
                int numRatings = 0;

                for (DataSnapshot reviewSnapshot : dataSnapshot.getChildren()) {
                    Reviews review = reviewSnapshot.getValue(Reviews.class);
                    if (review != null && review.getProductId().equals(product.getId())) {
                        totalRating += review.getStart();
                        numRatings++;
                    }
                }

                if (numRatings > 0) {
                    float averageRating = totalRating / numRatings;
                    DecimalFormat decimalFormat = new DecimalFormat("0.0");
                    String formattedRating = decimalFormat.format(averageRating);
                    holder.binding.start.setText(String.valueOf(formattedRating));
                    productRef.child("averageRating").setValue(averageRating);
                    productRef.child("numRatings").setValue(numRatings);
                } else {
                    holder.binding.start.setText("5.0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("loi", "onCancelled: " + databaseError.getMessage());
            }
        });
    }


    private void showDialogEdit(Product productToEdit) {

        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            if (!activity.isFinishing()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();
                View view = layoutInflater.inflate(R.layout.edit_dialog_product, null);
                builder.setView(view);
                editProduct = view.findViewById(R.id.btnEditProduct);
                editTitle = view.findViewById(R.id.titleProductEdit);
                editDes = view.findViewById(R.id.descriptionProductEdit);
                editBrand = view.findViewById(R.id.BrandProductEdit);
                editPrice = view.findViewById(R.id.priceProductSellerEdit);

                chooseColor = view.findViewById(R.id.btnColorEdit);
                chooseImgEdit = view.findViewById(R.id.chooseImgEdit);
                sizeSpinnerEdit = view.findViewById(R.id.spinnerSizeEdit);
                rvMultipleColorEdit = view.findViewById(R.id.rvMutilpeColorEdit);
                rvMultipleImgEdit = view.findViewById(R.id.mutilpeImgEdit);

                editTitle.setText(productToEdit.getName());
                editPrice.setText(String.valueOf(productToEdit.getPrice()));
                editDes.setText(productToEdit.getDescription());
                editBrand.setText(productToEdit.getBrand());



                String[] productTypeValues = context.getResources().getStringArray(R.array.product_types);


                String selectedProductType = productToEdit.getProductType().toString();
                int position = -1;
                for (int i = 0; i < productTypeValues.length; i++) {
                    if (productTypeValues[i].equals(selectedProductType)) {
                        position = i;
                        break;
                    }
                }


                if (position >= 0) {
                    sizeSpinnerEdit.setSelection(position);
                }


                List<ColorProduct> selectedColorProducts = productToEdit.getListColor();
                List<String> sizeColors = productToEdit.getSize();
                if (selectedColorProducts != null && !selectedColorProducts.isEmpty()) {

                    mAdapter.updateSelectedColors(selectedColorProducts, sizeColors);

                    rvMultipleColorEdit.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                    rvMultipleColorEdit.setAdapter(mAdapter);

                } else {
                    Toast.makeText(context, "Danh sách màu không hợp lệ", Toast.LENGTH_SHORT).show();
                }


                List<String> imageUrls = productToEdit.getImgProduct();


                imageUris = new ArrayList<>();
                for (String imageUrl : imageUrls) {
                    imageUris.add(Uri.parse(imageUrl));
                }
                MutilpleImgAdapter imgAdapter = new MutilpleImgAdapter(context, imageUris);

                rvMultipleImgEdit.setAdapter(imgAdapter);


                chooseImgEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                Toast.makeText(context, "Click", Toast.LENGTH_SHORT).show();
                        openImagePicker();
                    }


                });
                chooseColor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialogColor();
                    }
                });


                AlertDialog alertDialog = builder.create();
                editProduct.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        quantity = selectedColorProducts.stream().map(product -> product.getQuantity().stream().reduce(0,Integer::sum)).reduce(0,Integer::sum).intValue();

                        String updatedTitle = editTitle.getText().toString();
                        String updatedDescription = editDes.getText().toString();
                        String updatedBrand = editBrand.getText().toString();
                        double updatedPrice = Double.parseDouble(editPrice.getText().toString());
//                        int updatedQuantity = Integer.parseInt(editQuantity.getText().toString());
                        List<ColorProduct> updatedColorProducts = mAdapter.getSelectedColors();
                        String selectedSize = (String) sizeSpinnerEdit.getSelectedItem();


                        productToEdit.setName(updatedTitle);
                        productToEdit.setDescription(updatedDescription);
                        productToEdit.setBrand(updatedBrand);
                        productToEdit.setPrice(updatedPrice);
//                        productToEdit.setQuantity(updatedQuantity);

                        productToEdit.setListColor(updatedColorProducts);
                        productToEdit.setQuantity(quantity);

                        productToEdit.setProductType(Product.ProductType.valueOf(selectedSize));
                        String selectedProductType = (String) sizeSpinnerEdit.getSelectedItem();
                        List<String> sizes = new ArrayList<>();
                        if ("CLOTHING".equals(selectedProductType)) {
                            sizes.addAll(Arrays.asList("XS", "S", "M", "L", "XL", "XXL"));
                        } else if ("FOOTWEAR".equals(selectedProductType)) {
                            sizes.addAll(Arrays.asList("36", "37", "38", "39", "40", "41", "42", "43", "44"));
                        }

                        productToEdit.setSize(sizes);


                        saveProductChanges(productToEdit);
                        alertDialog.dismiss();
                    }
                });


                alertDialog.show();
            }
        }
    }
    private void openImagePicker() {
        Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show();
        TedImagePicker
                .with(context)

                .startMultiImage(uriList -> {

                    imageUris = new ArrayList<>(uriList);

                });
    }


    private void showDialogColor() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.dialog_color, null);
        builder.setView(view);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        GridLayout gridLayout = view.findViewById(R.id.grid);

        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            final ImageButton button = (ImageButton) gridLayout.getChildAt(i);
            final int color = ((ColorDrawable) button.getBackground()).getColor();


            if (mAdapter.getSelectedColors().contains(color)) {
                button.setSelected(true);
            }

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // handle lại giống bên AddProductFragment
                    //                   mAdapter.updateColor(color);

                    button.setSelected(!button.isSelected());
                }
            });
        }

        Button addButton = view.findViewById(R.id.b21);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();
            }
        });
    }

    private void removeColor(int color) {
        for (ColorProduct cl: selectedColors) {
            if (cl.getColor() == color) {
                selectedColors.remove(cl);
                break;
            }
        }
    }

    private List<Integer> getColor(List<ColorProduct> selectedColorProducts) {
        ArrayList<Integer> listColor = new ArrayList<>();
        for (ColorProduct cl : selectedColorProducts) {
            listColor.add(cl.getColor());
        }
        return listColor;
    }

    private boolean checkColorExist(int color) {
        for (ColorProduct cl : selectedColors) {
            if (cl.getColor() == color) {
                return true;
            }
        }
        return false;
    }


    private void saveProductChanges(Product productToEdit) {
        databaseReference = FirebaseDatabase.getInstance().getReference("products");
        DatabaseReference productRef = databaseReference.child(productToEdit.getId());

        productRef.setValue(productToEdit).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Lỗi khi sửa thông tin sản phẩm", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void deleteProduct(String productId, List<String> imageUrls) {
        databaseReference = FirebaseDatabase.getInstance().getReference("products");
        storage = FirebaseStorage.getInstance();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn có chắc chắn muốn xóa sản phẩm này?");

        // Xử lý sự kiện khi người dùng chọn "Đồng ý"
        builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (String imageUrl : imageUrls) {
                    StorageReference imageRef = storage.getReferenceFromUrl(imageUrl);
                    imageRef.delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                }

                databaseReference.child(productId).removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Product productToRemove = null;
                                int positionToRemove = -1;
                                for (int i = 0; i < productList.size(); i++) {
                                    if (productList.get(i).getId().equals(productId)) {
                                        productToRemove = productList.get(i);
                                        positionToRemove = i;
                                        break;
                                    }
                                }
                                if (productToRemove != null) {
                                    productList.remove(positionToRemove);
                                    notifyDataSetChanged();
                                }

                                if (productList.isEmpty()) {

                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
            }
        });

        // Xử lý sự kiện khi người dùng chọn "Hủy"
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Đóng dialog
                dialog.dismiss();
            }
        });

        // Hiển thị dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        ItemProductBinding binding;

        public ProductViewHolder(ItemProductBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}
