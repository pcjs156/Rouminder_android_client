package com.example.rouminder.firebase.manager;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

// Singleton 패턴으로 구현되어, 인스턴스에 접근하려면 항상 Manager.getInstance()를 호출해야 함
public class BaseModelManager {
    protected static final BaseModelManager instance = new BaseModelManager();

    protected static String uid = "";
    private static boolean isUidInitialized = false;

    protected FirebaseDatabase db;
    private DatabaseReference userRef;

    private BaseModelManager() {
        db = FirebaseDatabase.getInstance();
        userRef = db.getReference("user");
    }

    public static String getRandomId() {
        String timeMills = Long.toString(System.currentTimeMillis());
        int salt = (int) (Math.random() * 10000);
        String stringSalt = String.format("%04d", salt);
        return timeMills + stringSalt;
    }

    public static String getTimeStampString(Date dt) {
        SimpleDateFormat format = new SimpleDateFormat("20yy.MM.dd/HH:mm:ss");
        return format.format(dt);
    }

    public static String getTimeStampString() {
        Date now = new Date();
        return getTimeStampString(now);
    }

    public static String getTimeStampString(int year, int month, int day, int hour, int minute) {
        Date dt = new Date(year, month, day, hour, minute);
        return getTimeStampString(dt);
    }

    public static String getTimeStampString(int year, int month, int day) {
        Date dt = new Date(year, month, day, 0, 0);
        return getTimeStampString(dt);
    }

    public static Date parseTimeStampString(String dtString) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd/HH:mm:ss");
        Date ret = null;
        ret = format.parse(dtString);
        return ret;
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

    public static void checkUidInitialized() {
        if (!isUidInitialized || uid.isEmpty())
            throw new RuntimeException("uid가 초기화되지 않았습니다.");
    }

    public static void setUid(String uid) {
        BaseModelManager.uid = uid;
        isUidInitialized = true;
    }

    public static void readData(Query query, final OnGetDataListener listener) {
        listener.onStart();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onFailure();
            }
        });
    }

    public String getUid() {
        return uid;
    }
}
