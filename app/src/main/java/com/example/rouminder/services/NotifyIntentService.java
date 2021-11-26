package com.example.rouminder.services;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.example.rouminder.MainApplication;
import com.example.rouminder.data.goalsystem.Goal;
import com.example.rouminder.helpers.GoalNotificationHelper;


public class NotifyIntentService extends JobIntentService {
    private static final int JOB_ID = 2002;

    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, NotifyIntentService.class, JOB_ID, intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("notify", "started");
    }

    @Override
    protected void onHandleWork(@NonNull Intent serviceIntent) {
        Intent intent = serviceIntent.getParcelableExtra(Intent.EXTRA_INTENT);
        int id = intent.getIntExtra("goal_id", -1);
        MainApplication application = (MainApplication) getApplication();
        Goal goal = application.getGoalManager().getGoal(id);
        application.getGoalNotificationHelper().showNotification(goal);
        Log.d("notify", "handle id " + id);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("notify", "ended");
    }
}
