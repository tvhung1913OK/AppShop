package com.example.duantotnghiep.fragment;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duantotnghiep.activity.ManagerProductActivity;

import com.example.duantotnghiep.R;
import com.example.duantotnghiep.adapter.ColorAdapter;
import com.example.duantotnghiep.adapter.DiscountAdapter;
import com.example.duantotnghiep.adapter.DiscountSelectionAdapter;
import com.example.duantotnghiep.adapter.MutilpleColorAdapter;
import com.example.duantotnghiep.adapter.MutilpleImgAdapter;
import com.example.duantotnghiep.model.ColorProduct;
import com.example.duantotnghiep.model.Discount;
import com.example.duantotnghiep.model.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddProductFragment extends Fragment {
    ImageView btnColor, btnDiscount;
    private FirebaseAuth firebaseAuth;
    private List<Uri> selectedImageUris = new ArrayList<>();
    private List<ColorProduct> selectedColorProducts = new ArrayList<>();
    private MutilpleColorAdapter mAdapter = new MutilpleColorAdapter();
    private ImageView chooseImg;
    List<String> selectedSize;
    String Title, Des, Brand;
    MutilpleImgAdapter adapter;
    DiscountSelectionAdapter discountSelectionAdapter;
    private boolean isAddingProduct = false;
    private RecyclerView multipleImg;
    private static final int REQUEST_CODE_SELECT_IMAGES = 1;
    EditText edtTitle, edtPrice, edtBrand, edtDes;
    Button addProduct;
    int Price;
    List<Discount> selectedDiscounts;
    List<Discount> allDiscounts;
    RecyclerView rvMutilpeDiscount;
    private Spinner sizeSpinner;
    Product product;
    Integer quantity = 0 ;
    private StorageReference storageReference;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.add_product_fragment, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        sizeSpinner = root.findViewById(R.id.spinnerSize);
        addProduct = root.findViewById(R.id.btnAddProduct);
        btnColor = root.findViewById(R.id.btnColor);
        multipleImg = root.findViewById(R.id.mutilpeImg);
        edtTitle = root.findViewById(R.id.titleProduct);
        edtPrice = root.findViewById(R.id.priceProductSeller);
        edtDes = root.findViewById(R.id.descriptionProduct);
        btnDiscount = root.findViewById(R.id.btnDiscount);
        rvMutilpeDiscount = root.findViewById(R.id.rvMutilpeDiscount);
        edtBrand = root.findViewById(R.id.BrandProduct);
        chooseImg = root.findViewById(R.id.chooseImg);
        multipleImg = root.findViewById(R.id.mutilpeImg);
        RecyclerView rvMutilpeColor = root.findViewById(R.id.rvMutilpeColor);
        rvMutilpeColor.setAdapter(mAdapter);
        btnColor.setOnClickListener(v -> showDialogColor());
        btnDiscount.setOnClickListener(v -> showDialogDiscount());
        ((ManagerProductActivity) requireActivity()).hideFloatingActionButton();
        chooseImg.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Chọn hình ảnh"), REQUEST_CODE_SELECT_IMAGES);
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        multipleImg.setLayoutManager(layoutManager);


        adapter = new MutilpleImgAdapter(getContext(), selectedImageUris);
        multipleImg.setAdapter(adapter);

        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput()) {
                    if (!isAddingProduct) {
                        isAddingProduct = true;
                        saveProductToRealtimeDatabase();
                    }
                } else {

                }
            }
        });
        // default
        selectedSize = new ArrayList<>();
        selectedSize.addAll(Arrays.asList("S", "M", "L", "XL", "XXL"));

        ArrayAdapter<String> sizeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item);
        sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sizeSpinner.setAdapter(sizeAdapter);

        sizeAdapter.add("CLOTHING");
        sizeAdapter.add("FOOTWEAR");

        sizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedProductType = (String) sizeSpinner.getSelectedItem();
                selectedSize = new ArrayList<>();
                if ("CLOTHING".equals(selectedProductType)) {
                    selectedSize.addAll(Arrays.asList("XS", "S", "M", "L", "XL", "XXL"));
                } else if ("FOOTWEAR".equals(selectedProductType)) {
                    selectedSize.addAll(Arrays.asList("36", "37", "38", "39", "40", "41", "42", "43", "44"));
                }
                mAdapter.updateSelectedColors(selectedColorProducts, selectedSize);
                rvMutilpeColor.setAdapter(mAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });
        return root;
    }

    private void showDialogDiscount() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater layoutInflater = LayoutInflater.from(requireContext());

        View view = layoutInflater.inflate(R.layout.dialog_discount_selection, null);
        builder.setView(view);

        RecyclerView recyclerView = view.findViewById(R.id.rvDiscountSelection);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        DatabaseReference discountsRef = FirebaseDatabase.getInstance().getReference("discounts");

        discountsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allDiscounts = new ArrayList<>();
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Discount discount = snapshot.getValue(Discount.class);


                    if (currentUser != null && discount != null && currentUser.getUid().equals(discount.getSellerId())) {
                        allDiscounts.add(discount);
                    }
                }

                List<Discount> selectedDiscountIds = new ArrayList<>();
                DiscountSelectionAdapter adapter = new DiscountSelectionAdapter(allDiscounts, selectedDiscountIds);
                recyclerView.setAdapter(adapter);

                Log.d("DiscountActivity", "Size of allDiscounts: " + allDiscounts.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(requireContext(), "Lỗi khi đọc dữ liệu từ Firebase", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setPositiveButton("Xác nhận", (dialog, which) -> {
            DiscountSelectionAdapter discountSelectionAdapter1 = (DiscountSelectionAdapter) recyclerView.getAdapter();
            selectedDiscounts = discountSelectionAdapter1.getSelectedDiscountIds();
            showSelectedDiscounts(selectedDiscounts);
        });

        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showSelectedDiscounts(List<Discount> selectedDiscountIds) {
        rvMutilpeDiscount.setLayoutManager(new LinearLayoutManager(requireContext()));
        DiscountAdapter selectedDiscountsAdapter = new DiscountAdapter(new ArrayList<>());
        rvMutilpeDiscount.setAdapter(selectedDiscountsAdapter);
        selectedDiscountsAdapter.updateDiscountList(selectedDiscountIds);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_IMAGES && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                ClipData clipData = data.getClipData();
                if (clipData != null) {
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        Uri imageUri = clipData.getItemAt(i).getUri();
                        if (imageUri != null) {
                            selectedImageUris.add(imageUri);
                        }
                    }
                } else {
                    Uri imageUri = data.getData();
                    if (imageUri != null) {
                        selectedImageUris.add(imageUri);
                    }
                }
                if (adapter != null) {
                    adapter.setImageList(selectedImageUris);
                }
            }
        }
    }

    public void callOnActivityResult(int requestCode, int resultCode, Intent data) {
        onActivityResult(requestCode, resultCode, data);
    }

    private void showDialogColor() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater layoutInflater = LayoutInflater.from(requireContext());

        View view = layoutInflater.inflate(R.layout.dialog_color, null);
        builder.setView(view);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        GridLayout gridLayout = view.findViewById(R.id.grid);
        updateRecyclerView(view, getColor(selectedColorProducts));

        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            final ImageButton button = (ImageButton) gridLayout.getChildAt(i);
            final int color = ((ColorDrawable) button.getBackground()).getColor();
            button.setOnClickListener(v -> {
                if (!checkColorExist(color)) {
                    selectedColorProducts.add(new ColorProduct(color));
                } else {
                    removeColor(color);
                }
                updateRecyclerView(view, getColor(selectedColorProducts));
            });
        }
        Button addButton = view.findViewById(R.id.b21);
        addButton.setOnClickListener(v -> {
            mAdapter.updateSelectedColors(selectedColorProducts, selectedSize);
            alertDialog.dismiss();
        });
    }

    private void removeColor(int color) {
        for (ColorProduct cl: selectedColorProducts) {
            if (cl.getColor() == color) {
                selectedColorProducts.remove(cl);
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
        for (ColorProduct cl : selectedColorProducts) {
            if (cl.getColor() == color) {
                return true;
            }
        }
        return false;
    }

    private void updateRecyclerView(View view, List<Integer> selectedColors) {
        RecyclerView recyclerView = view.findViewById(R.id.rvChosseCL);
        ColorAdapter colorAdapter = new ColorAdapter(selectedColors);
        recyclerView.setAdapter(colorAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    private void saveProductToRealtimeDatabase() {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Vui lòng đợi...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference productsRef = database.getReference("products");
        storageReference = FirebaseStorage.getInstance().getReference();

        String productId = productsRef.push().getKey();

        Title = String.valueOf(edtTitle.getText()).trim();
        Price = Integer.parseInt(String.valueOf(edtPrice.getText()).trim());
        Des = String.valueOf(edtDes.getText()).trim();
        Brand = String.valueOf(edtBrand.getText()).trim();

        String selectedProductType = (String) sizeSpinner.getSelectedItem();

        Product.ProductType productType = Product.ProductType.valueOf(selectedProductType);
        List<String> imageUrls = new ArrayList<>();
        List<String> imageUriStrings = new ArrayList<>();

        for (Uri imageUri : selectedImageUris) {
            String imageName = "product_images/" + productId + "/" + imageUri.getLastPathSegment();
            StorageReference imageRef = storageReference.child(imageName);
            UploadTask uploadTask = imageRef.putFile(imageUri);
            imageUriStrings.add(imageUri.toString());

            uploadTask.addOnSuccessListener(taskSnapshot -> {
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    imageUrls.add(imageUrl);

                    if (imageUrls.size() == selectedImageUris.size()) {
                        String userId = firebaseAuth.getCurrentUser().getUid();
                        quantity = selectedColorProducts.stream().map(product -> product.getQuantity().stream().reduce(0,Integer::sum)).reduce(0,Integer::sum).intValue();
                        Product product = new Product(
                                productId, userId, Title, productType,
                                "categoryID", Brand, Des, imageUrls, selectedColorProducts, 0, "ngon", quantity, (double) Price, selectedSize,  selectedDiscounts
                        );
                        product.setUserProduct(true);

                        productsRef.child(productId).setValue(product);

                        progressDialog.dismiss();
                        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                        fragmentManager.popBackStack();

                        ((ManagerProductActivity) requireActivity()).showFloatingActionButton();
                        isAddingProduct = false;
                    }
                });
            });
        }
        Toast.makeText(context, "Thêm sản phẩm thành công!!", Toast.LENGTH_SHORT).show();
    }

    private boolean validateInput() {
        String title = edtTitle.getText().toString().trim();
        String priceString = edtPrice.getText().toString().trim();
        String brand = edtBrand.getText().toString().trim();
        String description = edtDes.getText().toString().trim();

        if (title.isEmpty()) {
            showToast("Tiêu đề không được để trống");
            return false;
        }

        if (priceString.isEmpty()) {
            showToast("Giá không được để trống");
            return false;
        }
        if (brand.isEmpty()) {
            showToast("Hãng không được để trống");
            return false;
        }
        if (description.isEmpty()) {
            showToast("Des4 không được để trống");
            return false;
        }
        if (selectedImageUris.isEmpty()) {
            showToast("Bạn cần chọn ít nhất một ảnh");
            return false;
        }

        if (selectedColorProducts.isEmpty()) {
            showToast("Bạn cần chọn ít nhất một màu");
            return false;
        }


        return true;
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }


}
