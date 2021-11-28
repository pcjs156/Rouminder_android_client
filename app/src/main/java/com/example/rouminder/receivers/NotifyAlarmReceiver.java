package com.example.rouminder.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.JobIntentService;

import com.example.rouminder.services.NotifyIntentService;

public class NotifyAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, NotifyIntentService.class);
        serviceIntent.putExtra(Intent.EXTRA_INTENT, intent);
        NotifyIntentService.enqueueWork(context, serviceIntent);
        Log.d("alarm", "received");
    }
}
