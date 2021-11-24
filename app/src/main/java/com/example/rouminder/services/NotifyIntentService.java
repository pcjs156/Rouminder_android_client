package com.example.rouminder.services;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.example.rouminder.MainApplication;
import com.example.rouminder.data.goalsystem.Goal;
import com.example.rouminder.helpers.GoalNotificationHelper;


public class NotifyIntentService extends JobIntentService {

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        int id = intent.getIntExtra("goal_id", -1);
        Goal goal = ((MainApplication) getApplication()).getGoalManager().getGoal(id);
        GoalNotificationHelper.showNotification(this, goal);
    }
}
