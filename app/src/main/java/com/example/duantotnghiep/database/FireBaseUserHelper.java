package com.example.duantotnghiep.database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FireBaseUserHelper {
    private DatabaseReference databaseReference;

    public FireBaseUserHelper(){
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public DatabaseReference getUsersRef(){
        return databaseReference.child("user");
    }


}
