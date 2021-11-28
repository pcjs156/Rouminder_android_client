package com.example.rouminder.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.rouminder.services.RenewGoalService;

public class RenewAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, RenewGoalService.class);
        serviceIntent.putExtra(Intent.EXTRA_INTENT, intent);
        RenewGoalService.enqueueWork(context, serviceIntent);
    }
}
