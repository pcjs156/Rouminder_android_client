package com.example.rouminder.data.goalsystem.goal;

import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.example.rouminder.data.goalsystem.GoalManager;
import com.example.rouminder.data.goalsystem.action.BaseDataFormat;
import com.example.rouminder.data.goalsystem.condition.Condition;
import com.example.rouminder.data.goalsystem.action.Action;

import java.time.LocalDateTime;

/**
 * Abstract class for Goal, which is responsible for content part of 'Goal Feature'.
 */
public abstract class Goal implements Comparable<Goal> {
    private final GoalManager manager;
    private final int conditionID;
    private int goalID;
    private String name;


    /**
     * Initialize Goal with given parameters.
     *
     * @param manager     a GoalManager object to be handling, or null when for temporary use
     * @param goalID      an ID of the Goal.
     * @param name        a name of the Goal.
     * @param conditionID an ID of Condition associated with the Goal.
     */
    public Goal(@Nullable GoalManager manager, int goalID, String name, int conditionID) {
        this.manager = manager;
        this.goalID = goalID;
        this.name = name;
        this.conditionID = conditionID;
    }

    public int getID() {
        return goalID;
    }

    void setID(int goalID) {
        this.goalID = goalID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Condition<?> getCondition() {
        return manager == null ? null : manager.getCondition(conditionID);
    }

    public GoalInstance createInstance(LocalDateTime from, LocalDateTime to) {
        GoalInstance goalInstance = new GoalInstance(manager,
                -1,
                getID(),
                getCondition().createInstance().getID(),
                from,
                to);

        if (manager == null) {
            return goalInstance;
        } else if (manager.addGoalInstance(goalInstance)) {
            return goalInstance;
        } else {
            return null;
        }

    }

    @Override
    public int compareTo(Goal goal) {
        return Integer.compare(getID(), goal.getID());
    }
}
