package com.example.rouminder.firebase.manager;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

// Singleton 패턴으로 구현되어, 인스턴스에 접근하려면 항상 Manager.getInstance()를 호출해야 함
public class BaseModelManager {
    protected static final BaseModelManager instance = new BaseModelManager();

    protected static String uid = "";
    private static boolean isUidInitialized = false;

    protected FirebaseDatabase db;
    private DatabaseReference userRef;
    private DatabaseReference categoryRef;

    private BaseModelManager() {
        db = FirebaseDatabase.getInstance();
        userRef = db.getReference("user");
        categoryRef = db.getReference("category");
    }

    public String getRandomId() {
        String timeMills = Long.toString(System.currentTimeMillis());
        int salt = (int) (Math.random() * 10000);
        String stringSalt = String.format("%04d", salt);
        return timeMills + stringSalt;
    }

    public static String getTimeStampString() {
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd/HH:mm:ss");
        return format.format(now);
    }

    public static BaseModelManager getInstance() {
        checkUidInitialized();

        return instance;
    }

    public void createUser() {
        checkUidInitialized();

        String loggedOn = getTimeStampString();
        userRef.child(uid).child("last_login").setValue(loggedOn);
    }

    public String createCategory(String categoryName) {
        checkUidInitialized();

        String created_at = getTimeStampString();
        String randomId = getRandomId();

        categoryRef.child("data").child(randomId).child("author").setValue(uid);
        categoryRef.child("data").child(randomId).child("name").setValue(categoryName);
        categoryRef.child("data").child(randomId).child("created_at").setValue(created_at);
        categoryRef.child("data").child(randomId).child("modified_at").setValue("");

        return randomId;
    }

    public static void checkUidInitialized() {
        if (!isUidInitialized || uid.isEmpty())
            throw new RuntimeException("uid가 초기화되지 않았습니다.");
    }

    public static void setUid(String uid) {
        if (isUidInitialized)
            throw new RuntimeException("uid가 이미 초기화 되어 있습니다.");
        else {
            BaseModelManager.uid = uid;
            isUidInitialized = true;
        }
    }
}
