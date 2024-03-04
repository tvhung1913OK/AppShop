package com.example.duantotnghiep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

import com.example.duantotnghiep.databinding.ActivityAdminBinding;
import com.example.duantotnghiep.databinding.ActivityMainBinding;
import com.example.duantotnghiep.fragment.CartFragment;
import com.example.duantotnghiep.fragment.HomeFragment;
import com.example.duantotnghiep.fragment.NotificationFragment;
import com.example.duantotnghiep.fragment.ProfileFragment;
import com.example.duantotnghiep.fragment.SearchProductFragment;

public class AdminActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1;

    private ActivityAdminBinding binding;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fragmentManager = getSupportFragmentManager();
        HomeFragment homeFragment = new HomeFragment();
        fragmentManager.beginTransaction().replace(R.id.frame_layout_admin, homeFragment).commit();
        binding.bottomNavigationViewAdmin.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.home) {
                replaceFragment(new HomeFragment());
            }else if (id == R.id.profile) {
                replaceFragment(new ProfileFragment());
            } else if (id == R.id.search) {
                replaceFragment(new SearchProductFragment());
            } else if (id==R.id.notification) {
                replaceFragment(new NotificationFragment());
            } else {
                replaceFragment(new HomeFragment());
            }
            return true;
        });
//        requestPermissions();
    }

    private void replaceFragment(Fragment fragment) {
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_admin, fragment);
        fragmentTransaction.commit();
    }

    private void requestPermissions() {
        boolean cameraPermissionGranted = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean storagePermissionGranted = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        if (!cameraPermissionGranted || !storagePermissionGranted) {
            ActivityCompat.requestPermissions(AdminActivity.this, new String[]{android.Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        } else {
            // Quyền đã được cấp, tiếp tục xử lý
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean cameraPermissionGranted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            boolean storagePermissionGranted = grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED;

            if (!cameraPermissionGranted || !storagePermissionGranted) {
                showPermissionDeniedDialog();
            } else {
                // Quyền đã được cấp, tiếp tục xử lý
            }
        }
    }

    private void showPermissionDeniedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminActivity.this);
        builder.setTitle("Quyền truy cập bị từ chối");
        builder.setMessage("Ứng dụng không thể hoạt động mà không có quyền truy cập. Vui lòng cấp quyền trong cài đặt.");
        builder.setPositiveButton("Đi đến cài đặt", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openAppSettings();
            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, PERMISSION_REQUEST_CODE);
    }
}