package com.example.rouminder.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.rouminder.services.RepeatPlanUpdateHandlerService;

public class RepeatPlanUpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, RepeatPlanUpdateHandlerService.class);
        serviceIntent.putExtra(Intent.EXTRA_INTENT, intent);
        RepeatPlanUpdateHandlerService.enqueueWork(context, intent);
    }
}
