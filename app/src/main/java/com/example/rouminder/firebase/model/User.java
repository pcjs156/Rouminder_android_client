package com.example.rouminder.firebase.model;

import android.os.Parcel;
import android.os.Parcelable;

public class User {
    private String uid;

    private static final User instance = new User();

    private User() {
        this.uid = null;
    }

    public static User getInstance() {
        return instance;
    }

    public void setInfo(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }
}
