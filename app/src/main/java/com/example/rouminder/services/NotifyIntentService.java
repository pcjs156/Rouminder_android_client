package com.example.rouminder.services;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.JobIntentService;

import com.example.rouminder.MainApplication;
import com.example.rouminder.data.goalsystem.Goal;
import com.example.rouminder.helpers.GoalNotificationHelper;
import com.example.rouminder.helpers.GoalNotificationHelper.NotificationType;


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
        String typeName = intent.getStringExtra("notify_type");
        NotificationType type = NotificationType.valueOf(typeName);
        MainApplication application = (MainApplication) getApplication();
        Goal goal = application.getGoalManager().getGoal(id);
        application.getGoalNotificationHelper().setNotification(goal, type);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("notify", "ended");
    }
}
