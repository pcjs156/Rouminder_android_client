package com.example.rouminder.firebase.manager;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.StringTokenizer;

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
        timeMills = timeMills.substring(6);

        int salt = (int) (Math.random() * 100);
        String stringSalt = String.format("%02d", salt);
        return timeMills + stringSalt;
    }

    public static DateTimeFormatter getShortTimeFormatter() {
        return DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
    }

    public static DateTimeFormatter getLongTimeFormatter() {
        return DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
    }

    public static String getTimeStampString(LocalDateTime dt) {
        return dt.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
    }

    public static String getTimeStampString() {
        LocalDateTime now = LocalDateTime.now();
        return getTimeStampString(now);
    }

    public static String getTimeStampString(int year, int month, int day, int hour, int minute) {
        LocalDateTime dt = LocalDateTime.of(year, month, day, hour, minute);
        return getTimeStampString(dt);
    }

    public static String getTimeStampString(int year, int month, int day) {
        LocalDateTime dt = LocalDateTime.of(year, month, day, 0, 0);
        return getTimeStampString(dt);
    }

    public static LocalDateTime parseTimeStampString(String dtString) throws ParseException {
        return LocalDateTime.parse(dtString, DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
    }

    // "hh:mm" 형태의 문자열을 입력받아 0년 0월 0일 hh시 00분의 LocalDatetime을 바환
    public static LocalDateTime parseTimeString(String timeString) {
        StringTokenizer tokenizer = new StringTokenizer(timeString, ":");
        int hour = Integer.parseInt(tokenizer.nextToken());
        int min = Integer.parseInt(tokenizer.nextToken());
        LocalDateTime ret = LocalDateTime.of(0, 0, 0, hour, min);
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
