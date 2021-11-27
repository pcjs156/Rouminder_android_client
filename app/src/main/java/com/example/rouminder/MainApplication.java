package com.example.rouminder;

import android.app.AlarmManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.example.rouminder.data.goalsystem.CheckGoal;
import com.example.rouminder.data.goalsystem.CountGoal;
import com.example.rouminder.data.goalsystem.GoalManager;
import com.example.rouminder.helpers.GoalNotificationHelper;
import com.example.rouminder.receivers.RenewAlarmReceiver;
import com.example.rouminder.services.RenewGoalService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class MainApplication extends android.app.Application {
    private final GoalManager goalManager;
    private final GoalNotificationHelper goalNotificationHelper;

    public MainApplication() {
        goalManager = new GoalManager();
        goalNotificationHelper = new GoalNotificationHelper(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initGoalManager();
        initGoalNotificationHelper();
        initReceivers();
    }

    private void initGoalNotificationHelper() {
        goalNotificationHelper.createNotificationChannel();
        goalManager.getGoals().forEach(goalNotificationHelper::registerGoal);
        bindGoalManagerToNotification();
    }

    private void bindGoalManagerToNotification() {
        goalManager.setOnGoalChangeListener(goalManager.new OnGoalChangeListener() {
            @Override
            public void onGoalAdd(int id) {
                goalNotificationHelper.registerGoal(goalManager.getGoal(id));
            }

            @Override
            public void onGoalUpdate(int id) {
                goalManager.renewGoals(LocalDateTime.now());
                goalNotificationHelper.unregisterGoal(id);
                goalNotificationHelper.registerGoal(goalManager.getGoal(id));
            }

            @Override
            public void onGoalRemove(int id) {
                goalNotificationHelper.unregisterGoal(id);
            }
        });
    }

    private void initGoalManager() {
        goalManager.setOnGoalChangeListener(goalManager.new OnGoalChangeListener() {
            @Override
            public void onGoalAdd(int id) {
                Log.d("goal_event", "add " + id + " " + goalManager.getGoal(id).getName());
            }

            @Override
            public void onGoalUpdate(int id) {
                Log.d("goal_event", "update " + id+ " " + goalManager.getGoal(id).getName());
            }

            @Override
            public void onGoalRemove(int id) {
                Log.d("goal_event", "remove " + id+ " " + goalManager.getGoal(id).getName());
            }
        });
//        loadGoalManager();
    }

    private void loadGoalManager() {
        // testing setup
        LocalDateTime today = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);

        goalManager.addGoal(new CheckGoal(goalManager, -1, "밥 먹기",
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusMinutes(2),
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusMinutes(12), 0));
        goalManager.addGoal(new CountGoal(goalManager, -1, "물 마시기",
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).minusMinutes(10),
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusMinutes(3), 1, 5, "회"));
        goalManager.addGoal(new CheckGoal(goalManager, -1, "한강 가기",
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).minusMinutes(5),
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusMinutes(8), 1));
    }

    private void initReceivers() {
        registerReceiver(new RenewAlarmReceiver(), new IntentFilter(Intent.ACTION_TIME_TICK));
        RenewGoalService.enqueueWork(this, new Intent());

        // test

    }

    public GoalManager getGoalManager() {
        return goalManager;
    }

    public GoalNotificationHelper getGoalNotificationHelper() { return goalNotificationHelper; }
}
