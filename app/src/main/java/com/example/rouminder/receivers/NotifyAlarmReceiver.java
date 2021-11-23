package com.example.rouminder.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.rouminder.services.NotifyIntentService;

public class NotifyAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotifyIntentService.enqueueWork(context, intent);
    }
}
