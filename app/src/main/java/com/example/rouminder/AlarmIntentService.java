package com.example.rouminder;

import android.app.IntentService;
import android.content.Intent;

import com.example.rouminder.activities.LoginActivity;

public class AlarmIntentService extends IntentService {
    public AlarmIntentService() {
        super("AlarmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        new Intent(this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }
}