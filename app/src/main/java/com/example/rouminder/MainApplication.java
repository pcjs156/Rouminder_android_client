package com.example.rouminder;

import com.example.rouminder.data.goalsystem.CheckGoal;
import com.example.rouminder.data.goalsystem.CountGoal;
import com.example.rouminder.data.goalsystem.GoalManager;
import com.example.rouminder.helpers.GoalNotificationHelper;

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
        initGoalNotificationHelper();
        initGoalManager();
    }

    private void initGoalNotificationHelper() {
        goalNotificationHelper.createNotificationChannel();
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
        loadGoalManager();
    }

    private void loadGoalManager() {
        // testing setup
        LocalDateTime today = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);

        goalManager.addGoal(new CheckGoal(goalManager, -1, "밥 먹기",
                today.withHour(8),
                today.withHour(12), 0));
        goalManager.addGoal(new CountGoal(goalManager, -1, "물 마시기",
                today.withHour(8).plusMinutes(1),
                today.withHour(12).plusMinutes(1), 1, 5, "회"));
        goalManager.addGoal(new CheckGoal(goalManager, -1, "한강 가기",
                today.withHour(16),
                today.withHour(18), 1));
    }

    public GoalManager getGoalManager() {
        return goalManager;
    }
}