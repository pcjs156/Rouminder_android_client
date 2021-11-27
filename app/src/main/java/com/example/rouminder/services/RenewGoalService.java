package com.example.rouminder.services;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.example.rouminder.MainApplication;
import com.example.rouminder.data.goalsystem.Goal;
import com.example.rouminder.data.goalsystem.GoalManager;
import com.example.rouminder.helpers.GoalNotificationHelper;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class RenewGoalService extends JobIntentService {
    private static final int JOB_ID = 2003;

    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, RenewGoalService.class, JOB_ID, intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("renew", "started");
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        GoalManager goalManager = ((MainApplication)getApplication()).getGoalManager();
        GoalNotificationHelper goalNotificationHelper = (((MainApplication) getApplication()).getGoalNotificationHelper());
        Set<Goal> previousOngoingGoals = new HashSet<>(goalManager.getOngoingGoals());
        goalManager.renewGoals(LocalDateTime.now());
        Set<Goal> newOngoingGoals = new HashSet<>(goalManager.getOngoingGoals());
        // remove old
        previousOngoingGoals.stream()
                .filter(g->!newOngoingGoals.contains(g))
                .forEach(g->goalNotificationHelper.unsetNotification(g.getId(), GoalNotificationHelper.NotificationType.ONGOING));
        newOngoingGoals.forEach(g -> goalNotificationHelper.setNotification(g, GoalNotificationHelper.NotificationType.ONGOING));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("renew", "ended");
    }
}
