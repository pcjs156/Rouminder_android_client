package com.example.rouminder.services;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.example.rouminder.Application;
import com.example.rouminder.data.goalsystem.Goal;
import com.example.rouminder.helpers.GoalNotificationHelper;


public class NotifyIntentService extends JobIntentService {
    public static final int JOB_ID = 1;

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, NotifyIntentService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        int id = intent.getIntExtra("goal_id", -1);
        Goal goal = ((Application) getApplication()).getGoalManager().getGoal(id);
        GoalNotificationHelper.showNotification(this, goal);
    }
}
