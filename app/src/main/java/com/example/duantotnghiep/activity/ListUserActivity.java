package com.example.duantotnghiep.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duantotnghiep.AdminActivity;
import com.example.duantotnghiep.MainActivity;
import com.example.duantotnghiep.R;
import com.example.duantotnghiep.adapter.UserAdapter;
import com.example.duantotnghiep.database.FireBaseUserHelper;
import com.example.duantotnghiep.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ListUserActivity extends AppCompatActivity implements UserAdapter.Callback{
    private FireBaseUserHelper firebaseHelper = new FireBaseUserHelper();
    private DatabaseReference usersRef;
    private SearchView edSearachUser;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private RecyclerView recyclerView;
    private List<User> list = new ArrayList<>();
    private User user;
    private ImageButton btnAdduser;
    private UserAdapter adapter;
    private AppCompatButton btnSave, btnCancle;
    private TextInputEditText edName, edEmail, edPhone, edPass, edRepass;
    private Spinner spinnerRole;
    private Dialog dialogUser;
    private ImageButton btnAddUser;
    private String role;
    private ImageView imgUser, imgBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_user);
        recyclerView = findViewById(R.id.rcy_list_user);
        imgBack = findViewById(R.id.imgBack);
        btnAdduser=findViewById(R.id.btn_add_user);
        edSearachUser = findViewById(R.id.edsearchUser);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new UserAdapter(getApplicationContext(), list, this);
        recyclerView.setAdapter(adapter);
        CallApiUser();
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        edSearachUser.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                performSearch(newText);
                return true;
            }
        });
        btnAdduser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenDialogUser(ListUserActivity.this,0,user);
            }
        });
    }
    private void CallApiUser() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("user");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (list != null) {
                    list.clear();
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (!user.getId().equals("ZYA1yQdRAYSzh1K24ZVYIYvHIc92")) {
                        list.add(user);
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ListUserActivity.this, "Get Fail !!!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void itemUserInfo(User user) {
            OpenDialogUser(ListUserActivity.this,1,user);

    }
    void OpenDialogUser(Context context, int type, User user) {

        dialogUser = new Dialog(context);
        Window window = dialogUser.getWindow();
        if (window == null) {
            return;
        }
        dialogUser.setContentView(R.layout.dialog_user);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        window.setAttributes(windowAttributes);

        imgUser = dialogUser.findViewById(R.id.imgUser);
        edName = dialogUser.findViewById(R.id.edNameUser);
        edEmail = dialogUser.findViewById(R.id.edEmailUser);
        edPhone = dialogUser.findViewById(R.id.edPhoneNumber);
        edPass = dialogUser.findViewById(R.id.edPassword);
        edRepass = dialogUser.findViewById(R.id.edtRePassword);
        btnAddUser = dialogUser.findViewById(R.id.btn_add_user);
        btnCancle = dialogUser.findViewById(R.id.btnCancle);
        btnSave = (AppCompatButton) dialogUser.findViewById(R.id.btnSave);
        spinnerRole = dialogUser.findViewById(R.id.spnRole);


        ArrayList<String> listRole = new ArrayList<>();
        listRole.add("Lock");
        listRole.add("Unlock");



        ArrayAdapter<String> arrayAdapterRole = new ArrayAdapter<>(this, R.layout.spinner_item, listRole);
        arrayAdapterRole.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(arrayAdapterRole);
        spinnerRole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                role = arrayAdapterRole.getItem(i).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogUser.dismiss();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edName.getText().toString().trim();
                String email = edEmail.getText().toString().trim();
                String phone = edPhone.getText().toString().trim();
                String password = edPass.getText().toString().trim();
                String repassword = edRepass.getText().toString().trim();
                if (validateRegistration(name, email, phone, password, repassword)) {
                    if (type == 0) {
                        firebaseAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            firebaseUser = firebaseAuth.getCurrentUser();
                                            String id = firebaseUser.getUid();
                                            User user = new User();
                                            user.setId(id);
                                            user.setUsername(name);
                                            user.setImg("https://media.istockphoto.com/id/1300845620/vector/user-icon-flat-isolated-on-white-background-user-symbol-vector-illustration.jpg?s=612x612&w=0&k=20&c=yBeyba0hUkh14_jgv1OKqIH0CCSWU_4ckRkAoy2p73o=");
                                            user.setEmail(email);
                                            user.setPhone(phone);
                                            user.setPassword(password);
                                            user.setUser_type(false);
                                            user.setWallet(0.0);
                                            boolean isUserLocked = role.equals("Lock");
                                            user.setLock(isUserLocked);
                                            usersRef = firebaseHelper.getUsersRef();


                                            if (role == "Unlock") {
                                                user.setLock(false);
                                            } else {
                                                user.setLock(true);
                                            }
                                            usersRef = firebaseHelper.getUsersRef();

                                            usersRef.child(id).setValue(user)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(context, "Thêm thành công", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                Toast.makeText(context, "Thêm thất bại!", Toast.LENGTH_SHORT).show();
                                                                Log.d("===TAG", "onComplete: ");
                                                            }
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        } else {
                                            Toast.makeText(context, "Thêm thất bại!", Toast.LENGTH_SHORT).show();
                                            Log.d("===TAG", "onComplete: ");
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        user.setUsername(name);
                        user.setEmail(email);
                        user.setPhone(phone);
                        user.setUser_type(false);
                        user.setImg("https://media.istockphoto.com/id/1300845620/vector/user-icon-flat-isolated-on-white-background-user-symbol-vector-illustration.jpg?s=612x612&w=0&k=20&c=yBeyba0hUkh14_jgv1OKqIH0CCSWU_4ckRkAoy2p73o=");
                        user.setWallet(0.0);
                        boolean idLock = (spinnerRole.getSelectedItemPosition() == 0);
                        user.setLock(idLock);
                        UpDateUser(user);
                        dialogUser.dismiss();
                    }
                    dialogUser.dismiss();
                }
            }
        });
        if (type == 1) {
            if (user.getImg() == null || user.getImg().isEmpty()) {
                imgUser.setImageResource(R.drawable.baseline_person_24);
            } else {
                Picasso.get().load(user.getImg()).into(imgUser);
            }
            edName.setText(user.getUsername());
            edEmail.setText(user.getEmail());
            edEmail.setEnabled(false);
            edPhone.setText(user.getPhone());
//            edAddress.setText(user.getAddress());
            edPass.setText(user.getPassword());
            edRepass.setText(user.getPassword());
            edPass.setVisibility(View.GONE);
            edRepass.setVisibility(View.GONE);
            btnSave.setText("UPDATE");
            if (user.getLock() == true) {
                spinnerRole.setSelection(1);
            } else {
                spinnerRole.setSelection(0);
            }
        }
        if (!isFinishing()) {
            dialogUser.show();
        }
    }
    private void UpDateUser(User user) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("user");

        String id = user.getId();
        if (id != null) {
            myRef.child(id).setValue(user, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    if (error == null) {
                        Toast.makeText(getApplicationContext(), "Update người dùng thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Update thất bại", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "ID NULL", Toast.LENGTH_SHORT).show();
        }

    }




    private boolean validateRegistration(String username, String email, String phone, String password, String repass) {
        String nameForm1 = "^[a-zA-Z]+$";
        String nameForm2 = "^[\\p{L}\\s]+$";

        if (username.isEmpty() || username == null) {
            edName.setError("Vui lòng nhập tên");
            return false;
        }
        if (!username.matches(nameForm1) || !username.matches(nameForm2)) {
            edName.setError("Tên không đúng định dạng!");
            return false;
        }
        if (TextUtils.isEmpty(email)) {
            edEmail.setError("Vui lòng nhập email");
            return false;
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        if (!email.matches(emailRegex)) {
            edEmail.setError("Email không hợp lệ");
            return false;
        }
        if (TextUtils.isEmpty(phone)) {
            edPhone.setError("Vui lòng nhập số điện thoại");
            return false;
        }
        String phoneRegex = "^[0-9]{10}$";
        if (!phone.matches(phoneRegex)) {
            edPhone.setError("Số điện thoại không hợp lệ");
            return false;
        }
        if (password.isEmpty()) {
            edPass.setError("Vui lòng nhập mật khẩu");
            return false;
        }
        if (password.length() < 6) {
            edPass.setError("Vui lòng nhập mật khẩu hơn 6 kí tự");
            return false;
        }
        if (!password.matches(repass)) {
            edRepass.setError("Mật khẩu nhập lại không trùng");
            return false;
        }
        return true;
    }
    private void performSearch(String query) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("user");
        Query searchQuery = myRef.orderByChild("email").startAt(query).endAt(query + "\uf8ff");
        searchQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        list.add(user);
                    }
                }
                adapter.setData(list);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("loi", "onCancelled: " + databaseError.getMessage());
            }
        });
    }
}