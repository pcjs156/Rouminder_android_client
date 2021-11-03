package com.example.rouminder.firebase;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.rouminder.MainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

// Singleton 패턴으로 구현되어, 인스턴스에 접근하려면 항상 Manager.getInstance()를 호출해야 함
public class Manager {
    private static Manager instance = new Manager();

    private FirebaseDatabase db;
    private DatabaseReference userRef;

    private Manager() {
        db = FirebaseDatabase.getInstance();
        userRef = db.getReference("user");
    }

    public static Manager getInstance() {
        return instance;
    }

    public void writeUserInstance(String uid) {
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy.mm.dd/HH:mm:ss");
        String loggedOn = format.format(now);

        userRef.child(uid).child("last_login").setValue(loggedOn);
    }
}
