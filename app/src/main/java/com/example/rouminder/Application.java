package com.example.rouminder;

import com.example.rouminder.data.goalsystem.GoalManager;

public class Application extends android.app.Application {
    private final GoalManager goalManager = new GoalManager();

    public GoalManager getGoalManager() {
        return goalManager;
    }
}
