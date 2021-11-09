package com.example.rouminder.firebase;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.rouminder.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;

import java.text.SimpleDateFormat;
import java.util.Date;

// Singleton 패턴으로 구현되어, 인스턴스에 접근하려면 항상 Manager.getInstance()를 호출해야 함
public class Manager {
    private static Manager instance = new Manager();

    private static Long nextActionId = null;

    private FirebaseDatabase db;
    private DatabaseReference rootRef;
    private DatabaseReference userRef;
    private DatabaseReference actionRef;
    private DatabaseReference conditionRef;
    private DatabaseReference goalRef;
    private DatabaseReference categoryRef;

    private Manager() {
        db = FirebaseDatabase.getInstance();
        userRef = db.getReference("user");
        actionRef = db.getReference("action");
        conditionRef = db.getReference("condition");
        goalRef = db.getReference("goal");
        categoryRef = db.getReference("category");
    }

    public String getRandomId() {
        String timeMills = Long.toString(System.currentTimeMillis());
        int salt = (int)(Math.random() * 10000);
        String stringSalt = String.format("%04d", salt);
        return timeMills + stringSalt;
    }

    public static Manager getInstance() {
        return instance;
    }

    private static String getTimeStampString() {
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd/HH:mm:ss");
        return format.format(now);
    }

    public void createUser(String uid) {
        String loggedOn = getTimeStampString();
        userRef.child(uid).child("last_login").setValue(loggedOn);
    }

    public String createAction(String uid, String type, String unit) {
        String created_at = getTimeStampString();

        String randomId = getRandomId();

        actionRef.child("data").child(randomId).child("author").setValue(uid);
        actionRef.child("data").child(randomId).child("created_at").setValue(created_at);
        actionRef.child("data").child(randomId).child("type").setValue(type);
        actionRef.child("data").child(randomId).child("unit").setValue(unit);
        actionRef.child("data").child(randomId).child("modified_at").setValue("");

        return randomId;
    }

    public String createCondition(String uid, String actionId, String condType) {
        String created_at = getTimeStampString();
        String randomId = getRandomId();

        conditionRef.child("data").child(randomId).child("author").setValue(uid);
        conditionRef.child("data").child(randomId).child("action").setValue(actionId);
        conditionRef.child("data").child(randomId).child("created_at").setValue(created_at);
        conditionRef.child("data").child(randomId).child("type").setValue(condType);
        conditionRef.child("data").child(randomId).child("modified_at").setValue("");

        return randomId;
    }

    public String createGoal(String uid, String conditionId, String categoryId, String goalName) {
        String created_at = getTimeStampString();
        String randomId = getRandomId();

        goalRef.child("data").child(randomId).child("author").setValue(uid);
        goalRef.child("data").child(randomId).child("goal").setValue(conditionId);
        goalRef.child("data").child(randomId).child("created_at").setValue(created_at);
        goalRef.child("data").child(randomId).child("name").setValue(goalName);
        goalRef.child("data").child(randomId).child("category").setValue(categoryId);
        goalRef.child("data").child(randomId).child("modified_at").setValue("");

        return randomId;
    }

    public String createCategory(String uid, String categoryName) {
        String created_at = getTimeStampString();
        String randomId = getRandomId();

        categoryRef.child("data").child(randomId).child("author").setValue(uid);
        categoryRef.child("data").child(randomId).child("name").setValue(categoryName);
        categoryRef.child("data").child(randomId).child("created_at").setValue(created_at);
        categoryRef.child("data").child(randomId).child("modified_at").setValue("");

        return randomId;
    }
}
