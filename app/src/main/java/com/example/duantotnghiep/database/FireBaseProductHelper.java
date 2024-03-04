package com.example.duantotnghiep.database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FireBaseProductHelper {
    private DatabaseReference databaseReference;

    public FireBaseProductHelper(){
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public DatabaseReference getProductRef(){
        return databaseReference.child("product");
    }
}
