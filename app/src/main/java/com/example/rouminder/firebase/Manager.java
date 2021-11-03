package com.example.rouminder.firebase;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.rouminder.MainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// Singleton 패턴으로 구현되어, 인스턴스에 접근하려면 항상 Manager.getInstance()를 호출해야 함
public class Manager {
    private static Manager instance = new Manager();

    private FirebaseDatabase db;
    private DatabaseReference testRef;

    private Manager() {
        db = FirebaseDatabase.getInstance();
        testRef = db.getReference("test");
    }

    public static Manager getInstance() {
        return instance;
    }

    public void writeNewTestObject(String body) {
        TestObject obj = new TestObject(body);

        testRef.child(body).setValue(obj)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("FB", "성공");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("FB", "실패");
                    }
                });
    }
}
